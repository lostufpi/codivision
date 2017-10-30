package br.ufpi.codivision.debit.codesmell;

public enum CodeSmellID {

	BRAIN_CLASS("brainclass"),
	BRAIN_METHOD("brainmethod"),
	COMPLEX_METHOD("complexmethod"),
	GOD_CLASS("godclass"),
	LONG_METHOD("longmethod"),
	DATA_CLASS("dataclass"),
	FEATURE_ENVY("featureenvy");

	private String descricao;
	
	private CodeSmellID(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
}