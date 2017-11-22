package br.ufpi.codivision.feature.common.util;

public enum FeatureNodeType {
	
	FEATURE("feature"), ELEMENT("element");
	
	private String type;
	
	private FeatureNodeType(String type){
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
