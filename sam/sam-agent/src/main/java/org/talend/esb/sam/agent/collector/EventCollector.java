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

import org.apache.cxf.Bus;
import org.apache.cxf.buslifecycle.BusLifeCycleListener;
import org.apache.cxf.buslifecycle.BusLifeCycleManager;
import org.apache.cxf.endpoint.ClientLifeCycleManager;
import org.apache.cxf.endpoint.ServerLifeCycleManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;

import org.talend.esb.sam.agent.lifecycle.ClientListenerImpl;
import org.talend.esb.sam.agent.lifecycle.ServiceListenerImpl;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.event.MonitoringException;
import org.talend.esb.sam.common.service.MonitoringService;
import org.talend.esb.sam.common.spi.EventFilter;
import org.talend.esb.sam.common.spi.EventHandler;

/**
 * EventCollector reads the events from Queue. 
 * After processing with filter/handler, the events will be sent to SAM Server periodically.
 */
public class EventCollector implements BusLifeCycleListener {

    private static final Logger LOG = Logger.getLogger(EventCollector.class.getName());

    private Bus bus;
    private MonitoringService monitoringServiceClient;
    @Autowired(required = false)
    private List<EventFilter> filters = new ArrayList<EventFilter>();

    private List<EventHandler> handlers = new ArrayList<EventHandler>();
    private Queue<Event> queue;
    private TaskExecutor executor;
    private TaskScheduler scheduler;
    private long defaultInterval = 1000;
    private int eventsPerMessageCall = 10;
    private boolean sendLifecycleEvent;
    private boolean stopSending;

    /**
     * Instantiates a new event collector.
     */
    public EventCollector() {
        //init Bus and LifeCycle listeners
        if (bus != null) {
            BusLifeCycleManager lm = bus.getExtension(BusLifeCycleManager.class);
            if (null != lm) {
                lm.registerLifeCycleListener(this);
            }

            if (sendLifecycleEvent) {
                ServerLifeCycleManager slcm = bus.getExtension(ServerLifeCycleManager.class);
                if (null != slcm) {
                    ServiceListenerImpl svrListener = new ServiceListenerImpl();
                    svrListener.setSendLifecycleEvent(sendLifecycleEvent);
                    svrListener.setQueue(queue);
                    svrListener.setMonitoringServiceClient(monitoringServiceClient);
                    slcm.registerListener(svrListener);
                }

                ClientLifeCycleManager clcm = bus.getExtension(ClientLifeCycleManager.class);
                if (null != clcm) {
                    ClientListenerImpl cltListener = new ClientListenerImpl();
                    cltListener.setSendLifecycleEvent(sendLifecycleEvent);
                    cltListener.setQueue(queue);
                    cltListener.setMonitoringServiceClient(monitoringServiceClient);
                    clcm.registerListener(cltListener);
                }
            }
        }
    }

    /**
     * Returns the number of events sent by one service call.
     *
     * @return the events per message call
     */
    public int getEventsPerMessageCall() {
        if (eventsPerMessageCall <= 0) {
            LOG.warning("Message package size is not set or is lower then 1. Set package size to 1.");
            return 1;
        }
        return eventsPerMessageCall;
    }

    /**
     * Set by Spring. Define how many events will be sent within one service call.
     *
     * @param eventsPerMessageCall the new events per message call
     */
    public void setEventsPerMessageCall(int eventsPerMessageCall) {
        this.eventsPerMessageCall = eventsPerMessageCall;
    }

    /**
     * Returns the default interval for sending events.
     *
     * @return the default interval
     */
    private long getDefaultInterval() {
        return defaultInterval;
    }

    /**
     * Set default interval for sending events to monitoring service. DefaultInterval will be used by
     * scheduler.
     *
     * @param defaultInterval the new default interval
     */
    public void setDefaultInterval(long defaultInterval) {
    	if(defaultInterval <= 0) {
    		LOG.severe("collector.scheduler.interval must be greater than 0. Recommended value is 500-1000. Current value is " + defaultInterval);
    		throw new IllegalArgumentException("collector.scheduler.interval must be greater than 0. Recommended value is 500-1000. Current value is " + defaultInterval);
    	}
        this.defaultInterval = defaultInterval;
    }

    /**
     * Set if collect/send lifecycle events to sam-server.
     *
     * @param sendLifecycleEvent the new send lifecycle event
     */
    public void setSendLifecycleEvent(boolean sendLifecycleEvent) {
        this.sendLifecycleEvent = sendLifecycleEvent;
    }

