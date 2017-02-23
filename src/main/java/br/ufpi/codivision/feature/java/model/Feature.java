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
	private List<Class> classes;
	
	@OneToMany(mappedBy = "feature")
	private List<FeatureClass> featureClasses;
	
	/**
	 * 
	 */
	public Feature() {
	}

	/**
	 * @param name
	 * @param classes
	 */
	public Feature(String name, List<Class> classes) {
		super();
		this.name = name;
		this.classes = classes;
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
	 * @return the classes
	 */
	public List<Class> getClasses() {
		return classes;
	}

	/**
	 * @param classes the classes to set
	 */
	public void setClasses(List<Class> classes) {
		this.classes = classes;
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
