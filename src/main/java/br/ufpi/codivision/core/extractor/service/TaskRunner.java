/**
 * 
 */
package br.ufpi.codivision.core.extractor.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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
import br.ufpi.codivision.common.model.PersistenceEntity;
import br.ufpi.codivision.common.notification.EmailDispatcher;
import br.ufpi.codivision.core.dao.DirTreeDAO;
import br.ufpi.codivision.core.dao.ExtractionPathDAO;
import br.ufpi.codivision.core.dao.OperationFileDAO;
import br.ufpi.codivision.core.dao.RepositoryDAO;
import br.ufpi.codivision.core.dao.RevisionDAO;
import br.ufpi.codivision.core.dao.UserDAO;
import br.ufpi.codivision.core.extractor.DirTreeExtractor;
import br.ufpi.codivision.core.extractor.GitFileExtractor;
import br.ufpi.codivision.core.extractor.PathExtractor;
import br.ufpi.codivision.core.extractor.RevisionExtractor;
import br.ufpi.codivision.core.extractor.SvnFileExtractor;
import br.ufpi.codivision.core.extractor.model.Extraction;
import br.ufpi.codivision.core.extractor.model.ExtractionType;
import br.ufpi.codivision.core.model.DirTree;
import br.ufpi.codivision.core.model.ExtractionPath;
import br.ufpi.codivision.core.model.OperationFile;
import br.ufpi.codivision.core.model.Repository;
import br.ufpi.codivision.core.model.Revision;
import br.ufpi.codivision.core.model.User;

/**
 * @author Werney Ayala
 *
 */
@Scheduled(fixedRate = 60000, concurrent = false)
public class TaskRunner implements Task{
	
	@Inject private TaskService service;
	@Inject private ThreadExecutor executor;
	@Inject private EntityManagerFactory factory;
	
	private RepositoryDAO repositoryDAO;
	private RevisionDAO revisionDAO;
	private ExtractionPathDAO extractionPathDAO;
	private DirTreeDAO dirTreeDAO;
	private OperationFileDAO oFileDAO;
	private UserDAO userDAO;

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	public TaskRunner() {
		
		this.repositoryDAO = new RepositoryDAO();
		this.revisionDAO = new RevisionDAO();
		this.extractionPathDAO = new ExtractionPathDAO();
		this.dirTreeDAO = new DirTreeDAO();
		this.oFileDAO = new OperationFileDAO();
		this.userDAO = new UserDAO();
		
	}
	
