package org.sopera.monitoring.service;

import java.util.List;

import org.sopera.monitoring.event.Event;

/**
 * Public interface for the business logic of MonitoringService
 * 
 * @author cschmuelling
 *
 */
public interface MonitoringService<E extends Event> {
	
	/**
	 * Handle the event with all configured handlers.
	 * @param events
	 */
	public void putEvents(List<E> events);
}
