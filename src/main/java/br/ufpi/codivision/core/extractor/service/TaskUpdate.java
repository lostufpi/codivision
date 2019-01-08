package br.ufpi.codivision.core.extractor.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.tasks.Task;
import br.com.caelum.vraptor.tasks.scheduler.Scheduled;
import br.ufpi.codivision.common.notification.EmailDispatcher;
import br.ufpi.codivision.core.dao.AuthorDAO;
import br.ufpi.codivision.core.dao.GamePointsDAO;
import br.ufpi.codivision.core.dao.GamificationDAO;
import br.ufpi.codivision.core.dao.RepositoryDAO;
import br.ufpi.codivision.core.dao.RevisionDAO;
import br.ufpi.codivision.core.dao.UserDAO;
import br.ufpi.codivision.core.extractor.model.Extraction;
import br.ufpi.codivision.core.extractor.model.RepositoryCredentials;
import br.ufpi.codivision.core.model.Author;
import br.ufpi.codivision.core.model.DirTree;
import br.ufpi.codivision.core.model.GamePoints;
import br.ufpi.codivision.core.model.Gamification;
import br.ufpi.codivision.core.model.OperationFile;
import br.ufpi.codivision.core.model.Repository;
import br.ufpi.codivision.core.model.Revision;
import br.ufpi.codivision.core.model.TestFile;
import br.ufpi.codivision.core.model.User;
import br.ufpi.codivision.core.model.enums.NodeType;
import br.ufpi.codivision.core.repository.GitUtil;
import br.ufpi.codivision.core.util.DeleteDir;
import br.ufpi.codivision.core.util.Fuzzy;
import br.ufpi.codivision.core.util.Outliers;
import br.ufpi.codivision.debit.model.InfoTD;
import br.ufpi.codivision.debit.model.TDAuthor;
import br.ufpi.codivision.feature.common.model.Feature;
import br.ufpi.codivision.feature.java.algorithm.ControllerDefiner;
import br.ufpi.codivision.feature.java.algorithm.FeatureDefiner;
import br.ufpi.codivision.feature.java.graph.ClassGraphBuilder;
import br.ufpi.codivision.feature.java.model.Class;
import br.ufpi.codivision.feature.java.model.NodeInfo;


@Scheduled(fixedRate = 1000, concurrent = false)
public class TaskUpdate implements Task{


	@Inject private TaskService service;
	@Inject private EntityManagerFactory factory;
	@Inject private GamificationDAO gDAO;
	private RepositoryDAO dao;
	private AuthorDAO authorDAO;
	private GamePointsDAO gamePointsDAO;
	private GamificationDAO gameDAO;
	private RevisionDAO revDAO;
	

	private final Logger log = LoggerFactory.getLogger(getClass());