	@Override
	public void execute() {
		
		
		EntityManager em = factory.createEntityManager();
		EntityTransaction transaction = em.getTransaction();
		transaction.begin();
		
		try {

			this.repositoryDAO.setEntityManager(em);
			this.revisionDAO.setEntityManager(em);
			this.extractionPathDAO.setEntityManager(em);
			this.dirTreeDAO.setEntityManager(em);
			this.oFileDAO.setEntityManager(em);
			this.userDAO.setEntityManager(em);
			
			Future<PersistenceEntity> future = executor.getFirstDone();
			while (future != null) {
				persistResult(future);
				future = executor.getFirstDone();
			}

			if (service.getTaskQueue().isEmpty())
				addFilesToDiff();

			if (service.getTaskQueue().isEmpty()) {

				if(service.getUpdating() != null) {
					Repository repository = repositoryDAO.findById(service.getUpdating());
					List<User> users = userDAO.listByRepository(service.getUpdating());
					for (User toUser : users)
						sendMail(toUser, repository.getName());
					service.setUpdating(null);
					
					repository.setLastUpdate(new Date());
					repositoryDAO.save(repository);
					
					log.info("Extraction done: " + repository.getName());
				}

				if (service.getToUpdateQueue().isEmpty()) {
					transaction.commit();
					return;
				} else {
					Long updating = service.getFirstToUpdate();
					service.addTask(new Extraction(updating, ExtractionType.REPOSITORY));
					service.setUpdating(updating);
				}

			}

			Extraction extraction = service.getFirstTask();
			while (extraction != null) {
				addTaskToExecute(extraction);
				extraction = service.getFirstTask();
			}

			transaction.commit();

		} catch (ExecutionException e) {
			log.error(e.getMessage());
		} catch (InterruptedException e) {
			log.error(e.getMessage());
		} finally {
			if (transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
			em.close();
		}

		
	}
	
	private void addFilesToDiff() {
		
		// Verifica se não tem arquivos do svn aguardando diff	
		List<Long> toExtractIds = oFileDAO.getNotExtractedFiles();
		
		if(!toExtractIds.isEmpty()) {
			for(Long temp : toExtractIds)
				service.addTask(new Extraction(temp, ExtractionType.SVN_FILE));
		}
		
		// Verificar se não tem revisões do git aguardando diff dos arquivos
		toExtractIds = revisionDAO.getRevisionsToExtract();
		
		if(!toExtractIds.isEmpty()) {
			for(Long temp : toExtractIds)
				service.addTask(new Extraction(temp, ExtractionType.GIT_FILE));
		}
		
	}
	
	private void addTaskToExecute(Extraction extraction) {

		if (extraction.getType().equals(ExtractionType.REPOSITORY)) {

			Repository repository = repositoryDAO.findById(extraction.getTarget());
			List<ExtractionPath> paths = extractionPathDAO.getExtractionPaths(repository.getId());
			executor.add(new RevisionExtractor(repository,paths));

			log.info("Extracting the repository " + repository.getName());

		} else if (extraction.getType().equals(ExtractionType.TREE)) {

			ExtractionPath extractionPath = extractionPathDAO.findById(extraction.getTarget());
			Repository repository = extractionPathDAO.findRepositoryByExtractionPath(extractionPath.getId());

			DirTree dirTree = extractionPath.getDirTree();
			for(DirTree temp : dirTree.getChildren())
				dirTreeDAO.delete(temp);

			executor.add(new DirTreeExtractor(repository, extractionPath));

			log.debug("Extracting the directory tree from the repository " + repository.getName());

		} else if (extraction.getType().equals(ExtractionType.SVN_FILE)) {

			Object[] toExtract = oFileDAO.getNotExtractedFileById(extraction.getTarget());

			if (toExtract == null)
				return;

			String url = (String) toExtract[0];
			Long revisionNumber = Long.parseLong((String) toExtract[1]);
			OperationFile oFile = (OperationFile) toExtract[2];
			String repositoryRoot = (String) toExtract[3];

			executor.add(new SvnFileExtractor(url, revisionNumber, oFile,repositoryRoot));

			log.debug("Extracting the file " + oFile.getPath());

		} else if (extraction.getType().equals(ExtractionType.GIT_FILE)) {

			Object[] toExtract = revisionDAO.getRevisionToExtract(extraction.getTarget());

			if (toExtract == null)
				return;

			String repositoryName = (String) toExtract[0];
			String repositoryOwner = (String) toExtract[1];
			Revision revision = (Revision) toExtract[2];

			executor.add(new GitFileExtractor(repositoryName, repositoryOwner, revision));

			log.debug("Extracting the revision " + revision.getExternalId());

		}else if(extraction.getType().equals(ExtractionType.PATH)){
			
			ExtractionPath extractionPath = extractionPathDAO.findById(extraction.getTarget());
			Repository repository = extractionPathDAO.findRepositoryByExtractionPath(extractionPath.getId());
			
			executor.add(new PathExtractor(repository, extractionPath));
			
			log.debug("Extracting the extractionPath " + extractionPath.getPath());
		}

	}
	
	private void persistResult(Future<PersistenceEntity> future) throws InterruptedException, ExecutionException {

		if (future.isDone()) {

			PersistenceEntity entity = future.get();

			if (entity instanceof Repository) {

				Repository result = (Repository) entity;
				Repository repository = repositoryDAO.findById(result.getId());

//				/* Calcula as revisoes que possuem mais arquivos que o normal */
//				int limiar = (int) Outliers.indentify(revisionDAO.getTotalFilesOfRevisions(repository.getId()));
//				List<String> revisionOutliers = revisionDAO.getRevisionOutliers(repository.getId(), limiar);
//				repository.setDeletedRevisions(revisionOutliers);
//
//				/* Exclui as revisões com mais arquivos que o normal */
//				if(!revisionOutliers.isEmpty()){
//					for(Long temp : revisionDAO.getIdFromRevisionOutliers(repository.getId(), revisionOutliers)) {
//						Revision revision = new Revision();
//						revision.setId(temp);
//						//revisionDAO.delete(revision);
//					}
//				}
				
				//caso seja a primeira vez que o repositorio é atualizado, ele satisfaz a primeira cláusula 
				//Se não, depois ele verifica se o repositorio está desatualizado com o LastRevision diferentes
				if (repository.getLastRevision()==null || !repository.getLastRevision().equals(result.getLastRevision())) {
					for(ExtractionPath path : repository.getExtractionPaths())
						service.addTask(new Extraction(path.getId(), ExtractionType.TREE));
				}

				repository.getRevisions().addAll(result.getRevisions());
				repository.setLastRevision(result.getLastRevision());

				repositoryDAO.save(repository);

			} else if (entity instanceof DirTree) {
				dirTreeDAO.save((DirTree) entity);
			} else if (entity instanceof OperationFile) {
				oFileDAO.save((OperationFile) entity);
			} else if (entity instanceof Revision) {
				
				Revision result = (Revision) entity;
				Revision revision = revisionDAO.findById(result.getId());
				revision.getFiles().addAll(result.getFiles());
				revision.setTotalFiles(revision.getFiles().size());
				revision.setExtracted(true);
				revisionDAO.save(revision);
				
			}

		}

	}
	
	private void sendMail(User user, String repositoryName) {
		
		try {
		
//		Map<String, Object> velocityParams = new HashMap<String, Object>();
//		velocityParams.put("username", user.getName());
//		velocityParams.put("repositoryName", repositoryName);
//		velocityParams.put("url", "http://easii.ufpi.br/codivision/");
		//String message = VelocityEngineUtil.getTemplate(velocityParams, "/templates/repository_updated.html");
		
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
		//message.setText(MENSSAGE);

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
