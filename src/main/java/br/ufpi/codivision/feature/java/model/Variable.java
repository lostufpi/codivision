package br.ufpi.codivision.feature.java.model;

/**
 * @author Vanderson Moura
 *
 */
public class Variable {
	private String type;
	private String name;
	
	/**
	 * @param type
	 * @param name
	 */
	public Variable(String type, String name) {
		super();
		this.type = type;
		this.name = name;
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
}
