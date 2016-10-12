/**
 * 
 */
package br.ufpi.codivision.core.extractor.service;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.ufpi.codivision.common.model.PersistenceEntity;

/**
 * @author Werney Ayala
 *
 */
@Named
@ApplicationScoped
public class ThreadExecutor {
	
	private ExecutorService executorService;
	private CompletionService<PersistenceEntity> completionService;
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	/**
	 * 
	 */
	public ThreadExecutor() {
		
		int avaliableProcessors = Runtime.getRuntime().availableProcessors();
		this.executorService = Executors.newFixedThreadPool(avaliableProcessors);
		this.completionService = new ExecutorCompletionService<PersistenceEntity>(executorService);
		
		log.info("Starting service with " + avaliableProcessors + " simultaneous tasks");
	}
	
	public void add(Callable<PersistenceEntity> thread) {
		this.completionService.submit(thread);
	}
	
	public Future<PersistenceEntity> getFirstDone() {
		return this.completionService.poll();
	}

	/**
	 * @return the completionService
	 */
	public CompletionService<PersistenceEntity> getCompletionService() {
		return completionService;
	}

	/**
	 * @param completionService the completionService to set
	 */
	public void setCompletionService(CompletionService<PersistenceEntity> completionService) {
		this.completionService = completionService;
	}

	/**
	 * @return the executorService
	 */
	public ExecutorService getExecutorService() {
		return executorService;
	}

	/**
	 * @param executorService the executorService to set
	 */
	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}
	
}
