package br.ufpi.codivision.debit.metric;

import br.ufpi.codivision.debit.ast.AST;
import br.ufpi.codivision.debit.ast.AbstractMethod;
import br.ufpi.codivision.debit.ast.AbstractType;

public class MAXNESTING implements IMetric {

	@Override
	public void calculate(AST ast) {
		for (AbstractMethod method : ast.getMethods())
			method.getMetrics().put(MetricID.MAXNESTING, method.getMaxDepth());
		
		for (AbstractType type : ast.getTypes())
			for (AbstractMethod method : type.getMethods())
				method.getMetrics().put(MetricID.MAXNESTING, method.getMaxDepth());
	}

	@Override
	public MetricID getId() {
		return MetricID.MAXNESTING;
	}

	@Override
	public MetricID[] getRequiredMetrics() {
		return null;
	}
	
}