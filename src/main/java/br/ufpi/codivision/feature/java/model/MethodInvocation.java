package br.ufpi.codivision.feature.java.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Vanderson Moura
 *
 */
public class MethodInvocation implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String expression;
	private String name;
	private List<String> arguments;
	
	/**
	 * 
	 */
	public MethodInvocation() {
		this.arguments = new ArrayList<String>();
	}

	/**
	 * @param expression
	 * @param name
	 */
	public MethodInvocation(String expression, String name) {
		super();
		this.expression = expression;
		this.name = name;
		this.arguments = new ArrayList<String>();
	}

	/**
	 * @return the expression
	 */
	public String getExpression() {
		return expression;
	}

	/**
	 * @param expression the expression to set
	 */
	public void setExpression(String expression) {
		this.expression = expression;
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
	 * @return the arguments
	 */
	public List<String> getArguments() {
		return arguments;
	}

	/**
	 * @param arguments the arguments to set
	 */
	public void setArguments(List<String> arguments) {
		this.arguments = arguments;
	}
}
