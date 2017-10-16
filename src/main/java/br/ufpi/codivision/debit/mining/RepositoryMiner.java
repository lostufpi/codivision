package br.ufpi.codivision.debit.mining;

import java.util.List;

import br.ufpi.codivision.debit.codesmell.ICodeSmell;
import br.ufpi.codivision.debit.metric.IMetric;
import br.ufpi.codivision.debit.parser.IParser;


public class RepositoryMiner {

	private String repositoryKey;
	private String repositoryPath;
	private String repositoryName;
	private String repositoryDescription;
	
	private List<IParser> parsers;
	private List<IMetric> metrics;
	private List<ICodeSmell> codeSmells;
	
	public RepositoryMiner() {
		
	}

	public boolean hasParsers() {
		return parsers != null && parsers.size() > 0;
	}
	
	public boolean hasMetrics() {
		return metrics != null && metrics.size() > 0;
	}
	
	public boolean hasCodeSmells() {
		return codeSmells != null && codeSmells.size() > 0;
	}
	
	
	
	/*** Getters and Setters ***/
	
	public String getRepositoryKey() {
		return repositoryKey;
	}

	public void setRepositoryKey(String repositoryKey) {
		this.repositoryKey = repositoryKey;
	}

	public String getRepositoryPath() {
		return repositoryPath;
	}

	public void setRepositoryPath(String repositoryPath) {
		this.repositoryPath = repositoryPath;
	}

	public String getRepositoryName() {
		return repositoryName;
	}

	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}

	public String getRepositoryDescription() {
		return repositoryDescription;
	}

	public void setRepositoryDescription(String repositoryDescription) {
		this.repositoryDescription = repositoryDescription;
	}

	public List<IParser> getParsers() {
		return parsers;
	}

	public void setParsers(List<IParser> parsers) {
		this.parsers = parsers;
	}

	public List<IMetric> getMetrics() {
		return metrics;
	}

	public void setMetrics(List<IMetric> metrics) {
		this.metrics = metrics;
	}

	public List<ICodeSmell> getCodeSmells() {
		return codeSmells;
	}

	public void setCodeSmells(List<ICodeSmell> codeSmells) {
		this.codeSmells = codeSmells;
	}

	
}