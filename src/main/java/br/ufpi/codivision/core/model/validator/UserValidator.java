/**
 * 
 */
package br.ufpi.codivision.core.model.validator;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import javax.inject.Inject;

import br.com.caelum.vraptor.validator.SimpleMessage;
import br.ufpi.codivision.common.validator.BaseValidator;
import br.ufpi.codivision.core.dao.UserDAO;
import br.ufpi.codivision.core.model.User;
import br.ufpi.codivision.core.util.GenerateHashPasswordUtil;

/**
 * @author Werney Ayala
 *
 */
public class UserValidator extends BaseValidator {

	public static final int PASSWORD_MIN_LENGTH = 6;
	public static final int PASSWORD_MAX_LENGTH = 100;

	public static final int LOGIN_MIN_LENGTH = 4;
	public static final int LOGIN_MAX_LENGTH = 100;

	public static final String LOGIN_PATTERN = "[a-zA-Z0-9_]+";

	@Inject
	private UserDAO dao;
	
	public User validateAddUser(User user, String checkPassword) {
		
		validateNewLogin(user.getLogin());
		validateNewPassword(user.getPassword(), checkPassword);
		
		//verifica se já não possui outra conta cadastrada com esse mail
		validator.check(dao.findByEmail(user.getEmail())==null, new SimpleMessage("error", "user.email.used"));
		user.setPassword(generateHashPassword(user.getPassword()));
		
		return user;
	}

	private void validateNewLogin(String login) {

		if (login.isEmpty()) {
			validator.add(new SimpleMessage("error", "user.login.required"));
		} else if (login.length() < LOGIN_MIN_LENGTH || login.length() > LOGIN_MAX_LENGTH) {
			validator.add(new SimpleMessage("error", "user.login.length"));
		} else if (!login.matches(LOGIN_PATTERN)) {
			validator.add(new SimpleMessage("error", "user.login.pattern"));
		} else if (dao.findByLogin(login) != null) {
			validator.add(new SimpleMessage("error", "user.login.used"));
		}

	}

	public void validateNewPassword(String password, String checkPassword) {

		if (password == null || checkPassword == null || password.isEmpty() || checkPassword.isEmpty()) {
			validator.add(new SimpleMessage("error", "user.password.required"));
		} else if (password.length() < PASSWORD_MIN_LENGTH || password.length() > PASSWORD_MAX_LENGTH) {
			validator.add(new SimpleMessage("error", "user.password.length"));
		} else if (!password.equals(checkPassword)) {
			validator.add(new SimpleMessage("error", "user.password.confirmation"));
		}

	}

	public User validateSignin(User user) {

		if (user.getLogin().isEmpty() || user.getLogin() == null) {
			validator.add(new SimpleMessage("error", "user.login.required"));
		} else if (user.getPassword().isEmpty() || user.getPassword() == null) {
			validator.add(new SimpleMessage("error", "user.password.required"));
		}

		user.setPassword(generateHashPassword(user.getPassword()));
		
		//busca pelo login e login pois busca
		User currentUser = dao.find(user);
		//busca pelo email
		if(currentUser==null){
			currentUser = dao.findByEmail(user.getLogin());
			if(!currentUser.getPassword().equals(user.getPassword())){
				currentUser = null;
			}
		}
		
		validator.check(currentUser!=null, new SimpleMessage("error", "user.signin.error"));

		return currentUser;

	}
	
	public User validateFindByLogin(String login) {
		User user = dao.findByLogin(login);
		validator.check(user != null, new SimpleMessage("error", "user.signin.error"));
		return user;
	}

	private String generateHashPassword(String password) {

		try {
			return GenerateHashPasswordUtil.generateHash(password);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			validator.add(new SimpleMessage("error", "user.password.encrypt"));
		} catch (NullPointerException e) {

		}

		return null;
	}

	public User validateEdit(User userForm, String passwordNew, String passwordNewCheck){
		
		User userDB = dao.findById(userForm.getId());
		if (userDB == null) 
			validator.add(new SimpleMessage("error", "user.signin.error"));
		
		// se a senha for não vazia ou seja, é passado algum valor no campo da senha.
		if (userForm.getPassword() != null && !userForm.getPassword().equals("")) {

			String passwordFormHash = generateHashPassword(userForm.getPassword());

			// verifica se a senha atual é igual a senha do usuário logado
			this.validateNewPassword(userDB.getPassword(), passwordFormHash);

			// verifica se a novas senhas coincidem
			this.validateNewPassword(passwordNew, passwordNewCheck);

			userForm.setPassword(generateHashPassword(passwordNew));
		}
		
		//verifica se o campo da senha atual é vazia e os da nova senha nao 
		if (userForm.getPassword() == null && (passwordNew != null || passwordNewCheck != null)) {
			this.validateNewPassword(passwordNew, passwordNewCheck);
			validator.add(new SimpleMessage("error", "user.current.password"));
		}
		
		// verifica se não possui algum outra conta com esse login
		this.validateEditLogin(userForm.getLogin(), userForm.getId());

		//verifica se ja não possui nenhuma outra conta cadastrada com esse novo email
		this.validateEditEmail(userForm.getEmail(), userForm.getId());

		if(userForm.getPassword() == null)
			userForm.setPassword(userDB.getPassword());

		return userForm;
	}
	
	public User validateEditLogin(String login, long id) {
		User user = dao.findByLogin(login);
		if (user != null) {
			validator.check(user.getId() == id, new SimpleMessage("error", "user.login.used"));
		}
		return user;
	}
	
	public User validateEditEmail(String email, Long id) {
		User user = dao.findByEmail(email);
		if (user != null) {
			validator.check(user.getId() == id, new SimpleMessage("error", "user.email.used"));
		}
		return user;
	}
	
	/**
	 * This method responsible for sending an email with a link that give the
	 * right to change the User password registered with the past as parameter
	 * email
	 * 
	 * @param email - Recipient e-mail
	 */
	public User validatorForgot(String email) {
		
		User user = dao.findByEmail(email);
		validator.check(user != null, new SimpleMessage("error", "user.email.invalid"));
		
		return user;

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
	 * @return
	 */
	public User validatorChangePassword(String password, String checkPassword, String loginUser) {
		
		validateNewPassword(password, checkPassword);
		User user = dao.findByLogin(loginUser);
		
		try {
			user.setPassword(GenerateHashPasswordUtil.generateHash(password));
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			validator.add(new SimpleMessage("error", "user.password.encrypt"));
		}

		user.setRecoveryCode(null);
		return user;
	}

	/**
	 * This method verifies that the recovery code is valid
	 * 
	 * @param code
	 *            - code of recuperation
	 * 
	 * @return - User with this code
	 */
	public User validatorRecoveryCode(String code) {
		User user = dao.findByRecoveryCode(code);
		validator.check(user!=null, new SimpleMessage("error", "user.code.invalid"));
		return user;
	}

}
