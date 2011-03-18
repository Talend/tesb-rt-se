/*
 * #%L
 * Service Activity Monitoring :: Server
 * %%
 * Copyright (C) 2011 Talend Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.talend.esb.sam.server.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.event.persistence.EventRepository;
import org.talend.esb.sam.common.service.MonitoringService;
import org.talend.esb.sam.common.spi.EventFilter;
import org.talend.esb.sam.common.spi.EventManipulator;

/**
 * Implementation of MonitoringService. This service needs all handler for
 * manipulating events.
 * 
 */
public class MonitoringServiceImpl implements MonitoringService {

	private List<EventFilter> eventFilter;
	private List<EventManipulator> eventManipulator;
	private EventRepository persistenceHandler;

	/**
	 * Sets a list of event filter. A filtered event will not processed.
	 * 
	 * @param eventFilter
	 */
	public void setEventFilter(List<EventFilter> eventFilter) {
		this.eventFilter = eventFilter;
	}

	/**
	 * Sets a list of event manipulator. Normally it's used for password
	 * filtering and cutting the content.
	 * 
	 * @param eventManipulator
	 */
	public void setEventManipulator(List<EventManipulator> eventManipulator) {
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
		
		// Execute Filter
		if (eventFilter != null && eventFilter.size() > 0) {
			for (EventFilter filter : eventFilter) {
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

		// Execute Manipulator
		if (eventManipulator != null && eventManipulator.size() > 0) {
			for (EventManipulator current : eventManipulator) {
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
