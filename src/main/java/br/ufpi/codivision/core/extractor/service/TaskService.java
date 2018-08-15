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
	private Queue<Extraction> taskQueueUpdate;
	
	public TaskService() {
		this.taskQueue = new LinkedList<>();
		this.taskQueueUpdate = new LinkedList<>();
	}
	
	public void addTask(Extraction value) {
		this.taskQueue.add(value);
	}
	
	public Extraction getFirstTask() {
		return this.taskQueue.poll();
	}
	
	public void addTaskUpdate(Extraction value) {
		this.taskQueueUpdate.add(value);
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

	public Queue<Extraction> getTaskQueueUpdate() {
		return taskQueueUpdate;
	}

	public void setTaskQueueUpdate(Queue<Extraction> taskQueueUpdate) {
		this.taskQueueUpdate = taskQueueUpdate;
	}
	
	public Extraction getFirstTaskUpdate() {
		return this.taskQueueUpdate.poll();
	}


}