/**
 * 
 */
package br.ufpi.codivision.core.extractor.service;


import java.util.LinkedList;
import java.util.Queue;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import br.ufpi.codivision.core.extractor.model.Extraction;

/**
 * @author Werney Ayala
 *
 */
@Named
@ApplicationScoped
public class TaskService {
	
	private Queue<Extraction> taskQueue;
	
	public TaskService() {
		this.taskQueue = new LinkedList<>();
	}
	
	public void addTask(Extraction value) {
		this.taskQueue.add(value);
	}
	
	public Extraction getFirstTask() {
		return this.taskQueue.poll();
	}
	
	

	/**
	 * @return the taskQueue
	 */
	public Queue<Extraction> getTaskQueue() {
		return taskQueue;
	}

	/**
	 * @param taskQueue the taskQueue to set
	 */
	public void setTaskQueue(Queue<Extraction> taskQueue) {
		this.taskQueue = taskQueue;
	}


}