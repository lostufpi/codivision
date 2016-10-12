/**
 * 
 */
package br.ufpi.codivision.core.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.validator.Severity;
import br.com.caelum.vraptor.validator.SimpleMessage;
import br.ufpi.codivision.common.annotation.Public;
import br.ufpi.codivision.common.notification.EmailNotification;
import br.ufpi.codivision.common.security.UserSession;
import br.ufpi.codivision.core.dao.UserDAO;
import br.ufpi.codivision.core.model.User;
import br.ufpi.codivision.core.model.validator.UserValidator;
import br.ufpi.codivision.core.model.vo.UserVO;
import br.ufpi.codivision.core.util.GenerateHashPasswordUtil;
import br.ufpi.codivision.core.util.Gravatar;

/**
 * This class is responsible for processing requests related to user
 * 
 * @author Werney Ayala
 *
 */
@Controller
public class UserController {

	@Inject private Result result;
	@Inject private UserSession userSession;

	@Inject private UserDAO dao;

	@Inject private UserValidator validator;

	@Inject private EmailNotification emailNotification;
	
	private final Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * This method render the view to login a user
	 */
	@Get
	@Public
	public void login() {
	}

	/**
	 * This method compare the database login and password with the login and
	 * password passed in param
	 * 
	 * @param user
	 *            - User to login
	 */
	@Post
	@Public
	public void login(User user) {

		User userValid = validator.validateSignin(user);
		validator.onErrorRedirectTo(this.getClass()).login();

		UserVO currentUser = new UserVO(userValid);
		userSession.login(currentUser);
		result.redirectTo(RepositoryController.class).list();
	}

	/**
	 * This method logout the user in the session
	 */
	@Get
	public void logout() {
		userSession.logout();
		result.redirectTo(this).login();
	}

	/**
	 * This method render the view to add a user
	 */
	@Get
	@Public
	public void add() {
	}

	/**
	 * This method add a new user
	 * 
	 * @param user
	 *            - User to save
	 * @param checkPassword
	 *            - Password confirmation
	 */
	@Post
	@Public
	public void add(User user, String checkPassword) {

		User validatedUser = validator.validateAddUser(user, checkPassword);
		validator.onErrorRedirectTo(this.getClass()).add();

		validatedUser.setId(null);
		validatedUser.setGravatarImageUrl(Gravatar.urlGravatar(validatedUser.getEmail()));

		dao.save(validatedUser);
		// coloco o valor real da senha e chamo o método que realiza a validaçao
		// e o envio do email
//		user.setPassword(checkPassword);
//		emailValidator.validatorADD(user);

		result.include("notice", new SimpleMessage("success", "user.add.success", Severity.INFO));
		result.redirectTo(UserController.class).login();

	}

	/**
	 * This method render the view to profile a user
	 */
	@Get
	public void profile() {
		String urlImage = userSession.getUser().getGravatarImageUrl() + "?s=217";
		result.include("urlImage", urlImage);
	}

	/**
	 * This method is responsible for change user data.
	 * 
	 * @param user - user data
	 * @param passwordNew - new password
	 * @param passwordNewCheck - new password
	 */
	@Post
	@Path("/user/{userId}/edit")
	public void edit(User user, String passwordNew, String passwordNewCheck) {

		user.setId(userSession.getUser().getId());

		User userEdit = validator.validateEdit(user, passwordNew, passwordNewCheck);
		validator.onErrorRedirectTo(this.getClass()).profile();

		userEdit.setName(user.getName());
		userEdit.setLogin(user.getLogin());
		userEdit.setEmail(user.getEmail());
		userEdit.setGravatarImageUrl(Gravatar.urlGravatar(user.getEmail()));

		dao.save(userEdit);
		userSession.setUser(new UserVO(userEdit));
		
		result.include("notice", new SimpleMessage("success", "change.success", Severity.INFO));
		result.redirectTo(UserController.class).profile();

	}
	
	@Public
	@Get("/user/password/forgot")
	public void forgotPassword() {	}

	/**
	 * This method responsible for sending an email with a link that give the
	 * right to change the User password registered with the past as parameter
	 * email
	 * 
	 * @param email
	 *            - Recipient e-mail
	 */
	@Public
	@Post("/user/password/forgot")
	public void forgotPassword(String email) throws IOException {
		
		User user = validator.validatorForgot(email);
		validator.onErrorRedirectTo(this.getClass()).forgotPassword();
		
		Date date = new Date();
		try {
			user.setRecoveryCode(GenerateHashPasswordUtil.generateHash(email) + date.getTime());
		} catch (NoSuchAlgorithmException e) {
			log.error(e.getMessage());
		}
		dao.save(user);
		
		emailNotification.sendChangePasswordNotification(user);
		
		result.include("notice", new SimpleMessage("success", "email.sent.success", Severity.INFO));
		result.redirectTo(UserController.class).login();
		
	}

	/**
	 * This method checks if the recovery code is valid, if valid, it redirects
	 * you to the screen where you will be asked the new password
	 * 
	 * @param code
	 *            - code of recuperation
	 **/
	@Public
	@Get("/user/password/change/{code}")
	public void changePasswordCode(String code) {

		User user = validator.validatorRecoveryCode(code);
		validator.onErrorRedirectTo(UserController.class).login();
		result.redirectTo(this.getClass()).changePassword(user.getLogin());
		
	}

	/**
	 * This method render the view to redefine password a user
	 * 
	 * @param login
	 *            - User login that will have the password changed
	 */
	@Public
	@Get("/user/password/change")
	public void changePassword(String login) {
		result.include("loginUser", login);
	}

	/**
	 * This method checks if the entered passwords are the same, if it saves the
	 * User the value of this new password
	 * 
	 * @param password
	 *            - new password to be saved
	 * @param checkPassword
	 *            - new password to be saved and should be verified
	 * @param loginUser
	 *            - User login only to have the password changed
	 *
	 */
	@Public
	@Post("/user/password/change")
	public void changePassword(String password, String checkPassword, String loginUser) {
		
		User user = validator.validatorChangePassword(password, checkPassword, loginUser);
		validator.onErrorRedirectTo(this.getClass()).changePassword(loginUser);
		
		dao.save(user);
		
		result.include("notice", new SimpleMessage("success", "passowrd.changed.success", Severity.INFO));
		result.redirectTo(UserController.class).login();
		
	}

}
