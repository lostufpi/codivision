/**
 * 
 */
package br.ufpi.codivision.common.security;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import br.ufpi.codivision.core.model.vo.UserVO;

/**
 * @author Werney Ayala
 *
 */
@Named 
@SessionScoped
public class UserSession implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private UserVO user;
	
	public void login(UserVO user) {
		this.user = user;
	}
	
	public void setUser(UserVO user){
		this.user = user;
	}
	
	public void logout() {
		this.user = null;
	}
	
	public boolean isLogged(){
		return (user != null);
	}
	
	public UserVO getUser(){
		return user;
	}
	
}
