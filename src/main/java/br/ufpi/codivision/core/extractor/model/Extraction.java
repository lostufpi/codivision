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
	
	/**
	 * 
	 */
	public Extraction() {	}
	
	public Extraction(Long target, ExtractionType type) {
		this.target =  target;
		this.type = type;
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
	
}
