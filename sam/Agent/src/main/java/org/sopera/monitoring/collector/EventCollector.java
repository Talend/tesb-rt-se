package org.sopera.monitoring.collector;

import org.sopera.monitoring.event.Event;

public interface EventCollector {

	/**
	 * Puts events in the queue for sending
	 * @param event
	 */
	public void putEvent(Event event);

	/**
	 * Synchronous method for sending events to monitoring service. Normally Spring scheduler will execute this method!
	 */
	public void sendEventsFromQueue();

	/**
	 * Set a flag to stop sending new events to the monitoring service. The running executors will be not interrupted.
	 */
	public void stopSending();

}
