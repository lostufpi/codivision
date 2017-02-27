package br.ufpi.codivision.feature.common.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import br.ufpi.codivision.common.model.PersistenceEntity;

/**
 * @author Vanderson Moura
 *
 */
/**
 * @author Vanderson Moura
 *
 */
@Entity
public class FeatureElement implements PersistenceEntity{
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
	
	@ManyToOne
	@JoinColumn(name = "elementId")
	private Element element;
	
	public FeatureElement() {
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
	 * @return the feature
	 */
	public Feature getFeature() {
		return feature;
	}

	/**
	 * @param feature the feature to set 
	 */
	public void setFeature(Feature feature) {
		this.feature = feature;
	}

	/**
	 * @return the element
	 */
	public Element getElement() {
		return element;
	}

	/**
	 * @param element the element to set
	 */
	public void setElement(Element element) {
		this.element = element;
	}
}
