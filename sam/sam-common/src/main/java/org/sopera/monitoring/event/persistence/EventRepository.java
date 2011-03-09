package org.sopera.monitoring.event.persistence;

import org.sopera.monitoring.event.Event;


public interface EventRepository {

	/**
	 * Write event to database
	 * 
	 * @param events
	 */
	public void writeEvent(Event event);
	
	/**
	 * Read event from database
	 * @param id
	 * @return
	 */
	public Event readEvent(long id);

}
