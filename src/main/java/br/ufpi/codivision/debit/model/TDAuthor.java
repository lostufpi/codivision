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
public class TDAuthor implements PersistenceEntity{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name="tdAuthorId")
	private List<InfoTD> yoursCodeSmell;
	
	public TDAuthor() {
		this.yoursCodeSmell = new ArrayList<>();
	}
	
	public TDAuthor(String name) {
		this.name = name;
		this.yoursCodeSmell = new ArrayList<>();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<InfoTD> getYoursCodeSmell() {
		return yoursCodeSmell;
	}
	public void setYoursCodeSmell(List<InfoTD> yoursCodeSmell) {
		this.yoursCodeSmell = yoursCodeSmell;
	}

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

}
