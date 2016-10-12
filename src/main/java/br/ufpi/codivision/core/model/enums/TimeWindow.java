/**
 * 
 */
package br.ufpi.codivision.core.model.enums;

/**
 * @author Werney Ayala
 *
 */
public enum TimeWindow {
	
	LAST_WEEK("window.week.last"), LAST_MONTH("window.month.last"), EVER("window.ever"), 
	CUSTOM("window.custom"), LAST_SIX_MONTHS("window.month.six");
	
	private String type;
	
	private TimeWindow(String type) {
		this.type = type;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
}
