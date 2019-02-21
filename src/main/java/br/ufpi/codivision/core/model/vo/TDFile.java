package br.ufpi.codivision.core.model.vo;

public class TDFile {
	
	private String path;
	private int acoplamento;
	private int complexidade;
	private int qntTD;
	private double gc;
	
	
	public TDFile(String path, int acoplamento, int complexidade, int qntTD, double gc) {
		super();
		this.path = path;
		this.acoplamento = acoplamento;
		this.complexidade = complexidade;
		this.qntTD = qntTD;
		this.gc = gc;
	}
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public int getAcoplamento() {
		return acoplamento;
	}
	public void setAcoplamento(int acoplamento) {
		this.acoplamento = acoplamento;
	}
	public int getComplexidade() {
		return complexidade;
	}
	public void setComplexidade(int complexidade) {
		this.complexidade = complexidade;
	}
	public int getQntTD() {
		return qntTD;
	}
	public void setQntTD(int qntTD) {
		this.qntTD = qntTD;
	}
	public double getGc() {
		return gc;
	}
	public void setGc(double gc) {
		this.gc = gc;
	}
	
	

}
