/**
 * 
 */
package br.ufpi.codivision.core.extractor.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.tasks.Task;
import br.com.caelum.vraptor.tasks.scheduler.Scheduled;
import br.ufpi.codivision.common.notification.EmailDispatcher;
import br.ufpi.codivision.core.dao.RepositoryDAO;
import br.ufpi.codivision.core.dao.UserDAO;
import br.ufpi.codivision.core.dao.UserRepositoryDAO;
import br.ufpi.codivision.core.model.Repository;
import br.ufpi.codivision.core.model.User;
import br.ufpi.codivision.core.model.UserRepository;
import br.ufpi.codivision.core.model.vo.CommitHistory;

/**
 * @author Irvayne Matheus
 *
 */
@Scheduled(fixedRate = 3600000, concurrent = false)
public class TaskEmail implements Task{
	
	@Inject private EntityManagerFactory factory;
	
	private RepositoryDAO repositoryDAO;
	private UserDAO userDAO;
	private UserRepositoryDAO userRepositoryDAO;
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	public TaskEmail(){
		
		this.repositoryDAO = new RepositoryDAO();
		this.userRepositoryDAO = new UserRepositoryDAO();
		this.userDAO = new UserDAO();
	}

	@Override
	public void execute() {
		
		EntityManager em = factory.createEntityManager();
		
		this.repositoryDAO.setEntityManager(em);
		this.userDAO.setEntityManager(em);
		this.userRepositoryDAO.setEntityManager(em);
		
		
		List<UserRepository> users = userRepositoryDAO.findAll();
		for(UserRepository user: users){
			//sendMail(user.getUser(), user.getRepository());
		}
		
		em.close();
	}
	
	private void sendMail(User user, Repository repository) {
		try {
			
		List<CommitHistory> percentage = repositoryDAO.getContribuitionQntLine(repository.getId());
		List<CommitHistory> percentageTest = repositoryDAO.getContribuitionQntLineTest(repository.getId());
		
		String his = "<tr><td>Desenvolvedor</td><td>Quantidade de Linhas Alteradas</td></tr>";
		for(CommitHistory history:percentage){
			his = his + "<tr><td>"+ history.getName() +"</td><td><center>"+history.getData()[0]+"</center></td></tr>";
		}
		
		String hisTest = "<tr><td>Desenvolvedor</td><td>Quantidade de Linhas Alteradas</td></tr>";
		for(CommitHistory history:percentageTest){
			hisTest = hisTest + "<tr><td>"+ history.getName() +"</td><td><center>"+history.getData()[0]+"</center></td></tr>";
		}
		if(percentage.size()==0){
			his = "<tr><td>Não houve alterações nesse perído</td></tr>";
		}
		if(percentageTest.size()==0){
			hisTest = "<tr><td>Não houve alterações nesse perído</td></tr>";
		}
		
//		Map<String, Object> velocityParams = new HashMap<String, Object>();
//		velocityParams.put("username", user.getName());
//		velocityParams.put("repositoryUrl", repository.getUrl());
//		velocityParams.put("path", repository.getExtractionPaths().get(0).getPath());
//		velocityParams.put("history", his);
//		velocityParams.put("historyTest", hisTest);
//		velocityParams.put("url", "http://easii.ufpi.br/codivision/");
//		String message = VelocityEngineUtil.getTemplate(velocityParams, "/templetes/repository_report.html");

		Date date = new Date();
		String dateCurrent = date.getDate() + "/"+(date.getMonth()+1)+"/"+(date.getYear()+1900);
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -2);
		Date initDate = c.getTime();
		String dateInit = initDate.getDate() + "/"+(initDate.getMonth()+1)+"/"+(initDate.getYear()+1900);
		
		String lastUpdate = repository.getLastUpdate().getDay() + "/"+(repository.getLastUpdate().getMonth()+1)+"/"+(repository.getLastUpdate().getYear()+1900);
		
		
		String message = "<!DOCTYPE html> <html>"
				+ "<body><center><div><h3>Relatório sobre as alterações feitas entre "+dateInit+" e "+dateCurrent+"</h3>"
				+"<hr><h3> Informações do projeto</h3>"
				+"<p>URL: "+repository.getUrl()+"</p>"
				+"<p>Data da ultima atualização registrada na CoDiVision: "+lastUpdate+"</p>"
				+ "<hr><h4>Quantidade de linhas alteradas</h4>"
				+ "<table border='1'>"+his+"</table>"
				+ "<h4>Quantidade de linhas alteradas nos arquivos de Teste</h4>"
				+ "<table border='1'>"+hisTest+"</table>"
				+ "<hr><p>Para informações mais detalhadas como visualizar quantidade de "
				+ "linhas alteradas em partes do projeto"
				+ " em um período de tempo específico acesse <a href='http://easii.ufpi.br/codivision/'>CoDiVision</a></p>"
				+ "</div></center></body></html>";
		
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
		mimeMessage.setSubject("Relatório Semanal do Projeto "+repository.getName());
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
