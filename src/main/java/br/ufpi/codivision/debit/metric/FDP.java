package br.ufpi.codivision.debit.metric;

import java.util.HashSet;
import java.util.Set;

import br.ufpi.codivision.debit.ast.AST;
import br.ufpi.codivision.debit.ast.AbstractFieldAccess;
import br.ufpi.codivision.debit.ast.AbstractMethod;
import br.ufpi.codivision.debit.ast.AbstractMethodInvocation;
import br.ufpi.codivision.debit.ast.AbstractStatement;
import br.ufpi.codivision.debit.ast.AbstractType;
import br.ufpi.codivision.debit.ast.NodeType;

public class FDP implements IMetric {

	@Override
	public void calculate(AST ast) {
		for (AbstractType type : ast.getTypes())
			for (AbstractMethod method : type.getMethods())
				method.getMetrics().put(MetricID.FDP, calculate(type, method));
	}
	
	public int calculate(AbstractType currType, AbstractMethod method) {
		Set<String> accessedClasses = new HashSet<String>();
		for (AbstractStatement stmt : method.getStatements()) {
			String declarringClass = null;

			if (stmt.getNodeType() == NodeType.FIELD_ACCESS) {
				AbstractFieldAccess fieldAccess = (AbstractFieldAccess) stmt;
				declarringClass = fieldAccess.getDeclaringClass();
			} else if (stmt.getNodeType() == NodeType.METHOD_INVOCATION) {
				AbstractMethodInvocation methodInvocation = (AbstractMethodInvocation) stmt;
				if (!methodInvocation.isAccessor())
					continue;
				
				declarringClass = methodInvocation.getDeclaringClass();
			} else
				continue;

			if (!currType.getName().equals(declarringClass))
				accessedClasses.add(declarringClass);
		}

		return accessedClasses.size();
	}

	@Override
	public MetricID getId() {
		return MetricID.FDP;
	}

	@Override
	public MetricID[] getRequiredMetrics() {
		return null;
	}

}