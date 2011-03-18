/*
 * #%L
 * Service Activity Monitoring :: Agent
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
package org.talend.esb.sam.agent.collector;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.buslifecycle.BusLifeCycleListener;
import org.apache.cxf.buslifecycle.BusLifeCycleManager;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.event.MonitoringException;
import org.talend.esb.sam.common.service.MonitoringService;
import org.talend.esb.sam.common.spi.EventFilter;
import org.talend.esb.sam.common.spi.EventManipulator;

/**
 * Event collector collects all events and stores them in a queue. This can be a
 * memory queue or a persistent queue. Asynchronously the events will be
 * processed and sent to MonitoringService
 */
public class EventCollectorImpl implements EventManipulator, BusLifeCycleListener {

	private static Logger logger = Logger.getLogger(EventCollectorImpl.class
			.getName());

	private MonitoringService monitoringServiceClient;
	
	@Resource(name="eventFilters")
	private List<EventFilter> eventFilters = new ArrayList<EventFilter>();
	@Resource(name="eventHandlers")
	private List<EventManipulator> eventHandlers = new ArrayList<EventManipulator>();
	
	private Queue<Event> queue;
	private TaskExecutor executor;
	private TaskScheduler scheduler;
	private long defaultInterval = 1000;
	private boolean stopMessageFlowOnError = false;
	private int eventsPerMessageCall = 10;
	private boolean stopSending = false;
	
	public EventCollectorImpl() {
	    Bus bus = BusFactory.getThreadDefaultBus();
	    BusLifeCycleManager lm = bus.getExtension(BusLifeCycleManager.class);
            if (null != lm) {
                lm.registerLifeCycleListener(this);
            }
        }

	/**
	 * Returns the number of events sent by one service call.
	 * 
	 * @return
	 */
	public int getEventsPerMessageCall() {
		if (eventsPerMessageCall <= 0) {
			logger.warning("Message package size is not set or is lower then 1. Set package size to 1.");
			return 1;
		}
		return eventsPerMessageCall;
	}

	/**
	 * Set by Spring. Define how many events will be sent within one service
	 * call.
	 * 
	 * @param eventsPerMessageCall
	 */
	public void setEventsPerMessageCall(int eventsPerMessageCall) {
		this.eventsPerMessageCall = eventsPerMessageCall;
	}

	/**
	 * Set by Spring. Stops the normal message flow if storing of event causes
	 * errors. After storing event in queue there is no way to stop the message
	 * flow.
	 * 
	 * @param stopMessageFlowOnError
	 */
	public void setStopMessageFlowOnError(boolean stopMessageFlowOnError) {
		this.stopMessageFlowOnError = stopMessageFlowOnError;
	}

	/**
	 * Returns the default interval for sending events
	 * 
	 * @return
	 */
	private long getDefaultInterval() {
		return defaultInterval;
	}

	/**
	 * Set default interval for sending events to monitoring service.
	 * DefaultInterval will be used by scheduler.
	 * 
	 * @param defaultInterval
	 */
	public void setDefaultInterval(long defaultInterval) {
		this.defaultInterval = defaultInterval;
	}

	/**
	 * Scheduler will be set and configured by Spring. Spring executes every x
	 * milliseconds the sending process.
	 * 
	 * @param scheduler
	 */
	public void setScheduler(TaskScheduler scheduler) {
		logger.info("Scheduler startet for sending events to monitoring service");
		this.scheduler = scheduler;

		this.scheduler.scheduleAtFixedRate(new Runnable() {

			public void run() {
				sendEventsFromQueue();
			}
		}, getDefaultInterval());
	}

	/**
	 * Spring sets the executor. The executer is used for sending events to the
	 * web service.
	 * 
	 * @param executor
	 */
	public void setExecutor(TaskExecutor executor) {
		this.executor = executor;
	}

