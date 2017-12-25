/**
 * 
 */
package br.ufpi.codivision.core.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import br.ufpi.codivision.common.model.PersistenceEntity;
import br.ufpi.codivision.feature.common.model.Feature;
import br.ufpi.codivision.feature.common.model.UseCase;

/**
 * @author Werney Ayala
 *
 */
@Entity
public class ExtractionPath implements PersistenceEntity{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private String path;
	
	@Lob
	private String dirTreeJson;
	
	
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private DirTree dirTree;
	

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name="extractionPathId")
	private List<Feature> features;
	
//	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//	@JoinColumn(name="extractionPathId")
//	private List<UseCase> useCases; 
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name="extractionPathId")
	private List<UseCase> useCases; 

	public ExtractionPath(){ }

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the dirTree
	 */
	public DirTree getDirTree() {
		return dirTree;
	}

	/**
	 * @param dirTree the dirTree to set
	 */
	public void setDirTree(DirTree dirTree) {
		this.dirTree = dirTree;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return the features
	 */
	public List<Feature> getFeatures() {
		return features;
	}

	/**
	 * @param features the features to set
	 */
	public void setFeatures(List<Feature> features) {
		this.features = features;
	}
	
	public String getDirTreeJson() {
		return dirTreeJson;
	}

	public void setDirTreeJson(String dirTreeJson) {
		this.dirTreeJson = dirTreeJson;
	}

	public List<UseCase> getUseCases() {
		return useCases;
	}

	public void setUseCases(List<UseCase> useCases) {
		this.useCases = useCases;
	}
}
