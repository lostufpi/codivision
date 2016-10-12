package br.ufpi.codivision.common.notification;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

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
	
	private final String URL_BASE = "http://localhost:8080/codivision";

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
		Map<String, Object> velocityParams = new HashMap<String, Object>();
		velocityParams.put("username", user.getName());
		velocityParams.put("url", URL_BASE + "/user/password/change/" + user.getRecoveryCode());
		return VelocityEngineUtil.getTemplate(velocityParams, CHANGE_PASSWORD_TEMPLATE);
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
		Map<String, Object> velocityParams = new HashMap<String, Object>();
		velocityParams.put("username", user.getName());
		velocityParams.put("repositoryName", repositoryName);
		velocityParams.put("url", URL_BASE);
		return VelocityEngineUtil.getTemplate(velocityParams, REPOSITORY_UPDATED_TEMPLATE);
	}
	
	private String getProperty(String key){
	    return bundle.getString(key); 
	} 
	
}
