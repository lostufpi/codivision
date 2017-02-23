package br.ufpi.codivision.feature.java.model;

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
@Entity
public class FeatureClass implements PersistenceEntity{
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
	@JoinColumn(name = "classId")
	private Class class_;

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
	 * @return the class_
	 */
	public Class getClass_() {
		return class_;
	}

	/**
	 * @param class_ the class_ to set
	 */
	public void setClass_(Class class_) {
		this.class_ = class_;
	}
}