	public void execute() {
		EntityManager em = factory.createEntityManager();
		EntityTransaction transaction = em.getTransaction();
		dao = new RepositoryDAO();
		dao.setEntityManager(em);
		Extraction task = service.getFirstTaskUpdate();
		if(task != null) {
			log.info("TaskUpdate.execute()");
			Long[] array = task.getData();
			if (task.isFirst()) {
				update(task);
				array[0]=array[0]+1;
				array[1]=array[1]+1;
				array[2]=array[2]+1;
				array[3]=array[3]+1;
				task.setData(array);
				task.setForce(false);
				task.setFirst(false);
				service.addTaskUpdate(task);
				log.info("Task Realocada na queue");
			}
			else {
				if (task.isForce()) {
					update(task);
				}
				else {
					System.out.println(array[0] + " de " + array[4] + " para novo update");
					if (array[0].equals(array[4])) {//FAZ UPDATE OK
						array[0]=(long) 1;
						update(task);
					}
					else {
						array[0]=array[0]+1;
					}

					System.out.println(array[1] + " de " + array[5] + " para novas mensagens");
					if(array[1].equals(array[5])) {//MANDA EMAIL
						array[1]=(long) 1;
						if(!array[2].equals(array[6])) {//SE FOR PREMIAR NÃO ENVIA EMAIL, POIS SÃO EMAILS DA PREMIAÇÃO
							log.info("Email via timer de Email");
							sendMailGamification(task);
						}
					}
					else {
						array[1]=array[1]+1;
					}
					System.out.println(array[2] + " de " + array[6] + " para premiar");
					if(array[2].equals(array[6])) {//PREMIA
						log.info("Começando premiação");
						premia(task);
						array[2]=(long) 1;
						log.info("Premiação completa");
					}
					else {
						array[2]=array[2]+1;
					}
					if(array[3].equals(array[7])) {//ENCERRA GAMIFICATION OK
						transaction.begin();
						gDAO = new GamificationDAO();
						gDAO.setEntityManager(em);
						array[3]=(long) 1;
						Gamification game = gDAO.findById(task.getTarget());
						Repository rep = dao.findById(task.getTarget());
						rep.setGameId(false);
						dao.save(rep);
						game.setActive(false);
						gDAO.save(game);
					}
					else {//READICIONA AO ARRAY DE TASKS OK
						array[3]=array[3]+1;
						if(!dao.findById(task.getTarget()).haveGameId()) {
							return ;
						}
						task.setData(array);
						service.addTaskUpdate(task);
						
					}
				}
			}
		}
		transaction.commit();
		em.close();
	}
	
	private void sendMailGamification (Extraction task) {
		EntityManager em = factory.createEntityManager();
		EntityTransaction transaction = em.getTransaction();
		transaction.begin();
		dao = new RepositoryDAO();
		dao.setEntityManager(em);
		Repository repository = dao.findById(task.getTarget());
		List<Author> auths = new ArrayList<Author>();
		List<Revision> revs = repository.getRevisions();
		String name = "";
		String mail = "";
		Random random = new Random();
		
		
		for (Revision rev: revs) {
			if(!auths.contains(rev.getAuthor()) && rev.getAuthor().getAutFather() == null)
			{
				auths.add(rev.getAuthor());
				String msg = generateMessageGam(rev.getAuthor(), repository, random.nextBoolean());
				sendMail(msg, null, repository.getName(), rev.getAuthor().getEmail());
				
			}
		}
		transaction.commit();
		em.close();
	}
	
