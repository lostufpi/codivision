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
	private Queue<Long> toUpdateQueue;
	private Long updating;
	
	public TaskService() {
		this.taskQueue = new LinkedList<>();
		this.toUpdateQueue = new LinkedList<>();
	}
	
	public void addTask(Extraction value) {
		this.taskQueue.add(value);
	}
	
	public Extraction getFirstTask() {
		return this.taskQueue.poll();
	}
	
	public void addToUpdate(Long value) {
		this.toUpdateQueue.add(value);
	}
	
	public Long getFirstToUpdate() {
		return this.toUpdateQueue.poll();
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

	/**
	 * @return the toUpdateQueue
	 */
	public Queue<Long> getToUpdateQueue() {
		return toUpdateQueue;
	}

	/**
	 * @param toUpdateQueue the toUpdateQueue to set
	 */
	public void setToUpdateQueue(Queue<Long> toUpdateQueue) {
		this.toUpdateQueue = toUpdateQueue;
	}

	/**
	 * @return the updating
	 */
	public Long getUpdating() {
		return updating;
	}

	/**
	 * @param updating the updating to set
	 */
	public void setUpdating(Long updating) {
		this.updating = updating;
	}

}
