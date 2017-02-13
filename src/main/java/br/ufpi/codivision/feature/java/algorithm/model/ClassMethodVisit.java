package br.ufpi.codivision.feature.java.algorithm.model;

import br.ufpi.codivision.feature.java.model.Class;
import br.ufpi.codivision.feature.java.model.Method;

/**
 * @author Vanderson Moura
 *
 */
public class ClassMethodVisit {
	private Class c;
	private Method method;
	
	public ClassMethodVisit() {
	}
	
	public ClassMethodVisit(Class class_, Method method) {
		super();
		this.c = class_;
		this.method = method;
	}

	/**
	 * @return the method
	 */
	public Method getMethod() {
		return method;
	}

	/**
	 * @param method the method to set
	 */
	public void setMethod(Method method) {
		this.method = method;
	}

	/**
	 * @return the class
	 */
	public Class getC() {
		return c;
	}

	/**
	 * @param class_ the class to set
	 */
	public void setC(Class class_) {
		this.c = class_;
	}
}
