/**
 * 
 */
package br.ufpi.codivision.core.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import br.ufpi.codivision.common.model.PersistenceEntity;

/**
 * @author Werney Ayala
 *
 */
@Entity
public class User implements PersistenceEntity{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	
	private String login;
	
	private String password;
	
	private String email;
	
	private String gravatarImageUrl;
	
	private String recoveryCode;
	
	@OneToMany(mappedBy = "user")
	private List<UserRepository> userRepositories;
	
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
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
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
	 * @return the userRepositories
	 */
	public List<UserRepository> getUserRepositories() {
		return userRepositories;
	}

	/**
	 * @param userRepositories the userRepositories to set
	 */
	public void setUserRepositories(List<UserRepository> userRepositories) {
		this.userRepositories = userRepositories;
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

	/**
	 * @return the recoveryCode
	 */
	public String getRecoveryCode() {
		return recoveryCode;
	}

	/**
	 * @param recoveryCode the recoveryCode to set
	 */
	public void setRecoveryCode(String recoveryCode) {
		this.recoveryCode = recoveryCode;
	}

}
