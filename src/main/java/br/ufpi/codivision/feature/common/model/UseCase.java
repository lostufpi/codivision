package br.ufpi.codivision.feature.common.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import br.ufpi.codivision.common.model.PersistenceEntity;

@Entity
public class UseCase implements PersistenceEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	
	@OneToMany(mappedBy = "useCase", cascade = CascadeType.ALL)
	private List<FeatureUseCase> featureUseCases;
	
	public UseCase() {
		super();
		this.featureUseCases = new ArrayList<>();
	}
	
	public UseCase(String name) {
		this();
		this.name = name;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<FeatureUseCase> getFeatureUseCases() {
		return featureUseCases;
	}

	public void setFeatureUseCases(List<FeatureUseCase> featureUseCases) {
		this.featureUseCases = featureUseCases;
	}
}
