package br.ufpi.codivision.debit.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import br.ufpi.codivision.common.model.PersistenceEntity;

@Entity
public class File implements PersistenceEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String path;
	
	private int acoplamento;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name="fileId")
	private List<Method> methods;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name="fileId")
	private List<Metric> codeMetrics;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name="fileId")
	private List<CodeSmell> codeSmells;
	
	private long qntBadSmellComment;
	
	@Override
	public String toString() {
		return "File [path=" + path + ", codeMetrics=" + codeMetrics + ", codeSmells=" + codeSmells + ", methods="
				+ methods + ", qntBadSmellComment=" + qntBadSmellComment + "]";
	}

	public File() {
		this.methods = new ArrayList<>();
		this.codeMetrics = new ArrayList<>();
		this.codeSmells = new ArrayList<>();
	}
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public List<Metric> getCodeMetrics() {
		return codeMetrics;
	}
	public void setCodeMetrics(List<Metric> codeMetrics) {
		this.codeMetrics = codeMetrics;
	}
	public List<CodeSmell> getCodeSmells() {
		return codeSmells;
	}
	public void setCodeSmells(List<CodeSmell> codeSmells) {
		this.codeSmells = codeSmells;
	}
	public List<Method> getMethods() {
		return methods;
	}
	public void setMethods(List<Method> methods) {
		this.methods = methods;
	}

	public long getQntBadSmellComment() {
		return qntBadSmellComment;
	}

	public void setQntBadSmellComment(long qntBadSmellComment) {
		this.qntBadSmellComment = qntBadSmellComment;
	}

	@Override
	public Long getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setId(Long id) {
		// TODO Auto-generated method stub
		
	}

	public int getAcoplamento() {
		return acoplamento;
	}

	public void setAcoplamento(int acoplamento) {
		this.acoplamento = acoplamento;
	}
	
}
