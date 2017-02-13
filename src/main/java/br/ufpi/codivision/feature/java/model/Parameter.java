package br.ufpi.codivision.feature.java.model;

/**
 * @author Vanderson Moura
 *
 */
public class Parameter extends Variable{
	private boolean optional;
	
	/**
	 * @param type
	 * @param name
	 */
	public Parameter(String type, String name) {
		super(type, name);
		this.optional = false; 
	}

	/**
	 * @return true if the parameter is opcional or false else
	 */
	public boolean isOptional() {
		return optional;
	}

	/**
	 * @param optional the optional to set
	 */
	public void setOptional(boolean optional) {
		this.optional = optional;
	}
}
