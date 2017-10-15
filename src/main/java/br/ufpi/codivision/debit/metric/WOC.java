package br.ufpi.codivision.debit.metric;

import java.math.BigDecimal;
import java.util.List;

import br.ufpi.codivision.debit.ast.AST;
import br.ufpi.codivision.debit.ast.AbstractField;
import br.ufpi.codivision.debit.ast.AbstractMethod;
import br.ufpi.codivision.debit.ast.AbstractType;

public class WOC implements IMetric {

	@Override
	public void calculate(AST ast) {
		for (AbstractType type : ast.getTypes())
			type.getMetrics().put(MetricID.WOC, calculate(type.getMethods(), type.getFields()));
	}

	public float calculate(List<AbstractMethod> methods, List<AbstractField> fields) {
		int publicMembers = 0;
		int functionalMembers = 0;

		for (AbstractField field : fields)
			if (field.getModifiers().contains("public"))
				publicMembers++;

		for (AbstractMethod method : methods) {
			if (method.getModifiers().contains("public")) {
				publicMembers++;
				if (!method.isAccessor())
					functionalMembers++;
			}
		}

		float result = publicMembers == 0 ? 0 : functionalMembers * 1.0f / publicMembers;
		return new BigDecimal(result).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
	}

	@Override
	public MetricID getId() {
		return MetricID.WOC;
	}

	@Override
	public MetricID[] getRequiredMetrics() {
		return null;
	}

}