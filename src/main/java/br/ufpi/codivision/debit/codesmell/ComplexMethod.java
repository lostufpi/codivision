package br.ufpi.codivision.debit.codesmell;

import br.ufpi.codivision.debit.ast.AST;
import br.ufpi.codivision.debit.ast.AbstractMethod;
import br.ufpi.codivision.debit.ast.AbstractType;
import br.ufpi.codivision.debit.metric.MetricID;

public class ComplexMethod implements ICodeSmell {

	private static final MetricID[] REQUIRED_METRICS = { MetricID.CYCLO };

	private int cycloThreshold = 10;

	public ComplexMethod() {
	}

	public ComplexMethod(int cycloThreshold) {
		this.cycloThreshold = cycloThreshold;
	}

	@Override
	public void detect(AST ast) {
		for (AbstractMethod method : ast.getMethods()) {
			int cyclo = (Integer) method.getMetrics().get(MetricID.CYCLO);
			if (cyclo > cycloThreshold)
				method.getCodeSmells().add(CodeSmellID.COMPLEX_METHOD);
		}

		for (AbstractType type : ast.getTypes()) {
			for (AbstractMethod method : type.getMethods()) {
				int cyclo = (Integer) method.getMetrics().get(MetricID.CYCLO);
				if (cyclo > cycloThreshold)
					method.getCodeSmells().add(CodeSmellID.COMPLEX_METHOD);
			}
		}
	}

	@Override
	public CodeSmellID getId() {
		return CodeSmellID.COMPLEX_METHOD;
	}

	@Override
	public MetricID[] getRequiredMetrics() {
		return REQUIRED_METRICS;
	}

	@Override
	public CodeSmellID[] getRequiredCodeSmells() {
		return null;
	}

	/*** GETTERS AND SETTERS ***/

	public int getCycloThreshold() {
		return cycloThreshold;
	}

	public void setCycloThreshold(int cycloThreshold) {
		this.cycloThreshold = cycloThreshold;
	}

}