package br.ufpi.codivision.core.extractor.model;

public class RepositoryCredentials {
	
	private String login;
	private String password;
	
	public RepositoryCredentials(String login, String password) {
		this.login = login;
		this.password = password;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	
	

}
