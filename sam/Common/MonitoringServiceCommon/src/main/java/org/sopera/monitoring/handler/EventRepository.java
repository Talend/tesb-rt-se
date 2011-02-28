package org.sopera.monitoring.handler;

import org.sopera.monitoring.event.Event;

public interface EventRepository {

	/**
	 * Write event to database
	 * 
	 * @param events
	 */
	public void writeEvent(Event event);

}
