/**
 * 
 */
package br.ufpi.codivision.core.extractor.model;

/**
 * @author Werney Ayala
 *
 */
public class Extraction {
	
	private Long target;
	private ExtractionType type;
	private RepositoryCredentials credentials;
	private Long[] data = new Long[8];
	private boolean force = false;
	private boolean first = false;
	
	/**
	 * 
	 */
	public Extraction() {	}
	
	public Extraction(Long target, ExtractionType type, RepositoryCredentials credentials) {
		this.target =  target;
		this.type = type;
		this.setCredentials(credentials);
		this.setData(null);
	}
	
	/**
	 * @return the target
	 */
	public Long getTarget() {
		return target;
	}
	
	/**
	 * @param target the target to set
	 */
	public void setTarget(Long target) {
		this.target = target;
	}

	/**
	 * @return the type
	 */
	public ExtractionType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(ExtractionType type) {
		this.type = type;
	}

	public RepositoryCredentials getCredentials() {
		return credentials;
	}

	public void setCredentials(RepositoryCredentials credentials) {
		this.credentials = credentials;
	}

	public Long[] getData() {
		return data;
	}

	public void setData(Long[] data) {
		this.data = data;
	}

	public boolean isForce() {
		return force;
	}

	public void setForce(boolean force) {
		this.force = force;
	}

	public boolean isFirst() {
		return first;
	}

	public void setFirst(boolean first) {
		this.first = first;
	}

	
}
