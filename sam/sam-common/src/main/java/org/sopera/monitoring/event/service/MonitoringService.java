package org.sopera.monitoring.event.service;

import java.util.List;

import org.sopera.monitoring.event.Event;


/**
 * Public interface for the business logic of MonitoringService
 */
public interface MonitoringService {
	
	/**
	 * Handle the event with all configured handlers.
	 * @param events
	 */
	public void putEvents(List<Event> events);
}
