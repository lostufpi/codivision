package br.ufpi.codivision.feature.common.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import br.ufpi.codivision.common.model.PersistenceEntity;

/**
 * @author Vanderson Moura
 *
 */
@Entity
public class Feature implements PersistenceEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	
	@OneToMany(mappedBy = "feature", cascade = CascadeType.ALL)
	private List<FeatureElement> featureElements;
	
	@OneToMany(mappedBy = "feature")
	private List<FeatureUseCase> featureUseCases;
	
	@ManyToOne
	@JoinColumn(name = "useCaseId")
	private UseCase useCase;
	
	
	/**
	 * 
	 */
	public Feature() {
	}
	
	/**
	 * @param name
	 */
	public Feature(String name) {
		super();
		this.name = name;
	}

	/**
	 * @param name
	 * @param featureElements
	 */
	public Feature(String name, List<FeatureElement> featureElements) {
		super();
		this.name = name;
		this.featureElements = featureElements;
	}
	
	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the featuresElements 
	 */
	public List<FeatureElement> getFeatureElements() {
		return featureElements;
	}

	/**
	 * @param featureElements the featureElements to set
	 */
	public void setFeatureElements(List<FeatureElement> featureElements) {
		this.featureElements = featureElements;
	}

	public UseCase getUseCase() {
		return useCase;
	}

	public void setUseCase(UseCase useCase) {
		this.useCase = useCase;
	}

	public List<FeatureUseCase> getFeatureUseCases() {
		return featureUseCases;
	}

	public void setFeatureUseCases(List<FeatureUseCase> featureUseCases) {
		this.featureUseCases = featureUseCases;
	}
}
