/**
 * 
 */
package br.ufpi.codivision.core.model.vo;

import java.io.Serializable;

import br.ufpi.codivision.core.model.User;

/**
 * @author Werney Ayala
 *
 */
public class UserVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;
	
	private String name;
	
	private String login;
	
	private String email;
	
	private String gravatarImageUrl;
	
	/**
	 * Default constructor
	 */
	public UserVO() {	}

	public UserVO(User user) {
		this.id = user.getId();
		this.name = user.getName();
		this.login = user.getLogin();
		this.email = user.getEmail();
		this.gravatarImageUrl = user.getGravatarImageUrl();
	}
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the login
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * @param login the login to set
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the gravatarImageUrl
	 */
	public String getGravatarImageUrl() {
		return gravatarImageUrl;
	}

	/**
	 * @param gravatarImageUrl the gravatarImageUrl to set
	 */
	public void setGravatarImageUrl(String gravatarImageUrl) {
		this.gravatarImageUrl = gravatarImageUrl;
	}
	
	
}
