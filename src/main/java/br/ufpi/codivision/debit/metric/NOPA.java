package br.ufpi.codivision.debit.metric;

import java.util.List;

import br.ufpi.codivision.debit.ast.AST;
import br.ufpi.codivision.debit.ast.AbstractField;
import br.ufpi.codivision.debit.ast.AbstractType;

public class NOPA implements IMetric {

	@Override
	public void calculate(AST ast) {
		for (AbstractType type : ast.getTypes())
			type.getMetrics().put(MetricID.NOPA, calculate(type.getFields()));
	}

	public int calculate(List<AbstractField> fields) {
		int publicMembers = 0;
		for (AbstractField field : fields)
			if (field.getModifiers().contains("public"))
				publicMembers++;

		return publicMembers;
	}

	@Override
	public MetricID getId() {
		return MetricID.NOPA;
	}

	@Override
	public MetricID[] getRequiredMetrics() {
		return null;
	}

}