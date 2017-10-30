package br.ufpi.codivision.debit.metric;

public enum MetricID {
	ATFD("atfd"),
	CYCLO("cyclo"),
	LVAR("lvar"),
	MAXNESTING("maxnesting"),
	MLOC("mloc"),
	NOM("nom"),
	NOA("noa"),
	NOAV("noav"),
	PAR("par"),
	LOC("loc"),
	TCC("tcc"),
	WMC("wmc"),
	AMW("amw"),
	DIT("dit"),
	NProtM("nprotm"),
	BOvR("bovr"),
	BUR("bur"),
	WOC("woc"),
	NOPA("nopa"),
	NOAM("noam"),
	LAA("laa"),
	FDP("fdp");

	private String descricao;
	
	private MetricID(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
}
