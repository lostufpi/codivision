package br.ufpi.codivision.feature.java.model;

/**
 * @author Vanderson Moura
 *
 */
public class NodeInfo {
	private Class class_;
	private Integer degreeIN;
	private Integer degreeOUT;
	private boolean hasCycle;

	/**
	 * @param c
	 */
	public NodeInfo(Class c) {
		super();
		this.class_ = c;
		this.degreeIN = new Integer(0);
		this.degreeOUT = new Integer(0);
	}

	/**
	 * @return the class
	 */
	public Class getC() {
		return class_;
	}

	/**
	 * @param c the class to set
	 */
	public void setC(Class c) {
		this.class_ = c;
	}

	/**
	 * @return the degreeIN
	 */
	public Integer getDegreeIN() {
		return degreeIN;
	}

	/**
	 * @param degreeIN the degreeIN to set
	 */
	public void setDegreeIN(Integer degreeIN) {
		this.degreeIN = degreeIN;
	}

	/**
	 * @return the degreeOUT
	 */
	public Integer getDegreeOUT() {
		return degreeOUT;
	}

	/**
	 * @param degreeOUT the degreeOUT to set
	 */
	public void setDegreeOUT(Integer degreeOUT) {
		this.degreeOUT = degreeOUT;
	}

	/**
	 * @return true if hasCycle or false else
	 */
	public boolean hasCycle() {
		return hasCycle;
	}

	/**
	 * @param hasCycle the hasCycle to set
	 */
	public void setHasCycle(boolean hasCycle) {
		this.hasCycle = hasCycle;
	}

	/**
	 * increment the degreeIN
	 */
	public void incrementIN() {
		this.degreeIN++;
	}

	/**
	 * increment the degreeOUT
	 */
	public void incrementOUT() {
		this.degreeOUT++;
	}
}
