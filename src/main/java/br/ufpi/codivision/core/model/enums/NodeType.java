/**
 * 
 */
package br.ufpi.codivision.core.model.enums;

/**
 * @author Werney Ayala
 *
 */
public enum NodeType {
	
	FOLDER("folder"), FILE("file");
	
	private String type;
	
	private NodeType(String type){
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