	/**
	 * Spring sets the queue. Within the spring configuration you can decide
	 * between memory queue and persistent queue.
	 * 
	 * @param queue
	 */
	public void setQueue(Queue<Event> queue) {
		this.queue = queue;
	}

	/**
	 * Spring sets event filter. Event filter will be processed before sending
	 * events to web service.
	 * 
	 * @param queue
	 */
	public void setEventFilters(List<EventFilter> eventFilters) {
		this.eventFilters = eventFilters;
	}

    /**
	 * 
	 * @param eventHandlers
	 */
	public void setEventHandlers(
			List<EventManipulator> eventHandlers) {
		this.eventHandlers = eventHandlers;
	}

	public List<EventFilter> getEventFilters() {
		return eventFilters;
	}

	public List<EventManipulator> getEventHandlers() {
		return eventHandlers;
	}

	/**
	 * Spring sets the monitoring service client.
	 * 
	 * @param monitoringServiceClient
	 */
	public void setMonitoringServiceClient(
			MonitoringService monitoringServiceClient) {
		this.monitoringServiceClient = monitoringServiceClient;
	}

	/**
	 * Stores an event in the queue and returns. So the synchronous execution of
	 * this service is as short as possible.
	 */
	@Override
	public void handleEvent(Event event) {
		String id = (event.getMessageInfo() != null) ? event.getMessageInfo().getMessageId() : null;
		logger.fine("Store event [message_id=" + id + "] in cache.");
		try {
			queue.add(event);
		} catch (RuntimeException e) {
			if (stopMessageFlowOnError) {
				throw e;
			} else {
				logger.severe("Could not store event in Queue: " + e.getMessage());
			}
		}
	}

	/**
	 * Method will be executed asynchronously from spring.
	 */
	public void sendEventsFromQueue() {
		if(this.stopSending == true){
			return;
		}
		logger.fine("Scheduler called for sending events");

		int packageSize = getEventsPerMessageCall();

		while (!queue.isEmpty()) {
			final List<Event> list = new ArrayList<Event>();
			int i = 0;
			while (i < packageSize && !queue.isEmpty()) {
				Event event = queue.remove();
				if (event!= null && !filter(event)) {
					list.add(event);
					i++;
				}
			}
			if (list.size() > 0) {

				// Need to call executer because @Async wouldn't work
				// through proxy. This Method is inside a proxy and local
				// proxy calls are not supported
				executor.execute(new Runnable() {
					public void run() {
						try{
							sendEvents(list);
						}catch (MonitoringException e){
							e.logException(Level.SEVERE);
						}
					}
				});

			}
		}

	}

	/**
	 * Execute all filters for the event.
	 * 
	 * @param event
	 * @return
	 */
	private boolean filter(Event event) {
		if (eventFilters != null && eventFilters.size() > 0) {
			for (EventFilter filter : eventFilters) {
				if (filter.filter(event) == true)
					return true;
			}
		}
		return false;
	}

	/**
	 * Sends the events to monitoring service client.
	 * 
	 * @param events
	 */
	private void sendEvents(final List<Event> events) {
		// Execute Manipulator
		if (eventHandlers != null && eventHandlers.size() > 0) {
			for (EventManipulator current : eventHandlers) {
				for (Event event : events) {
				    current.handleEvent(event);
				}
			}
		}

		logger.info("Put events(" + events.size() + ") to Monitoring Server.");
		try {
	        monitoringServiceClient.putEvents(events);
		} catch (Exception e) {
			if (e instanceof MonitoringException)
				throw (MonitoringException) e;
			throw new MonitoringException("002",
					"Unknown error while execute put events to Monitoring Server", e);
		}

	}
	
	public void stopSending(){
		
	}

    @Override
    public void initComplete() {
        // Ignore
    }

    @Override
    public void preShutdown() {
        logger.info("Bus is stopping. Stopping sending events to monitoring service.");
        this.stopSending = true;
    }

    @Override
    public void postShutdown() {
        // Ignore
    }
}
