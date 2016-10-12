/**
 * 
 */
package br.ufpi.codivision.core.model.enums;

/**
 * @author Werney Ayala
 *
 */
public enum PermissionType {
	
	OWNER("permission.owner"), MEMBER("permission.member");
	
	private String type;
	
	private PermissionType(String type){
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
