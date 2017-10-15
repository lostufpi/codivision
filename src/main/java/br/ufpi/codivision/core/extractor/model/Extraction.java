/**
 * 
 */
package br.ufpi.codivision.core.extractor.model;

import br.ufpi.codivision.core.repository.GitUtil;

/**
 * @author Werney Ayala
 *
 */
public class Extraction {
	
	private Long target;
	private ExtractionType type;
	private GitUtil util;
	
	/**
	 * 
	 */
	public Extraction() {	}
	
	public Extraction(Long target, ExtractionType type, GitUtil util) {
		this.target =  target;
		this.type = type;
		this.setUtil(util);
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

	public GitUtil getUtil() {
		return util;
	}

	public void setUtil(GitUtil util) {
		this.util = util;
	}
	
}
