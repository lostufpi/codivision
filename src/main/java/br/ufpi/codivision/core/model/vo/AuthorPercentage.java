/**
 * 
 */
package br.ufpi.codivision.core.model.vo;

import java.io.Serializable;

/**
 * @author Werney Ayala
 *
 */
public class AuthorPercentage implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String name;
	private Double y;
	private Long absolute;
	
	/**
	 * @param name
	 * @param y
	 */
	public AuthorPercentage(String name, Double y, Long absolute) {
		this.name = name;
		this.y = y;
		this.absolute = absolute;
	}
	
	public AuthorPercentage() {
		
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
	 * @return the y
	 */
	public Double getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(Double y) {
		this.y = y;
	}

	/**
	 * @return the absolute
	 */
	public Long getAbsolute() {
		return absolute;
	}

	/**
	 * @param absolute the absolute to set
	 */
	public void setAbsolute(Long absolute) {
		this.absolute = absolute;
	}

}
