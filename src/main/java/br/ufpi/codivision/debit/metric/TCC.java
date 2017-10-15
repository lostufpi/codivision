package br.ufpi.codivision.debit.metric;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.ufpi.codivision.debit.ast.AST;
import br.ufpi.codivision.debit.ast.AbstractFieldAccess;
import br.ufpi.codivision.debit.ast.AbstractMethod;
import br.ufpi.codivision.debit.ast.AbstractMethodInvocation;
import br.ufpi.codivision.debit.ast.AbstractStatement;
import br.ufpi.codivision.debit.ast.AbstractType;
import br.ufpi.codivision.debit.ast.NodeType;

public class TCC implements IMetric {

	@Override
	public void calculate(AST ast) {
		for (AbstractType type : ast.getTypes())
			type.getMetrics().put(MetricID.TCC, calculate(type));
	}

	public float calculate(AbstractType type) {
		List<AbstractMethod> methodList = new ArrayList<AbstractMethod>();
		for (AbstractMethod m : type.getMethods())
			if (!(m.getModifiers().contains("abstract") || m.isConstructor()))
				methodList.add(m);

		int n = methodList.size();
		int npc = (n * (n - 1)) / 2;
		int ndc = 0;

		List<List<String>> accessedFieldsByMethods = new ArrayList<List<String>>();
		for (AbstractMethod method : methodList)
			accessedFieldsByMethods.add(processAccessedFields(type, method));

		for (int i = 0; i < accessedFieldsByMethods.size(); i++)
			for (int j = i + 1; j < accessedFieldsByMethods.size(); j++)
				if (isConnected(accessedFieldsByMethods.get(i), accessedFieldsByMethods.get(j)))
					ndc++;

		float result = npc > 0 ? ndc * 1.0f / npc : 0;
		return new BigDecimal(result).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
	}

	public List<String> processAccessedFields(AbstractType currType, AbstractMethod method) {
		Set<String> fields = new HashSet<String>();
		for (AbstractStatement stmt : method.getStatements()) {
			if (stmt.getNodeType() == NodeType.FIELD_ACCESS) {
				AbstractFieldAccess fdAccess = (AbstractFieldAccess) stmt;
				if (currType.getName().equals(fdAccess.getDeclaringClass()))
					fields.add(stmt.getExpression());
			} else if (stmt.getNodeType() == NodeType.METHOD_INVOCATION) {
				AbstractMethodInvocation methodInv = (AbstractMethodInvocation) stmt;
				if (methodInv.isAccessor() && currType.getName().equals(methodInv.getDeclaringClass()))
					fields.add(methodInv.getAccessedField());
			} else
				continue;
		}
		return new ArrayList<String>(fields);
	}

	private boolean isConnected(List<String> method1, List<String> method2) {
		for (String field : method1)
			if (method2.contains(field))
				return true;
		return false;
	}

	@Override
	public MetricID getId() {
		return MetricID.TCC;
	}

	@Override
	public MetricID[] getRequiredMetrics() {
		return null;
	}

}