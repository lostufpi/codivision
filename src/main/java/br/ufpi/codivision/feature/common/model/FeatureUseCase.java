package br.ufpi.codivision.feature.common.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import br.ufpi.codivision.common.model.PersistenceEntity;

@Entity
public class FeatureUseCase implements PersistenceEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "featureId")
	private Feature feature;
	
	@ManyToOne (cascade = {CascadeType.MERGE, CascadeType.PERSIST})
	@JoinColumn(name = "useCaseId")
	private UseCase useCase;
	
	
	public FeatureUseCase() {
		super();
	}

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public Feature getFeature() {
		return feature;
	}

	public void setFeature(Feature feature) {
		this.feature = feature;
	}

	public UseCase getUseCase() {
		return useCase;
	}

	public void setUseCase(UseCase useCase) {
		this.useCase = useCase;
	}
}
