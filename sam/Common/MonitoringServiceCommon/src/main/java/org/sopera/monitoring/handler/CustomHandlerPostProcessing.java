package org.sopera.monitoring.handler;

import org.sopera.monitoring.event.Event;

/**
 * Custom post handler will be executetd after events are persisted. You can write your own post handler and add it in monitoringService.xml
 *  
 * @author cschmuelling
 *
 */
public interface CustomHandlerPostProcessing<E extends Event> extends FilteredHandler<E> {
	
	/**
	 * handleEvent is called for each event. 
	 *  
	 * @param event
	 * @param persistanceHandler
	 */
	void handleEvent(E event, PersistenceHandler<E> persistenceHandler);
}
