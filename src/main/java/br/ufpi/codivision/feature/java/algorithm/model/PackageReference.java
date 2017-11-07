package br.ufpi.codivision.feature.java.algorithm.model;

import br.ufpi.codivision.feature.java.model.Package;

public class PackageReference {
	
	private Package p;
	private double referencesInNumber;
	private double referencesOutNumber;
	
	public PackageReference(Package p) {
		super();
		this.p = p;
	}
	public Package getP() {
		return p;
	}
	public void setP(Package p) {
		this.p = p;
	}
	public double getReferencesInNumber() {
		return referencesInNumber;
	}
	public void setReferencesInNumber(double referencesInNumber) {
		this.referencesInNumber = referencesInNumber;
	}
	public double getReferencesOutNumber() {
		return referencesOutNumber;
	}
	public void setReferencesOutNumber(double referencesOutNumber) {
		this.referencesOutNumber = referencesOutNumber;
	}
}
