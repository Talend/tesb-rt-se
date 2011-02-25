package org.sopera.monitoring.queue;

import java.io.Serializable;

import org.sopera.monitoring.exception.MonitoringException;

public interface Queue<O extends Serializable> {

	/**
	 * Adds a new object to the queue
	 * @param object
	 */
	public void add(O object) throws MonitoringException;
	
	/**
	 * Returns true if queue is empty
	 * @return
	 */
	public boolean isEmpty();
	
	/**
	 * Returns one element from queue and removes it.
	 * @return
	 */
	public O remove() throws MonitoringException;
}