	private String generateMessageGam (Author auth, Repository repository, boolean bool) {
		String repName = repository.getName();
		String msg = "";
		Random random = new Random();
		if(bool) {//MENSAGEM BASEADA NO CICLO, ESTIMULO
			if (auth.getLastGamePointParam(auth.getGpValid()).getNumberOfLinesTest() == 0) {
				if (random.nextBoolean()) {
					msg = "<html><body>"
							+ "<p>Ola! "+auth.getName()+",</p>"
							+ "<p>O repositório "+repName+" precisa de você para garantir mais qualidade, não foi detectado neste ciclo sua contribuição para testes. :(</p>"
							+ "<p>Acesse a <a href='http://easii.ufpi.br/codivision/'>CODIVISION</a> para visualizar mais informações.</p>"
							+ "</body></html>";
				}
				else {
					msg = "<html><body>"
							+ "<p>Ola! "+auth.getName()+",</p>"
							+ "<p>Traga boas práticas para o seu repositório " + repName + ", faça sua contribuição com testes!</p>"
							+ "<p>Acesse a <a href='http://easii.ufpi.br/codivision/'>CODIVISION</a> para visualizar mais informações.</p>"
							+ "</body></html>";
				}
			}
			else {
				int lines = auth.getLastGamePointParam(auth.getGpValid()).getNumberOfLinesTest();
				if (random.nextBoolean()) {
					msg = "<html><body>"
							+ "<p>Ola! "+auth.getName()+",</p>"
							+ "<p>O repositório "+repName+" ainda precisa de você para garantir qualidade no produto final, suas " + lines + " linhas de teste contribuidas neste ciclo mostram seu trabalho junto as boas práticas. Continue contribuindo para uma solução ainda melhor! :)</p>"
							+ "<p>Acesse a <a href='http://easii.ufpi.br/codivision/'>CODIVISION</a> para visualizar mais informações.</p>"
							+ "</body></html>";
				}
				else {
					msg = "<html><body>"
							+ "<p>Ola! "+auth.getName()+",</p>"
							+ "<p>Suas "+lines+" contribuções em arquivos de teste trazem mais confiabilidade ao repositório " + repName + ". Continue ajudando! :D</p>"
							+ "<p>Acesse a <a href='http://easii.ufpi.br/codivision/'>CODIVISION</a> para visualizar mais informações.</p>"
							+ "</body></html>";
				}
			}
		}
		else {
			if (random.nextBoolean() || auth.getGpValid() == 0) {//COMPARA COM MELHOR AUTOR
				int maiorValorT = auth.getLastGamePointParam(auth.getGpValid()).getNumberOfLinesTest();
				Author maiorValA = auth; 
				List<Revision> revs = repository.getRevisions();
				List<Author> auths = new ArrayList<Author>();
				for (Revision rev: revs) {
					if(!auths.contains(rev.getAuthor()) && rev.getAuthor().getAutFather() == null)
					{
						auths.add(rev.getAuthor());
						GamePoints gp = rev.getAuthor().getLastGamePointParam(rev.getAuthor().getGpValid());
						if (gp.getNumberOfLinesTest() > maiorValorT) {
							maiorValorT = gp.getNumberOfLinesTest();
							maiorValA = rev.getAuthor();
						}
					}
				}
				if (maiorValorT != 0 && maiorValA.getId().equals(auth.getId())) {//ELE JÁ TEM O MAIOR VALOR (NÃO É ZERO)
					msg = "<html><body>"
							+ "<p>Ola! "+auth.getName()+",</p>"
							+ "<p>Parabéns! Durante este ciclo você está a frente em produção de testes no repositório " + repName + ". Já consegue ver a sua medalha? Continue ajudando! :D</p>"
							+ "<p>Acesse a <a href='http://easii.ufpi.br/codivision/'>CODIVISION</a> para visualizar mais informações.</p>"
							+ "</body></html>";
				} else {//ELE NÃO TEM O MAIOR VALOR
					if(maiorValorT == 0) {
						if(random.nextBoolean()) {
							msg = "<html><body>"
									+ "<p>Ola! "+auth.getName()+",</p>"
									+ "<p>O repositório "+repName+" precisa receber mais atenção. Não se esqueça de produzir testes, evitando falhas futuras!</p>"
									+ "<p>Acesse a <a href='http://easii.ufpi.br/codivision/'>CODIVISION</a> para visualizar mais informações.</p>"
									+ "</body></html>";
						}
						else {
							msg = "<html><body>"
									+ "<p>Ola! "+auth.getName()+",</p>"
									+ "<p>Evite surpresas, traga boas práticas para o seu repositório " + repName + ", faça sua contribuição com testes, é fundamental para garantir o bom funcionamento do projeto!</p>"
									+ "<p>Acesse a <a href='http://easii.ufpi.br/codivision/'>CODIVISION</a> para visualizar mais informações.</p>"
									+ "</body></html>";
						}

					}
					else {
						msg = "<html><body>"
								+ "<p>Ola! "+auth.getName()+",</p>"
								+ "<p>O(a) colega " + maiorValA.getName() + " está com os melhores resultados deste ciclo! Não desista, produza testes, garanta qualidade, busque a premiação de ouro! </p>"
								+ "<p>Acesse a <a href='http://easii.ufpi.br/codivision/'>CODIVISION</a> para visualizar mais informações.</p>"
								+ "</body></html>";
					}
				}
			}
			else {//COMPARA COM CICLO ANTERIOR
				GamePoints gp = auth.getLastGamePointParam(auth.getGpValid());
				GamePoints gpOld = auth.getLastGamePointParam(auth.getGpValid() -1);
				if (gp.getNumberOfLinesTest() > gpOld.getNumberOfLinesTest()) {//MAIS TESTE AGORA QUE CICLO ANTERIOR
					if (random.nextBoolean()) {
						msg = "<html><body>"
								+ "<p>Ola! "+auth.getName()+",</p>"
								+ "<p>Parabéns! Vimos que produziu mais testes nesse ciclo do que no anterior! Continue produzindo para evitar comportamentos inesperados no produto do repositório " + repName + "; :D</p>"
								+ "<p>Acesse a <a href='http://easii.ufpi.br/codivision/'>CODIVISION</a> para visualizar mais informações.</p>"
								+ "</body></html>";
					}
					else {
						msg = "<html><body>"
								+ "<p>Ola! "+auth.getName()+",</p>"
								+ "<p>Boas notícias! Seu empenho em produzir testes aumentou! Continue com empenho no repositório " + repName + ".</p>"
								+ "<p>Acesse a <a href='http://easii.ufpi.br/codivision/'>CODIVISION</a> para visualizar mais informações.</p>"
								+ "</body></html>";
					}
				}
				else {//MAIS TESTE NO CICLO ANTERIOR
					if (random.nextBoolean()) {
						msg = "<html><body>"
								+ "<p>Ola! "+auth.getName()+",</p>"
								+ "<p>Não se esqueça de testar o que é produzido! O seu desempenho na produção de testes caiu em relação ao último ciclo no repositório " + repName + ". :(</p>"
								+ "<p>Acesse a <a href='http://easii.ufpi.br/codivision/'>CODIVISION</a> para visualizar mais informações.</p>"
								+ "</body></html>";
					}
					else {
						msg = "<html><body>"
								+ "<p>Ola! "+auth.getName()+",</p>"
								+ "<p>Você pode fazer melhor! O seu desempenho na produção de testes caiu em relação ao último ciclo no repositório " + repName + ". :(</p>"
								+ "<p>Acesse a <a href='http://easii.ufpi.br/codivision/'>CODIVISION</a> para visualizar mais informações.</p>"
								+ "</body></html>";
					}
				}
			}
		}
		return msg;
	}
	
