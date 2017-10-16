package br.ufpi.codivision.core.extractor.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
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
import br.ufpi.codivision.core.dao.RepositoryDAO;
import br.ufpi.codivision.core.dao.UserDAO;
import br.ufpi.codivision.core.extractor.model.Extraction;
import br.ufpi.codivision.core.model.DirTree;
import br.ufpi.codivision.core.model.Repository;
import br.ufpi.codivision.core.model.Revision;
import br.ufpi.codivision.core.model.User;
import br.ufpi.codivision.core.model.enums.NodeType;
import br.ufpi.codivision.core.repository.GitUtil;
import br.ufpi.codivision.core.util.DeleteDir;
import br.ufpi.codivision.core.util.Outliers;


@Scheduled(fixedRate = 60000, concurrent = false)
public class TaskRunner implements Task{


	@Inject private TaskService service;
	@Inject private EntityManagerFactory factory;
	private RepositoryDAO dao;
	
	private final Logger log = LoggerFactory.getLogger(getClass());


	public void execute() {

		Extraction task = service.getFirstTask();
		if(task != null) {
			System.out.println("TaskRunner.execute()");

			EntityManager em = factory.createEntityManager();
			EntityTransaction transaction = em.getTransaction();
			transaction.begin();
			dao = new RepositoryDAO();
			dao.setEntityManager(em);

			Repository repository = dao.findById(task.getTarget());
			try {

				GitUtil util = task.getUtil();


				repository.setRevisions(util.getRevisions());
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
				repository.setTestFiles(util.getTestFiles());
				repository.setCodeSmallsFile(util.getCodeSmellFiles());

				DirTree tree = new DirTree();
				tree.setText(repository.getExtractionPath().getPath().substring(1));
				tree.setType(NodeType.FOLDER);
				tree.setChildren(util.getDirTree());
				repository.getExtractionPath().setDirTree(tree);

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
			
			System.out.println(DeleteDir.deleteDir(new File(GitUtil.getDirectoryToSave().concat(repository.getUrl()))));

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

}
