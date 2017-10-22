package br.ufpi.codivision.debit.mining;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.ufpi.codivision.debit.ast.AST;
import br.ufpi.codivision.debit.ast.AbstractMethod;
import br.ufpi.codivision.debit.ast.AbstractType;
import br.ufpi.codivision.debit.codesmell.CodeSmellFactory;
import br.ufpi.codivision.debit.codesmell.CodeSmellID;
import br.ufpi.codivision.debit.codesmell.ICodeSmell;
import br.ufpi.codivision.debit.metric.IMetric;
import br.ufpi.codivision.debit.metric.MetricFactory;
import br.ufpi.codivision.debit.metric.MetricID;
import br.ufpi.codivision.debit.model.CodeSmell;
import br.ufpi.codivision.debit.model.CodeSmellMethod;
import br.ufpi.codivision.debit.model.File;
import br.ufpi.codivision.debit.model.Method;
import br.ufpi.codivision.debit.model.Metric;
import br.ufpi.codivision.debit.model.MetricMethod;
import br.ufpi.codivision.debit.parser.java.JavaParser;

public class CodeAnalysisProcessor {


	private RepositoryMiner rm;
	private Map<MetricID, IMetric> metrics = new LinkedHashMap<MetricID, IMetric>();
	private Map<CodeSmellID, ICodeSmell> codeSmells = new LinkedHashMap<CodeSmellID, ICodeSmell>();
	
	public CodeAnalysisProcessor() {
		RepositoryMiner repositoryMiner = new RepositoryMiner();
		
		List<ICodeSmell> codeSmells = new ArrayList<>();
		
		for(CodeSmellID code: CodeSmellID.values()) {
			codeSmells.add(CodeSmellFactory.getCodeSmell(code));
		}
		
		List<IMetric> metricsCode = new ArrayList<>();
		
		for(MetricID metri: MetricID.values()) {
			metricsCode.add(MetricFactory.getMetric(metri));
		}
		
		
		repositoryMiner.setMetrics(metricsCode);
		repositoryMiner.setCodeSmells(codeSmells);
		
		rm = repositoryMiner;
	}

	public File processFile(String code) {
		
		JavaParser parser = new JavaParser();
		AST ast = parser.generate("", code);
		
		File file = new File();
		
		
		long qntBadSmell = code.split("//TODO").length - 1;
		qntBadSmell = qntBadSmell + code.split("//FIXME").length - 1;
		qntBadSmell = qntBadSmell + code.split("//XXX").length - 1;
		
		file.setQntBadSmellComment(qntBadSmell);
		
		if (rm.hasMetrics()) {
			for (IMetric metric : rm.getMetrics())
				visitMetric(metric);
		}

		if (rm.hasCodeSmells()) {
			for (ICodeSmell codeSmell : rm.getCodeSmells())
				visitCodeSmell(codeSmell);
		}	

		for (IMetric metric : metrics.values()) {
			metric.calculate(ast);
		}

		for (ICodeSmell codeSmell : codeSmells.values()) {
			codeSmell.detect(ast);
		}

		for (AbstractType type : ast.getTypes()) {
			
			String path = type.getName().replaceAll("[.]", "/");
			file.setPath(path);
			
			
			List<Method> methods = new ArrayList<>();
			for (AbstractMethod method : type.getMethods()) {
				
				Method myMethod = new Method();
				myMethod.setName(method.getName());
				
				Map<MetricID, Object> convertMetrics = method.getMetrics();
				
				for(MetricID key: convertMetrics.keySet()) {
					MetricMethod m = new MetricMethod();
					m.setMetricType(key);
					m.setQnt(convertMetrics.get(key) + "");
					myMethod.getCodeMetrics().add(m);
				}
				
				
				for(CodeSmellID codesmell: method.getCodeSmells()) {
					CodeSmellMethod cs = new CodeSmellMethod();
					cs.setCodeSmellType(codesmell);
					myMethod.getCodeSmells().add(cs);
				}
				
				methods.add(myMethod);
			}
			
			file.setMethods(methods);
			
			
			Map<MetricID, Object> convertMetrics = type.getMetrics();
			
			for(MetricID key: convertMetrics.keySet()) {
				Metric m = new Metric();
				m.setMetricType(key);
				m.setQnt(convertMetrics.get(key) + "");
				file.getCodeMetrics().add(m);
			}
			
			for(CodeSmellID codesmell: type.getCodeSmells()) {
				CodeSmell cs = new CodeSmell();
				cs.setCodeSmellType(codesmell);
				file.getCodeSmells().add(cs);
			}
			
		}
		
		return file;

	}

	private void visitMetric(IMetric metricParam) {
		if (metricParam!= null && metricParam.getRequiredMetrics() != null) {
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

		if (metricParam!= null && !metrics.containsKey(metricParam.getId()))
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