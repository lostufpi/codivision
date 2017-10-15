package br.ufpi.codivision.debit.metric;

import java.util.ArrayList;
import java.util.List;

import br.ufpi.codivision.debit.ast.AST;
import br.ufpi.codivision.debit.ast.AbstractMethod;
import br.ufpi.codivision.debit.ast.AbstractStatement;
import br.ufpi.codivision.debit.ast.AbstractType;
import br.ufpi.codivision.debit.ast.NodeType;

public class LVAR implements IMetric {

	private static final MetricID ID = MetricID.LVAR;

	@Override
	public void calculate(AST ast) {
		for (AbstractMethod method : ast.getMethods())
			method.getMetrics().put(ID, calculate(method));

		for (AbstractType type : ast.getTypes())
			for (AbstractMethod method : type.getMethods())
				method.getMetrics().put(ID, calculate(method));
	}

	public int calculate(AbstractMethod method) {
		List<String> lvar = new ArrayList<String>();
		for (AbstractStatement statement : method.getStatements())
			if (statement.getNodeType() == NodeType.VARIABLE_DECLARATION && !lvar.contains(statement.getExpression()))
				lvar.add(statement.getExpression());
		
		return lvar.size();
	}

	@Override
	public MetricID getId() {
		return MetricID.LVAR;
	}

	@Override
	public MetricID[] getRequiredMetrics() {
		return null;
	}

}