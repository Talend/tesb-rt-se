/*
 * #%L
 * Service Activity Monitoring :: Agent
 * %%
 * Copyright (c) 2006-2021 Talend Inc. - www.talend.com
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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
//import javax.inject.Inject;
//import javax.inject.Named;
//import javax.inject.Singleton;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.ClientLifeCycleManager;
import org.apache.cxf.endpoint.ServerLifeCycleManager;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;

import org.talend.esb.sam.agent.lifecycle.ClientListenerImpl;
import org.talend.esb.sam.agent.lifecycle.ServiceListenerImpl;
import org.talend.esb.sam.agent.eventadmin.EventAdminPublisher;
import org.talend.esb.sam.agent.queue.EventQueue;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.event.MonitoringException;
import org.talend.esb.sam.common.service.MonitoringService;
import org.talend.esb.sam.common.spi.EventFilter;
import org.talend.esb.sam.common.spi.EventHandler;

/**
 * EventCollector reads the events from Queue.
 * After processing with filter/handler, the events will be sent to SAM Server periodically.
 */
//@Named
//@Singleton
public class EventCollector {

    private static final Logger LOG = Logger.getLogger(EventCollector.class.getName());

    //@Inject
    private Bus bus;

    //@Inject
    private MonitoringService monitoringServiceClient;

    @Autowired(required = false)
    private List<EventFilter> filters;

    @Autowired(required = false)
    private List<EventHandler> handlers;

    //@Inject
    private EventQueue queue;

    //@Value("${executor.pool.size}")
    private int executorPoolSize = 20;

    //@Value("${executor.queue.size}")
    private int executorQueueSize = 0;

    private ExecutorService executor;
    private Timer scheduler;

    //@Value("${collector.scheduler.interval}")
    private long defaultInterval = 1000;

    //@Value("${collector.maxEventsPerCall}")
    private int eventsPerMessageCall = 10;

    //@Value("${collector.lifecycleEvent}")
    private boolean sendLifecycleEvent;

    private boolean sendToEventadmin;

    private boolean stopSending;

    /**
     * Instantiates a new event collector.
     */
    @PostConstruct
    public void init() {
        //init Bus and LifeCycle listeners
        if (bus != null && sendLifecycleEvent ) {
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

        if(executorQueueSize == 0) {
        	executor = Executors.newFixedThreadPool(this.executorPoolSize);
        }else{
            executor = new ThreadPoolExecutor(executorPoolSize, executorPoolSize, 0, TimeUnit.SECONDS,
            		new LinkedBlockingQueue<Runnable>(executorQueueSize), Executors.defaultThreadFactory(),
            			new RejectedExecutionHandlerImpl());
        }

        scheduler = new Timer();
        scheduler.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                sendEventsFromQueue();
            }
        }, 0, getDefaultInterval());
    }

    public class RejectedExecutionHandlerImpl implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            LOG.warning("Executor queue size ["+executorQueueSize+"] is exceeded. Extra SAM Events are rejected.");
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
     * Set if send SAM events to the EventAdmin (for Event Logging)
     *
     * @param sendToEventadmin send SAM events to the EventAdmin
     */
    public void setSendToEventadmin(boolean sendToEventadmin) {
        this.sendToEventadmin = sendToEventadmin;
    }

    /**
     * Spring sets the queue. Within the spring configuration you can decide between memory queue and
     * persistent queue.
     *
     * @param queue the new queue
     */
    public void setQueue(EventQueue queue) {
        this.queue = queue;
    }

    public void setExecutorPoolSize(int executorPoolSize) {
        this.executorPoolSize = executorPoolSize;
    }

    public void setExecutorQueueSize(int executorQueueSize) {
        this.executorQueueSize = executorQueueSize;
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
    public void setHandlers(List<EventHandler> handlers) {
        this.handlers = handlers;
    }

    /**
     * Method will be executed asynchronously.
     */
    public void sendEventsFromQueue() {
        if (null == queue || stopSending) {
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
        if (null == filters) return false;

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
        if (null != handlers) {
            for (EventHandler current : handlers) {
                for (Event event : events) {
                    current.handleEvent(event);
                }
            }
        }

        LOG.info("Put events(" + events.size() + ") to Monitoring Server.");

        try {
            if (sendToEventadmin) {
                EventAdminPublisher.publish(events);
            } else {
                monitoringServiceClient.putEvents(events);
            }
        } catch (MonitoringException e) {
            throw e;
        } catch (Exception e) {
            throw new MonitoringException("002",
                                          "Unknown error while execute put events to Monitoring Server", e);
        }

    }

    @PreDestroy
    public void destroy() {
        try {
            Thread.sleep(200);
            if (!queue.isEmpty()) {
                Thread.sleep(500);
            } else {
                this.stopSending = true;
            }
        } catch (InterruptedException e) {
            // Ignore
        }

        if (null != scheduler) {
            scheduler.cancel();
        }
        if (null != executor) {
            executor.shutdown();
        }
    }

}
