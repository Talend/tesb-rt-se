package org.sopera.monitoring.server.service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.sopera.monitoring.event.Event;
import org.sopera.monitoring.event.persistence.EventRepository;
import org.sopera.monitoring.event.service.MonitoringService;
import org.sopera.monitoring.filter.EventFilter;
import org.sopera.monitoring.handler.EventManipulator;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of MonitoringService. This service needs all handler for
 * manipulating events.
 * 
 */
public class MonitoringServiceImpl implements MonitoringService {

	private static Logger logger = Logger.getLogger(MonitoringServiceImpl.class
			.getName());

	private List<EventFilter<Event>> eventFilter;
	private List<EventManipulator<Event>> eventManipulator;
	private EventRepository persistenceHandler;

	/**
	 * Sets a list of event filter. A filtered event will not processed.
	 * 
	 * @param eventFilter
	 */
	public void setEventFilter(List<EventFilter<Event>> eventFilter) {
		this.eventFilter = eventFilter;
	}

	/**
	 * Sets a list of event manipulator. Normally it's used for password
	 * filtering and cutting the content.
	 * 
	 * @param eventManipulator
	 */
	public void setEventManipulator(List<EventManipulator<Event>> eventManipulator) {
		this.eventManipulator = eventManipulator;
	}


	/**
	 * Set a persistence handler. For example the DefaultDatabaseHandler
	 * 
	 * @param persistenceHandler
	 */
	public void setPersistenceHandler(EventRepository persistenceHandler) {
		this.persistenceHandler = persistenceHandler;
	}

	/**
	 * Executes all event manipulating handler and writes the event with persist
	 * handler
	 */
	@Transactional
	public void putEvents(List<Event> events) {
		List<Event> filteredEvents = new ArrayList<Event>();
		if (eventFilter != null && eventFilter.size() > 0) {
			for (EventFilter<Event> filter : eventFilter) {
				for (Event event : events) {
					if (!filter.filter(event))
						filteredEvents.add(event);
				}
			}
			if (filteredEvents.size() == 0)
				return;
		} else {
			filteredEvents = events;
		}

		// Execute Filter (for example password filter and cutting content
		if (eventManipulator != null && eventManipulator.size() > 0) {
			for (EventManipulator<Event> current : eventManipulator) {
				for (Event event : filteredEvents) {
				    current.handleEvent(event);
				}
			}
		}
		
		for (Event event : filteredEvents) {
                    persistenceHandler.writeEvent(event);
                }
	}
}
