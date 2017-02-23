package codivision.util;

public enum ExtractionPath {
	
	MASTER("master"), INTEGRATION("integration"), FEATURE("feature");
	
	private String name;
	
	private ExtractionPath(String name) {
		this.name = name;
	}	

	/**
	 * @return the type
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param type the type to set
	 */
	public void setName(String type) {
		this.name = type;
	}
}
