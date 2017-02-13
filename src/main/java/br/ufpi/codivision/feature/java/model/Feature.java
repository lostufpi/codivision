package br.ufpi.codivision.feature.java.model;

import java.util.List;

/**
 * @author Vanderson Moura
 *
 */
public class Feature {
	private String name;
	private List<Class> classes;
	
	/**
	 * 
	 */
	public Feature() {
	}

	/**
	 * @param name
	 * @param classes
	 */
	public Feature(String name, List<Class> classes) {
		super();
		this.name = name;
		this.classes = classes;
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
	 * @return the classes
	 */
	public List<Class> getClasses() {
		return classes;
	}

	/**
	 * @param classes the classes to set
	 */
	public void setClasses(List<Class> classes) {
		this.classes = classes;
	}
}