	private void premia (Extraction task) {
		EntityManager em = factory.createEntityManager();
		EntityTransaction transaction = em.getTransaction();
		transaction.begin();
		dao = new RepositoryDAO();
		dao.setEntityManager(em);
		
		authorDAO = new AuthorDAO();
		authorDAO.setEntityManager(em);
		Repository repository = dao.findById(task.getTarget());
		List<Revision> revs = repository.getRevisions();
		List<Author> auths = new ArrayList<Author>();
		log.info("Preparando lista de autores");
		int test = 0;
		int code = 0;
		int maiorValorC = 0;
		int maiorValorT = 0;
		for (Revision rev: revs) {
			if(!auths.contains(rev.getAuthor()) && rev.getAuthor().getAutFather() == null)
			{
				auths.add(rev.getAuthor());
				GamePoints gp = rev.getAuthor().getLastGamePointParam(rev.getAuthor().getGpValid());
				test = test + gp.getNumberOfLinesTest();
				code = code + gp.getNumberOfLinesCode();
				if (gp.getNumberOfLinesCode() > maiorValorC) {
					maiorValorC = gp.getNumberOfLinesCode();
				}
				if (gp.getNumberOfLinesTest() > maiorValorT) {
					maiorValorT = gp.getNumberOfLinesTest();
				}
			}
		}
		
		log.info("Calculando desvio padrão");
		double mediaAritmT = test/auths.size();
		double mediaAritmC = code/auths.size();
		double desvPadraoT = 0;
		double desvPadraoC = 0;
		for (Author auth: auths) {
			desvPadraoT = desvPadraoT + Math.pow((auth.getLastGamePointParam(auth.getGpValid()).getNumberOfLinesTest() - mediaAritmT),2);
			desvPadraoC = desvPadraoC + Math.pow((auth.getLastGamePointParam(auth.getGpValid()).getNumberOfLinesCode() - mediaAritmC),2);
		}
		desvPadraoT = Math.sqrt(desvPadraoT / auths.size());
		desvPadraoC = Math.sqrt(desvPadraoC / auths.size());
		
		log.info("Premiando");
		if (maiorValorT != 0) {
			//PREMIAÇÃO
			for (Author auth: auths) {
				
				//DIAMANTE
				
				//OURO
				if (auth.getLastGamePointParam(auth.getGpValid()).getNumberOfLinesTest() == maiorValorT) {
					auth.getLastGamePointParam(auth.getGpValid()).setGmedal(1);
					auth.setGmedal(1);
					auth.setScore(5);
					sendMail("gold", null, repository.getName(), auth.getEmail());
				}
				else {
					//PRATA
					if (auth.getLastGamePointParam(auth.getGpValid()).getNumberOfLinesTest() > mediaAritmT + desvPadraoT) {
						auth.getLastGamePointParam(auth.getGpValid()).setSmedal(1);
						auth.setSmedal(1);
						auth.setScore(3);
						sendMail("silver", null, repository.getName(), auth.getEmail());
					}
					else {
						//BRONZE
						if (auth.getLastGamePointParam(auth.getGpValid()).getNumberOfLinesTest() > mediaAritmT) {
							auth.getLastGamePointParam(auth.getGpValid()).setBmedal(1);
							auth.setBmedal(1);
							auth.setScore(2);
							sendMail("bronze", null, repository.getName(), auth.getEmail());
						}
						else {
							System.out.println("PREPARANDO PRA ENVIAR MENSAGEM");
							sendMail("nothing", null, repository.getName(), auth.getEmail());
						}
					}
				}
				authorDAO.save(auth);
			}
		}
		else {
			for (Author auth: auths) {
				log.info("Email via premiação");
				sendMail("nothing", null, repository.getName(), auth.getEmail());
			}
		}
		transaction.commit();
		em.close();
	}
	
