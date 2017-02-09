package br.ufpi.codivision.common.notification;

import java.util.ResourceBundle;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mail.MessagingException;

import br.ufpi.codivision.core.model.User;

/**
 * 
 * @author Werney Ayala
 *
 */
@RequestScoped
public class EmailNotification {
	
	@Inject
	private ResourceBundle bundle;
	
	@Inject
	private EmailDispatcher emailDispatcher;
	
	private final String URL_BASE = "http://easii.ufpi.br/codivision";

	private final String CHANGE_PASSWORD_TEMPLATE = "/templates/change_password.html";
	private final String REPOSITORY_UPDATED_TEMPLATE = "/templates/repository_updated.html";


	/**
	 * Email User to change passwords
	 * 
	 * @param toUser
	 */
	public void sendChangePasswordNotification(User toUser) {
		try {
			emailDispatcher.send(getProperty("user.password.change.notification"), makeHTMLChangePassword(toUser), 
					toUser.getEmail());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Make HTML template for Recover Password E-mail
	 * 
	 * @param user
	 * @return string template
	 */
	private String makeHTMLChangePassword(User user) {
//		Map<String, Object> velocityParams = new HashMap<String, Object>();
//		velocityParams.put("username", user.getName());
//		velocityParams.put("url", URL_BASE + "/user/password/change/" + user.getRecoveryCode());
		//return VelocityEngineUtil.getTemplate(velocityParams, CHANGE_PASSWORD_TEMPLATE);
		String message = "<html><body><p>Olá! "+user.getName()+",<p>"
				+ "<p>Seu pedido de alteração de senha foi atentido com sucesso pelo sistema.</p>"
				+ "<p><a href='"+URL_BASE + "/user/password/change/" + user.getRecoveryCode()+"'>Clique aqui</a> para realizar a alteração de sua senha.</p>"
						+ "</body></html>";
		return message;
	}
	
	/**
	 * Email User to notify that the repository has been updated
	 * @param toUser
	 * @param repositoryName
	 */
	public void sendRepositoryUpdatedNotification(User toUser, String repositoryName) {
		try {
			emailDispatcher.send(getProperty("repository.update.done"), makeHTMLRepositoryUpdated(toUser,repositoryName), 
					toUser.getEmail());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Make HTML template to notify that the repository has been updated
	 * @param user
	 * @param repositoryName
	 * @return
	 */
	private String makeHTMLRepositoryUpdated(User user, String repositoryName) {
//		Map<String, Object> velocityParams = new HashMap<String, Object>();
//		velocityParams.put("username", user.getName());
//		velocityParams.put("repositoryName", repositoryName);
//		velocityParams.put("url", URL_BASE);
//		return VelocityEngineUtil.getTemplate(velocityParams, REPOSITORY_UPDATED_TEMPLATE);
		String message = "<html><body>"
				+ "<p>Ola! "+user.getName()+",<p>"
				+ "<p>O repositorio "+repositoryName+" foi atualizado.</p>"
				+ "<p>Acesse a <a href='"+URL_BASE+"'>CODIVISION</a> para verificar o resultado.</p>"
				+ "</body></html>";
		return message;
	
	}
	
	private String getProperty(String key){
	    return bundle.getString(key); 
	}

	public void sendWelcomeUserNotification(User user) {
		
		try {
			emailDispatcher.send(getProperty("user.welcome"),makeHTMLWelcomeUserNotification(user) , user.getEmail());
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	} 
	
	private String makeHTMLWelcomeUserNotification(User user) {
//		Map<String, Object> velocityParams = new HashMap<String, Object>();
//		velocityParams.put("username", user.getName());
//		velocityParams.put("repositoryName", repositoryName);
//		velocityParams.put("url", URL_BASE);
//		return VelocityEngineUtil.getTemplate(velocityParams, REPOSITORY_UPDATED_TEMPLATE);
		String message = "Olá! "+user.getName()+","
				+ "Sua conta na CoDiVision foi cadastrada com sucesso."
				+ "Para acessa-la informe seu login e sua senha em: "+URL_BASE+"";
		return message;
	
	}
	
}
