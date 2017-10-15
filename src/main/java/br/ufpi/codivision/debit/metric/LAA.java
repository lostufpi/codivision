package br.ufpi.codivision.debit.metric;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import br.ufpi.codivision.debit.ast.AST;
import br.ufpi.codivision.debit.ast.AbstractFieldAccess;
import br.ufpi.codivision.debit.ast.AbstractMethod;
import br.ufpi.codivision.debit.ast.AbstractMethodInvocation;
import br.ufpi.codivision.debit.ast.AbstractStatement;
import br.ufpi.codivision.debit.ast.AbstractType;
import br.ufpi.codivision.debit.ast.NodeType;

public class LAA implements IMetric {

	@Override
	public void calculate(AST ast) {
		for (AbstractType type : ast.getTypes())
			for (AbstractMethod method : type.getMethods())
				method.getMetrics().put(MetricID.LAA, calculate(type, method));
	}

	public float calculate(AbstractType type, AbstractMethod method) {
		int countFields = countAccessedFields(method);
		float result = countFields > 0 ? (type.getFields().size() * 1.0f) / countFields : 0;
		return new BigDecimal(result).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
	}

	public static int countAccessedFields(AbstractMethod method) {
		Set<String> accessedFields = new HashSet<String>();
		for (AbstractStatement stmt : method.getStatements()) {
			if (stmt.getNodeType() == NodeType.FIELD_ACCESS) {
				AbstractFieldAccess fieldAccess = (AbstractFieldAccess) stmt;
				accessedFields.add(fieldAccess.getDeclaringClass() + '.' + fieldAccess.getExpression());
			} else if (stmt.getNodeType() == NodeType.METHOD_INVOCATION) {
				AbstractMethodInvocation methodInvocation = (AbstractMethodInvocation) stmt;
				if (methodInvocation.isAccessor())
					accessedFields.add(methodInvocation.getDeclaringClass() + '.' + methodInvocation.getExpression());
			} else
				continue;
		}
		return accessedFields.size();
	}

	@Override
	public MetricID getId() {
		return MetricID.LAA;
	}

	@Override
	public MetricID[] getRequiredMetrics() {
		return null;
	}

}