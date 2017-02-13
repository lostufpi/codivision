package br.ufpi.codivision.feature.java.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vanderson Moura
 *
 */
public class Method {
	private Class ownerClass;
	private String name;
	private String content;
	private String body;
	private String returnType;
	private List<Variable> variables;
	private List<String> modifiers;
	private List<Parameter> parameters;
	private List<MethodInvocation> methodInvocations;
	private List<String> simpleNames;
	
	/**
	 * @param name
	 * @param content
	 * @param body
	 * @param returnType
	 * @param parameters
	 */
	public Method(String name, String content, String body, String returnType,
			List<Parameter> parameters) {
		super();
		this.name = name;
		this.content = content;
		this.body = body;
		this.returnType = returnType;
		this.parameters = parameters;
		this.variables = new ArrayList<Variable>();
		this.methodInvocations = new ArrayList<MethodInvocation>();
		this.simpleNames = new ArrayList<String>();
	}
	
	/**
	 * @param name
	 * @param content
	 * @param body
	 * @param returnType
	 * @param methodInvocations
	 * @param modifiers
	 * @param parameters
	 * @param variables
	 * @param ownerClass
	 */
	public Method(String name, String content, String body, String returnType,
			List<MethodInvocation> methodInvocations, List<String> modifiers,
			List<Parameter> parameters, List<Variable> variables,
			Class ownerClass) {
		super();
		this.name = name;
		this.content = content;
		this.body = body;
		this.returnType = returnType;
		this.methodInvocations = methodInvocations;
		this.modifiers = modifiers;
		this.parameters = parameters;
		this.variables = variables;
		this.ownerClass = ownerClass;
		this.simpleNames = new ArrayList<String>();
	}



	/**
	 * @param name
	 * @param content
	 * @param body
	 * @param returnType
	 * @param parameters
	 * @param modifiers
	 */
	public Method(String name, String content, String body, String returnType,
			List<Parameter> parameters, List<String> modifiers) {
		super();
		this.name = name;
		this.content = content;
		this.body = body;
		this.returnType = returnType;
		this.parameters = parameters;
		this.modifiers = modifiers;
		this.simpleNames = new ArrayList<String>();
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
	 * @return the body
	 */
	public String getBody() {
		return body;
	}
	
	/**
	 * @param body the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the returnType
	 */
	public String getReturnType() {
		return returnType;
	}

	/**
	 * @param returnType the returnType to set
	 */
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	/**
	 * @return the parameters
	 */
	public List<Parameter> getParameters() {
		return parameters;
	}

	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	/**
	 * @return the variables
	 */
	public List<Variable> getVariables() {
		return variables;
	}

	/**
	 * @param variables the variables to set
	 */
	public void setVariables(List<Variable> variables) {
		this.variables = variables;
	}

	/**
	 * @return the modifiers
	 */
	public List<String> getModifiers() {
		return modifiers;
	}

	/**
	 * @param modifiers the modifiers to set
	 */
	public void setModifiers(List<String> modifiers) {
		this.modifiers = modifiers;
	}

	/**
	 * @return the methodInvocations
	 */
	public List<MethodInvocation> getMethodInvocations() {
		return methodInvocations;
	}

	/**
	 * @param methodInvocations the methodInvocations to set
	 */
	public void setMethodInvocations(List<MethodInvocation> methodInvocations) {
		this.methodInvocations = methodInvocations;
	}

	/**
	 * @return the ownerClass
	 */
	public Class getOwnerClass() {
		return ownerClass;
	}

	/**
	 * @param ownerClass the ownerClass
	 */
	public void setOwnerClass(Class ownerClass) {
		this.ownerClass = ownerClass;
	}
	
	/**
	 * @return true if exist any opcional parameter or false else 
	 */
	public boolean containsOptionalParam (){
		for (Parameter p : this.parameters) {
			if (p.isOptional()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return the simpleNames
	 */
	public List<String> getSimpleNames() {
		return simpleNames;
	}

	/**
	 * @param simpleNames the simpleNames to set
	 */
	public void setSimpleNames(List<String> simpleNames) {
		this.simpleNames = simpleNames;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.name;
	}
}