	private void update(Extraction task) {
		EntityManager em = factory.createEntityManager();
		EntityTransaction transaction = em.getTransaction();
		transaction.begin();
		dao = new RepositoryDAO();
		dao.setEntityManager(em);
		
		authorDAO = new AuthorDAO();
		authorDAO.setEntityManager(em);

		Repository repository = dao.findById(task.getTarget());
		
		DeleteDir.deleteDir(new File(GitUtil.getDirectoryToSave().concat(repository.getUrl().replace(":", "-"))));
		
		try {

			RepositoryCredentials credentials = task.getCredentials();

			GitUtil util = null;
			log.info("Iniciando o Clone");
			if(credentials.getLogin() == null || credentials.getPassword() == null) {
				util = new GitUtil(repository.getUrl(),
						repository.getExtractionPath().getPath().substring(1));
			}else {
				util = new GitUtil(repository.getUrl(),
						repository.getExtractionPath().getPath().substring(1),
						credentials.getLogin(), credentials.getPassword());
			}
			log.info("Clone finalizado");

			log.info("Iniciando a extracao dos diffs");
			List<Revision> revs = util.getRevisions();
			List<Revision> revs2 = dao.findById(repository.getId()).getRevisions();
			List<Revision> result = new ArrayList<Revision>();
			boolean remove = false;
			for (Revision rev : revs) {
				for (Revision rev2 : revs2) {
					//System.out.println("compara " + rev.getExternalId() + "  "+ rev2.getExternalId());
					if (rev.getExternalId().equals(rev2.getExternalId())) {
						//System.out.println("igual");
						result.add(rev2);
						remove = true;
						break;
					}
				}
				if (remove) {
					//System.out.println("remover " + rev.getExternalId());
					remove = false;
				} else {
					result.add(rev);
				}
			}
			
			log.info("Extracao dos diffs concluidas");
			repository.setLastUpdate(repository.getRevisions().get(0).getDate());
			repository.setLastRevision(repository.getRevisions().get(0).getExternalId());
			
			if(result.size() != revs2.size()) {
				repository.setRevisions(result);

			/* Calcula as revisoes que possuem mais arquivos que o normal */
			List<Integer> qntFileRevision = new ArrayList<Integer>();
			for(Revision revision: repository.getRevisions()) {
				qntFileRevision.add(revision.getTotalFiles());
			}

			Collections.sort(qntFileRevision);

			int limiar = (int) Outliers.indentify(qntFileRevision);


			List<Revision> revisions = new ArrayList<Revision>();
			for(Revision revision: repository.getRevisions()) {
				if(revision.getTotalFiles() <= limiar) {
					revisions.add(revision);
				}
			}

			repository.setRevisions(revisions);
			log.info("Iniciando a extracao dos testes");
			repository.setTestFiles(util.getTestFiles());
			log.info("A extracao dos testes concluida");
			

			DirTree tree = new DirTree();
			tree.setText(repository.getExtractionPath().getPath().substring(1));
			tree.setType(NodeType.FOLDER);
			log.info("Iniciando a extracao da arvore");
			tree.setChildren(util.getDirTree());
			log.info("Arvore concluida");
			repository.getExtractionPath().setDirTree(tree);
			
			log.info("Iniciando DT");
			repository.setCodeSmallsFile(util.getCodeSmellFiles());
			log.info("Dt concluida");
			
			log.info("Extraindo arquivos de código...");
			List<Class> arquivosJava = util.getRepositoryJavaFiles();
			log.info("Extração de arquivos de código concluída!");
			
			if(!arquivosJava.isEmpty()) {
				log.info("Inicializando a identificacao do acoplamento");
				ClassGraphBuilder graph = new ClassGraphBuilder(arquivosJava);
				
				for (NodeInfo nodeInfo : graph.getG().vertexSet()) {
					for (br.ufpi.codivision.debit.model.File file : repository.getCodeSmallsFile()) {
						String full = "/src/main/java/" + file.getPath();	
						full = full.concat(".java");

						if(nodeInfo.getC().getFullname().equals(full)) {
							file.setAcoplamento(nodeInfo.getDegreeOUT());
						}
							
					}
					
				}
				
				log.info("Finalizando a identificacao do acoplamento");
			}
			
			if(!arquivosJava.isEmpty()) {
				log.info("Inicializando a identificação de funcionalidades...");
				ClassGraphBuilder builder = new ClassGraphBuilder(arquivosJava);
				ControllerDefiner controllerDefiner = new ControllerDefiner(builder.getG());
				FeatureDefiner fd = new FeatureDefiner(controllerDefiner.controllersDefiner(), builder.getG());
				List<Feature> features = fd.featureIdentify();
				repository.getExtractionPath().setFeatures(features);
				log.info("Identificação de funcionalidades concluída!");
			}
			
			
			log.info("Iniciando a extracao do historico de DTs pagas por cada desenvolvedor");
			Map<String, Map<String, Integer>> tdRemove = Fuzzy.historicTDRemove(repository);
			List<TDAuthor> list = new ArrayList<TDAuthor>();
			for (String nameAuthor : tdRemove.keySet()) {
				TDAuthor tdAuthor = new TDAuthor(nameAuthor);
				Map<String, Integer> map = tdRemove.get(nameAuthor);
				for (String dt : map.keySet()) {
					InfoTD infoTD = new InfoTD();
					infoTD.setCodeSmellType(dt);
					infoTD.setQnt(map.get(dt));
					tdAuthor.getYoursCodeSmell().add(infoTD);
				}
				
				list.add(tdAuthor);
			}
			
			repository.setTdAuthor(list);
			log.info("Extracao do historico de DTs pagas por cada desenvolvedor concluída!");
			
			
		
			log.info("Save repositories");
			saveAuthors(revisions);
			dao.save(repository);

			UserDAO userDAO = new UserDAO();
			userDAO.setEntityManager(em);

			List<User> users = userDAO.listByRepository(repository.getId());
			for (User toUser : users)
				sendMail("",toUser, repository.getName(), "");

			util.closeRepository();
			}


		} catch (Exception e) {
			e.printStackTrace();
		}


		if(repository.haveGameId()) {
			log.info("Iniciando extração do Gamification");
			Long[] array = task.getData();
			for(Revision revision: repository.getRevisions()) {
				if(repository.getLastUpdateFromGit().compareTo(revision.getDate()) < 0 || (array[0] == 0 && array[1] == 0 && array[2] == 0 && array[3] == 0)) {
					Author auth = revision.getAuthor();
					if (auth.getAutFather() != null) {
						auth = authorDAO.findById(auth.getAutFather());
					}
					List<OperationFile> files = revision.getFiles();
					List<TestFile> testFiles = repository.getTestFiles();
					int LinesTest = 0;
					int Lines = 0;
					int MethodsTest = 0;
					boolean aux = false;
					for (OperationFile of : files) {
						for(TestFile tf : testFiles) {
							if (tf.getPath().equals(of.getPath())) {
								LinesTest = LinesTest + of.getLineAdd() + of.getLineMod();
								aux = true;
							}
						}
						if (!aux) {
							Lines = Lines + of.getLineAdd() + of.getLineMod();
						}
						aux = false;
					}
					auth.addNumberOfLinesCode(Lines);
					auth.addNumberOfLinesTest(LinesTest);
					if (auth.getLastGamePoint() == null) {
						GamePoints gp = new GamePoints();
						gp.addNumberOfLinesCode(Lines);
						gp.addNumberOfLinesTest(LinesTest);
						gp.countDays();
						auth.addGamePoints(gp);
						//gamePointsDAO.save(gp);
						authorDAO.save(auth);
					}
					else {
						if(!array[2].equals(array[6])) {
							log.info("entrou aqui errado");
							GamePoints gp = auth.getLastGamePoint();
							gp.addNumberOfLinesCode(Lines);
							gp.addNumberOfLinesTest(LinesTest);
							gp.countDays();
							//gamePointsDAO.save(gp);
							authorDAO.save(auth);
						}
						else {
							log.info("entrou aqui certo");
							GamePoints gp = new GamePoints();
							gp.addNumberOfLinesCode(Lines);
							gp.addNumberOfLinesTest(LinesTest);
							auth.addGpValid();
							auth.addGamePoints(gp);
							//gamePointsDAO.save(gp);
							authorDAO.save(auth);
						}
					}
				}
			}
		}
		repository.setLastUpdateFromGit(new Date());
		DeleteDir.deleteDir(new File(GitUtil.getDirectoryToSave().concat(repository.getUrl().replace(":", "-"))));
		log.info("TaskUpdate.finalizado()");
		transaction.commit();
		em.close();
		
	}
	
