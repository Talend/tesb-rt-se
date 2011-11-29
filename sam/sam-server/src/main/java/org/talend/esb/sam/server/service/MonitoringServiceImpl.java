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

import org.springframework.beans.factory.annotation.Autowired;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.event.persistence.EventRepository;
import org.talend.esb.sam.common.service.MonitoringService;
import org.talend.esb.sam.common.spi.EventFilter;
import org.talend.esb.sam.common.spi.EventHandler;

/**
 * Implementation of MonitoringService. This service needs all handler for
 * manipulating events.
 * 
 */
public class MonitoringServiceImpl implements MonitoringService {

    @Autowired(required=false)
    private List<EventFilter> eventFilters = new ArrayList<EventFilter>();
    @Autowired(required=false)
    private List<EventHandler> eventHandlers = new ArrayList<EventHandler>();
    private EventRepository persistenceHandler;

    /**
     * Sets a list of event filter. A filtered event will not processed.
     * 
     * @param eventFilter
     */
    public void setEventFilters(List<EventFilter> eventFilters) {
        this.eventFilters = eventFilters;
    }

    /**
     * Sets a list of event manipulator. Normally it's used for password
     * filtering and cutting the content.
     * 
     * @param eventManipulator
     */
    public void setEventHandlers(List<EventHandler> eventHandlers) {
        this.eventHandlers = eventHandlers;
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
    public void putEvents(List<Event> events) {
        List<Event> filteredEvents = filterEvents(events);
        executeHandlers(filteredEvents);
        for (Event event : filteredEvents) {
            persistenceHandler.writeEvent(event);
        }
    }

    /**
     * Execute all filters for the event.
     * 
     * @param event
     * @return
     */
    private boolean filter(Event event) {
        for (EventFilter filter : eventFilters) {
            if (filter.filter(event) == true)
                return true;
        }
        return false;
    }

    private List<Event> filterEvents(List<Event> events) {
        List<Event> filteredEvents = new ArrayList<Event>();
        for (Event event : events) {
            if (!filter(event)) {
                filteredEvents.add(event);
            }
        }
        return filteredEvents;
    }

    private void executeHandlers(List<Event> filteredEvents) {
        for (EventHandler current : eventHandlers) {
            for (Event event : filteredEvents) {
                current.handleEvent(event);
            }
        }
    }
}
