package br.ufpi.codivision.debit.mining;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import br.ufpi.codivision.debit.ast.AST;
import br.ufpi.codivision.debit.ast.AbstractMethod;
import br.ufpi.codivision.debit.ast.AbstractType;
import br.ufpi.codivision.debit.codesmell.CodeSmellFactory;
import br.ufpi.codivision.debit.codesmell.CodeSmellID;
import br.ufpi.codivision.debit.codesmell.ICodeSmell;
import br.ufpi.codivision.debit.metric.IMetric;
import br.ufpi.codivision.debit.metric.MetricFactory;
import br.ufpi.codivision.debit.metric.MetricID;
import br.ufpi.codivision.debit.util.HashingUtils;

public class CodeAnalysisProcessor {


	private RepositoryMiner rm;
	
	public CodeAnalysisProcessor(RepositoryMiner miner) {
		rm = miner;
	}
	
	
	private Map<MetricID, IMetric> metrics = new LinkedHashMap<MetricID, IMetric>();
	private Map<CodeSmellID, ICodeSmell> codeSmells = new LinkedHashMap<CodeSmellID, ICodeSmell>();
	

	public Document processFile(String filename, AST ast) {
		
		if (rm.hasMetrics()) {
			for (IMetric metric : rm.getMetrics())
				visitMetric(metric);
		}

		if (rm.hasCodeSmells()) {
			for (ICodeSmell codeSmell : rm.getCodeSmells())
				visitCodeSmell(codeSmell);
		}	
		
		Document doc = new Document();
		doc.append("package", ast.getPackageDeclaration());
		doc.append("filename", filename);
		//doc.append("repository", new ObjectId(repoId));
		doc.append("filename_hash", HashingUtils.encodeToCRC32(filename));

		for (IMetric metric : metrics.values()) {
			metric.calculate(ast);
		}

		for (ICodeSmell codeSmell : codeSmells.values()) {
			codeSmell.detect(ast);
		}

		doc.append("metrics", ast.convertMetrics());
		List<Document> docMethods1 = new ArrayList<Document>();
		for (AbstractMethod method : ast.getMethods()) {
			docMethods1.add(new Document("name", method.getName()).append("metrics", method.convertMetrics())
					.append("code_smells", method.convertCodeSmells()));
		}
		doc.append("methods", docMethods1);

		List<Document> docTypes = new ArrayList<Document>();
		for (AbstractType type : ast.getTypes()) {
			List<Document> docMethods2 = new ArrayList<Document>();
			for (AbstractMethod method : type.getMethods()) {
				docMethods2.add(new Document("name", method.getName()).append("metrics", method.convertMetrics())
						.append("code_smells", method.convertCodeSmells()));
			}
			docTypes.add(new Document("name", type.getName()).append("metrics", type.convertMetrics())
					.append("code_smells", type.convertCodeSmells()).append("methods", docMethods2));
		}
		doc.append("types", docTypes);
		
		return doc;

	}

	private void visitMetric(IMetric metricParam) {
		if (metricParam.getRequiredMetrics() != null) {
			for (MetricID id: metricParam.getRequiredMetrics()) {
				if (!metrics.containsKey(id)) {
					IMetric metric = MetricFactory.getMetric(id);
					if (metric.getRequiredMetrics() == null)
						metrics.put(id, metric);
					else
						visitMetric(metric);
				}
			}
		}

		if (!metrics.containsKey(metricParam.getId()))
			metrics.put(metricParam.getId(), metricParam);
	}

	private void visitCodeSmell(ICodeSmell codeSmellParam) {
		if (codeSmellParam!=null && codeSmellParam.getRequiredMetrics() != null) {
			for (MetricID id : codeSmellParam.getRequiredMetrics()) {
				IMetric metric = MetricFactory.getMetric(id);
				visitMetric(metric);
			}
		}
		
		if (codeSmellParam!=null && codeSmellParam.getRequiredCodeSmells() != null) {
			for (CodeSmellID id : codeSmellParam.getRequiredCodeSmells()) {
				if (!codeSmells.containsKey(id)) {
					ICodeSmell codeSmell = CodeSmellFactory.getCodeSmell(id);
					if (codeSmell.getRequiredCodeSmells() == null)
						codeSmells.put(id, codeSmell);
					else
						visitCodeSmell(codeSmell);
				}
			}
		}

		if (codeSmellParam!=null && !codeSmells.containsKey(codeSmellParam.getId()))
			codeSmells.put(codeSmellParam.getId(), codeSmellParam);
	}




}