	private void sendMail(String message, User user, String repositoryName, String mail) {
		log.info("Enviando email");
		try {

			if (user != null) {
				message = "<html><body>"
						+ "<p>Ola! "+user.getName()+",</p>"
						+ "<p>O repositorio "+repositoryName+" foi atualizado.</p>"
						+ "<p>Acesse a <a href='http://easii.ufpi.br/codivision/'>CODIVISION</a> para verificar o resultado.</p>"
						+ "</body></html>";
			} else {
				if (message == "gold") {
					message = "<html><body>"
							+ "<p>Ciclo de gamification no "+repositoryName+ " foi reiniciado!</p>"
							+ "<p>Parabéns! Você obteve a medalha de ouro, significando que produziu a maior quantidade de linhas de arquivo de teste neste ciclo! :D.</p>"
							+ "<p>Acesse a <a href='http://easii.ufpi.br/codivision/'>CODIVISION</a> para verificar o resultado.</p>"
							+ "</body></html>";
				}
				else {
					if (message == "silver") {
						message = "<html><body>"
								+ "<p>Ciclo de gamification no "+repositoryName+ " foi reiniciado!</p>"
								+ "<p>Parabéns! Você obteve a medalha de prata, significando que produziu uma quantidade de linhas de teste bem acima da média neste ciclo! Tente, você pode conseguir ouro nos próximos ciclos!.</p>"
								+ "<p>Acesse a <a href='http://easii.ufpi.br/codivision/'>CODIVISION</a> para verificar o resultado.</p>"
								+ "</body></html>";
					}	
					else {
						if (message == "bronze") {
							message = "<html><body>"
									+ "<p>Ciclo de gamification no "+repositoryName+ " foi reiniciado!</p>"
									+ "<p>Parabéns! Você obteve a medalha de bronze, significando que produziu uma quantidade de linhas de teste acima da média neste ciclo! Com esforços, você pode conseguir ouro nos próximos ciclos!.</p>"
									+ "<p>Acesse a <a href='http://easii.ufpi.br/codivision/'>CODIVISION</a> para verificar o resultado.</p>"
									+ "</body></html>";
						}	
						else {
							if (message == "nothing") {
								message = "<html><body>"
										+ "<p>Ciclo de gamification no "+repositoryName+ " foi reiniciado!</p>"
										+ "<p>Você não obteve um bom desempenho em produção de testes neste ciclo! Fique ligado! Um novo ciclo se inicia e assim poderá praticar essa boa prática tão importante!.</p>"
										+ "<p>Acesse a <a href='http://easii.ufpi.br/codivision/'>CODIVISION</a> para verificar o resultado.</p>"
										+ "</body></html>";
							}
						}
					}
				}
			}
			final Properties properties = new Properties(); 
			InputStream in = EmailDispatcher.class.getResourceAsStream("/mail.properties");  
			properties.load(in); 
			in.close(); 

			Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(properties.getProperty("mail.user"), properties.getProperty("mail.password"));
				}
			});

			final MimeMessage mimeMessage = new MimeMessage(session);
			mimeMessage.setFrom(new InternetAddress(properties.getProperty("mail.user")));
			if (user != null) {
				mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
				System.out.println("Enviando " + message + " para " + user.getEmail());
			}
			else {
				mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(mail));
				System.out.println("Enviando " + message + " para " + mail);
			}
			mimeMessage.setContent(message, "text/html");
			mimeMessage.setSubject("Repositório atualizado");

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Transport.send(mimeMessage);
					} catch (MessagingException e) {
						log.error(e.getMessage());
					}
				}
			}).start();

		} catch (AddressException e) {
			log.error(e.getMessage());
		} catch (MessagingException e) {
			log.error(e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
	
	private void saveAuthors(List<Revision> revisions) {
 		HashMap<String, Author> authors = new HashMap<>();
		
  		for (Revision r : revisions) {
			Author author = authors.get(r.getAuthor().getEmail());
 			if(author == null) {
 				author = new Author(r.getAuthor().getName(), r.getAuthor().getEmail());
 				authors.put(r.getAuthor().getEmail(), author);
 			}
  		}
  		
  		for (Author a : authors.values()) {
  			a = this.authorDAO.save(a);
  			for (Revision r : revisions) {
  				if(r.getAuthor().getEmail().equals(a.getEmail())) {
  					r.setAuthor(a);
  				}
  			}
 		}
 	}
}
