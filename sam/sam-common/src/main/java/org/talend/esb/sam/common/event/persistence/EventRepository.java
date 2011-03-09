package org.talend.esb.sam.common.event.persistence;

import org.talend.esb.sam.common.event.Event;


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
