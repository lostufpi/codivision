/**
 * 
 */
package br.ufpi.codivision.core.model.enums;

/**
 * @author Werney Ayala
 *
 */
public enum RepositoryType {
	
	SVN("vcs.svn"), GITHUB("vcs.github"), GIT("vcs.git");
	
	private String type;
	
	private RepositoryType(String type) {
		this.type = type;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
}
