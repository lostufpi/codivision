package br.ufpi.codivision.debit.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import br.ufpi.codivision.common.model.PersistenceEntity;

@Entity
public class Method implements PersistenceEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name="methodId")
	private List<Metric> codeMetrics;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name="methodId")
	private List<CodeSmell> codeSmells;
	
	public Method() {
		this.codeMetrics = new ArrayList<>();
		this.codeSmells = new ArrayList<>();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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

	@Override
	public String toString() {
		return "Method [name=" + name + ", codeMetrics=" + codeMetrics + ", codeSmells=" + codeSmells + "]";
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
	
	
	
	
}
