package br.ufpi.codivision.feature.java.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vanderson Moura
 *
 */
public class Package {
	private List<Class> classes;
	private String name;
	
	public Package() {
		this.classes = new ArrayList<Class>();
	}

	/**
	 * @param name
	 */
	public Package(String name) {
		this.classes = new ArrayList<Class>();
		this.name = new String(name);
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
