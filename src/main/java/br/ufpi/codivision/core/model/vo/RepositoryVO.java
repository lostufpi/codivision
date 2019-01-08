/**
 * 
 */
package br.ufpi.codivision.core.model.vo;

import java.io.Serializable;
import java.util.Date;

import br.ufpi.codivision.core.model.Repository;

/**
 * @author Werney Ayala
 *
 */
public class RepositoryVO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;
	
	private String name;
	
	private String url;
	
	private Date lastUpdate;

	private Date lastUpdateFromGit;
	
	private String branchName;
	
	private boolean local;
	
	private boolean gameId;
	
	
	
	public boolean getGameId() {
		return gameId;
	}
	
	public boolean haveGameId() {
		return this.gameId;
	}

	public void setGameId(boolean gameId) {
		this.gameId = gameId;
	}
	
	public RepositoryVO(){
		
	}
	
	public RepositoryVO(Repository repository){
		this.lastUpdateFromGit = repository.getLastUpdateFromGit();
		this.id = repository.getId();
		this.name = repository.getName();
		this.url = repository.getUrl();
		this.lastUpdate = repository.getLastUpdate();
		this.local = repository.isLocal();
		this.branchName = repository.getExtractionPath().getPath();
		this.gameId=repository.haveGameId();
	}

	public Date getLastUpdateFromGit() {
		return lastUpdateFromGit;
	}

	public void setLastUpdateFromGit(Date lastUpdateFromGit) {
		this.lastUpdateFromGit = lastUpdateFromGit;
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

	public boolean isLocal() {
		return local;
	}

	public void setLocal(boolean local) {
		this.local = local;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	
}