    /**
     * Scheduler will be set and configured by Spring. Spring executes every x milliseconds the sending
     * process.
     *
     * @param scheduler the new scheduler
     */
    public void setScheduler(TaskScheduler scheduler) {
        LOG.info("Scheduler started for sending events to SAM Server");
        this.scheduler = scheduler;

        this.scheduler.scheduleAtFixedRate(new Runnable() {

            public void run() {
                sendEventsFromQueue();
            }
        }, getDefaultInterval());
    }

    /**
     * Spring sets the executor. The executer is used for sending events to the web service.
     *
     * @param executor the new executor
     */
    public void setExecutor(TaskExecutor executor) {
        this.executor = executor;
    }

    /**
     * Spring sets the queue. Within the spring configuration you can decide between memory queue and
     * persistent queue.
     *
     * @param queue the new queue
     */
    public void setQueue(Queue<Event> queue) {
        this.queue = queue;
    }

    /**
     * Spring sets the monitoring service client.
     *
     * @param monitoringServiceClient the new monitoring service client
     */
    public void setMonitoringServiceClient(MonitoringService monitoringServiceClient) {
        this.monitoringServiceClient = monitoringServiceClient;
    }

    /**
     * Sets the bus.
     *
     * @param bus the new bus
     */
    public void setBus(Bus bus) {
        this.bus = bus;
    }

    /**
     * Gets the filters.
     *
     * @return the filters
     */
    public List<EventFilter> getFilters() {
        return filters;
    }

    /**
     * Sets the filters.
     *
     * @param filters the new filters
     */
    public void setFilters(List<EventFilter> filters) {
        this.filters = filters;
    }

    /**
     * Gets the handlers.
     *
     * @return the handlers
     */
    public List<EventHandler> getHandlers() {
        return handlers;
    }

    /**
     * Sets the handlers.
     *
     * @param newHandlers the new handlers
     */
    @Autowired(required = false)
    public void setHandlers(List<EventHandler> newHandlers) {
        this.handlers.clear();
        for (EventHandler eventHandler : newHandlers) {
            this.handlers.add(eventHandler);
        }
    }

    /**
     * Method will be executed asynchronously from spring.
     */
    public void sendEventsFromQueue() {
        if (stopSending) {
            return;
        }
        LOG.fine("Scheduler called for sending events");

        int packageSize = getEventsPerMessageCall();

        while (!queue.isEmpty()) {
            final List<Event> list = new ArrayList<Event>();
            int i = 0;
            while (i < packageSize && !queue.isEmpty()) {
                Event event = queue.remove();
                if (event != null && !filter(event)) {
                    list.add(event);
                    i++;
                }
            }
            if (list.size() > 0) {
                executor.execute(new Runnable() {
                    public void run() {
                        try {
                            sendEvents(list);
                        } catch (MonitoringException e) {
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
     * @param event the event
     * @return true, if successful
     */
    private boolean filter(Event event) {
        for (EventFilter filter : filters) {
            if (filter.filter(event)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sends the events to monitoring service client.
     *
     * @param events the events
     */
    private void sendEvents(final List<Event> events) {
        for (EventHandler current : handlers) {
            for (Event event : events) {
                current.handleEvent(event);
            }
        }

        LOG.info("Put events(" + events.size() + ") to Monitoring Server.");
        try {
            monitoringServiceClient.putEvents(events);
        } catch (MonitoringException e) {
            throw e;
        } catch (Exception e) {
            throw new MonitoringException("002",
                                          "Unknown error while execute put events to Monitoring Server", e);
        }

    }

    /* (non-Javadoc)
     * @see org.apache.cxf.buslifecycle.BusLifeCycleListener#initComplete()
     */
    @Override
    public void initComplete() {
        // Ignore
    }

    /* (non-Javadoc)
     * @see org.apache.cxf.buslifecycle.BusLifeCycleListener#preShutdown()
     */
    @Override
    public void preShutdown() {
        LOG.info("Bus is stopping. Stopping sending events to monitoring service.");
        this.stopSending = true;
    }

    /* (non-Javadoc)
     * @see org.apache.cxf.buslifecycle.BusLifeCycleListener#postShutdown()
     */
    @Override
    public void postShutdown() {
        // Ignore
    }

}
