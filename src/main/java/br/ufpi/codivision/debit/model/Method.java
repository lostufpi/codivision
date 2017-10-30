package br.ufpi.codivision.debit.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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

	@Column(columnDefinition = "TEXT")
	private String name = "nameMethod";
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name="methodId")
	private List<MetricMethod> codeMetrics;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name="methodId")
	private List<CodeSmellMethod> codeSmells;
	
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
	public List<MetricMethod> getCodeMetrics() {
		return codeMetrics;
	}
	public void setCodeMetrics(List<MetricMethod> codeMetrics) {
		this.codeMetrics = codeMetrics;
	}
	public List<CodeSmellMethod> getCodeSmells() {
		return codeSmells;
	}
	public void setCodeSmells(List<CodeSmellMethod> codeSmells) {
		this.codeSmells = codeSmells;
	}

	@Override
	public String toString() {
		return "<br>" + name + "<br>------" + codeMetrics + "<br>------" + codeSmells + "";
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
