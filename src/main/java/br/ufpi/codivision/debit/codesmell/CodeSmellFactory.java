package br.ufpi.codivision.debit.codesmell;

public class CodeSmellFactory {

	public static ICodeSmell getCodeSmell(CodeSmellID id) {
		switch (id) {
		case BRAIN_CLASS:
			return new BrainClass();
		case BRAIN_METHOD:
			return new BrainMethod();
		case COMPLEX_METHOD:
			return new ComplexMethod();
		case DATA_CLASS:
			return new DataClass();
		case FEATURE_ENVY:
			return new FeatureEnvy();
		case GOD_CLASS:
			return new GodClass();
		case LONG_METHOD:
			return new LongMethod();
		default:
			return null;
		}
	}

}
