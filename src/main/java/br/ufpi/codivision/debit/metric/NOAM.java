package br.ufpi.codivision.debit.metric;

import java.util.List;

import br.ufpi.codivision.debit.ast.AST;
import br.ufpi.codivision.debit.ast.AbstractField;
import br.ufpi.codivision.debit.ast.AbstractMethod;
import br.ufpi.codivision.debit.ast.AbstractType;

public class NOAM implements IMetric {

	@Override
	public void calculate(AST ast) {
		for (AbstractType type : ast.getTypes())
			type.getMetrics().put(MetricID.NOAM, calculate(type.getMethods(), type.getFields()));
	}

	public int calculate(List<AbstractMethod> methods, List<AbstractField> fields) {
		int accessorMehtods = 0;
		for (AbstractMethod method : methods)
			if (method.getModifiers().contains("public") && method.isAccessor())
				accessorMehtods++;

		return accessorMehtods;
	}

	@Override
	public MetricID getId() {
		return MetricID.NOAM;
	}

	@Override
	public MetricID[] getRequiredMetrics() {
		return null;
	}

}