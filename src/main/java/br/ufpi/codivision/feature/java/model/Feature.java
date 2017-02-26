package br.ufpi.codivision.feature.java.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
	
	@OneToMany(mappedBy = "feature")
	private List<FeatureClass> featureClasses;
	
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
	 * @param featureClasses
	 */
	public Feature(String name, List<FeatureClass> featureClasses) {
		super();
		this.name = name;
		this.featureClasses = featureClasses;
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
	 * @return the featuresClasses 
	 */
	public List<FeatureClass> getFeatureClasses() {
		return featureClasses;
	}

	/**
	 * @param featureClasses the featureClasses to set
	 */
	public void setFeatureClasses(List<FeatureClass> featureClasses) {
		this.featureClasses = featureClasses;
	}
}
