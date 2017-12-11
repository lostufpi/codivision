package br.ufpi.codivision.core.extractor.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

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
import br.ufpi.codivision.core.dao.RepositoryDAO;
import br.ufpi.codivision.core.dao.UserDAO;
import br.ufpi.codivision.core.extractor.model.Extraction;
import br.ufpi.codivision.core.extractor.model.RepositoryCredentials;
import br.ufpi.codivision.core.model.Author;
import br.ufpi.codivision.core.model.DirTree;
import br.ufpi.codivision.core.model.Repository;
import br.ufpi.codivision.core.model.Revision;
import br.ufpi.codivision.core.model.User;
import br.ufpi.codivision.core.model.enums.NodeType;
import br.ufpi.codivision.core.repository.GitUtil;
import br.ufpi.codivision.core.util.DeleteDir;
import br.ufpi.codivision.core.util.Outliers;
import br.ufpi.codivision.feature.common.model.Feature;
import br.ufpi.codivision.feature.java.algorithm.ControllerDefiner;
import br.ufpi.codivision.feature.java.algorithm.FeatureDefiner;
import br.ufpi.codivision.feature.java.graph.ClassGraphBuilder;
import br.ufpi.codivision.feature.java.model.Class;
import br.ufpi.codivision.feature.java.model.NodeInfo;


@Scheduled(fixedRate = 60000, concurrent = false)
public class TaskRunner implements Task{


	@Inject private TaskService service;
	@Inject private EntityManagerFactory factory;
	
	private RepositoryDAO dao;
	private AuthorDAO authorDAO;
	

	private final Logger log = LoggerFactory.getLogger(getClass());


	public void execute() {

		Extraction task = service.getFirstTask();
		if(task != null) {
			log.info("TaskRunner.execute()");

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
				repository.setRevisions(util.getRevisions());
				log.info("Extracao dos diffs concluidas");
				repository.setLastUpdate(repository.getRevisions().get(0).getDate());
				repository.setLastRevision(repository.getRevisions().get(0).getExternalId());

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
			
				log.info("Save repositories");
				
				
				saveAuthors(revisions);
				dao.save(repository);

				UserDAO userDAO = new UserDAO();
				userDAO.setEntityManager(em);

				List<User> users = userDAO.listByRepository(repository.getId());
				for (User toUser : users)
					sendMail(toUser, repository.getName());

				util.closeRepository();



			} catch (Exception e) {
				e.printStackTrace();
			}

			DeleteDir.deleteDir(new File(GitUtil.getDirectoryToSave().concat(repository.getUrl().replace(":", "-"))));
			log.info("TaskRunner.finalizado()");
			transaction.commit();
			em.close();
		}
	}

	private void sendMail(User user, String repositoryName) {

		try {


			String message = "<html><body>"
					+ "<p>Ola! "+user.getName()+",<p>"
					+ "<p>O repositorio "+repositoryName+" foi atualizado.</p>"
					+ "<p>Acesse a <a href='http://easii.ufpi.br/codivision/'>CODIVISION</a> para verificar o resultado.</p>"
					+ "</body></html>";

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

			mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
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
