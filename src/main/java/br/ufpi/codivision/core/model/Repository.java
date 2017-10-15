/**
 * 
 */
package br.ufpi.codivision.core.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufpi.codivision.common.model.PersistenceEntity;
import br.ufpi.codivision.core.model.enums.RepositoryType;

/**
 * @author Werney Ayala
 *
 */
@Entity
public class Repository implements PersistenceEntity{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	
	private String owner;
	
	private String url;
	
	private String repositoryRoot;
	
	private RepositoryType type;

	private String lastRevision;
	
	private boolean local;
	
	private boolean deleted;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name="repositoryId")
	private List<Revision> revisions;
	
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private ExtractionPath extractionPath;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name="repositoryId")
	private List<TestFile> testFiles;
	
	@ElementCollection
	@CollectionTable(name="DeletedRevision")
	private List<String> deletedRevisions;
	
	@OneToMany(mappedBy = "repository")
	private List<UserRepository> userRepositories;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUpdate;
	
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Configuration configuration;
	
	public Repository(){
		this.revisions = new ArrayList<Revision>();
		this.deletedRevisions = new ArrayList<String>();
		this.extractionPath = new ExtractionPath();
		this.testFiles = new ArrayList<TestFile>();
	}
	
	public List<TestFile> getTestFiles() {
		return testFiles;
	}

	public void setTestFiles(List<TestFile> testFiles) {
		this.testFiles = testFiles;
	}

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
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
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
	 * @return the revisions
	 */
	public List<Revision> getRevisions() {
		return revisions;
	}

	/**
	 * @param revisions the revisions to set
	 */
	public void setRevisions(List<Revision> revisions) {
		this.revisions = revisions;
	}

	/**
	 * @return the lastUpdate
	 */
	public Date getLastUpdate() {
		return lastUpdate;
	}

	/**
	 * @param lastUpdate the lastUpdate to set
	 */
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	/**
	 * @return the userRepositories
	 */
	public List<UserRepository> getUserRepositories() {
		return userRepositories;
	}

	/**
	 * @param userRepositories the userRepositories to set
	 */
	public void setUserRepositories(List<UserRepository> userRepositories) {
		this.userRepositories = userRepositories;
	}

	/**
	 * @return the configuration
	 */
	public Configuration getConfiguration() {
		return configuration;
	}

	/**
	 * @param configuration the configuration to set
	 */
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public boolean isLocal() {
		return local;
	}

	public void setLocal(boolean local) {
		this.local = local;
	}

	/**
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * @return the type
	 */
	public RepositoryType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(RepositoryType type) {
		this.type = type;
	}

	/**
	 * @return the lastRevision
	 */
	public String getLastRevision() {
		return lastRevision;
	}

	/**
	 * @param lastRevision the lastRevision to set
	 */
	public void setLastRevision(String lastRevision) {
		this.lastRevision = lastRevision;
	}

	/**
	 * @return the deletedRevisions
	 */
	public List<String> getDeletedRevisions() {
		return deletedRevisions;
	}

	/**
	 * @param deletedRevisions the deletedRevisions to set
	 */
	public void setDeletedRevisions(List<String> deletedRevisions) {
		this.deletedRevisions = deletedRevisions;
	}

	/**
	 * @return the extractionPath
	 */
	public ExtractionPath getExtractionPath() {
		return extractionPath;
	}

	/**
	 * @param extractionPath the extractionPath to set
	 */
	public void setExtractionPath(ExtractionPath extractionPath) {
		this.extractionPath = extractionPath;
	}

	/**
	 * @return the deleted
	 */
	public boolean isDeleted() {
		return deleted;
	}

	/**
	 * @param deleted the deleted to set
	 */
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public String getRepositoryRoot() {
		return repositoryRoot;
	}

	public void setRepositoryRoot(String repositoryRoot) {
		this.repositoryRoot = repositoryRoot;
	}
}
