package org.sopera.monitoring.collector;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sopera.monitoring.event.Event;
import org.sopera.monitoring.exception.MonitoringException;
import org.sopera.monitoring.filter.EventFilter;
import org.sopera.monitoring.handler.EventManipulator;
import org.sopera.monitoring.queue.Queue;
import org.sopera.monitoring.service.MonitoringService;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;

/**
 * Event collector collects all events and stores them in a queue. This can be a
 * memory queue or a persistent queue. Asynchronously the events will be
 * processed and sent to MonitoringService
 * 
 * @author cschmuelling
 * 
 */
public class EventCollectorImpl implements EventCollector {

	private static Logger logger = Logger.getLogger(EventCollectorImpl.class
			.getName());

	private MonitoringService monitoringServiceClient;
	private List<EventFilter<Event>> eventFilter;
	private Queue<Event> queue;
	private TaskExecutor executor;
	private TaskScheduler scheduler;
	private long defaultInterval = 0;
	private boolean stopMessageFlowOnError = false;
	private int eventsPerMessageCall = 10;
	private boolean stopSending = false;

	private List<EventManipulator<Event>> eventManipulator;

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
	 * Returns the default interval for sending events. Returns 30000 if there
	 * is no interval set.
	 * 
	 * @return
	 */
	private long getDefaultInterval() {
		if (defaultInterval <= 0) {
			logger.warning("Scheduler interval for starting sending process is set to default 30000.");
			return 30000;
		}
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
	public void setEventFilter(List<EventFilter<Event>> eventFilter) {
		this.eventFilter = eventFilter;
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
	 * Spring sets eventManipulator
	 * 
	 * @param eventManipulator
	 */
	public void setEventManipulator(
			List<EventManipulator<Event>> eventManipulator) {
		this.eventManipulator = eventManipulator;
	}


	/**
	 * Stores an event in the queue and returns. So the synchronous execution of
	 * this service is as short as possible.
	 */
	public void putEvent(Event event) {
		logger.info("Store event in cache");
		try {
			queue.add(event);
		} catch (MonitoringException e) {
			logger.severe("Store event in cache caused ERROR");
			e.addEvent(event);
			e.logException(Level.SEVERE);
			if (stopMessageFlowOnError) {
				throw e;
			}
		}
	}

	/**
	 * Method will be executed asynchronously from spring.
	 */
	public void sendEventsFromQueue() {
		if(this.stopSending == true){
			logger.info("Scheduler called for sending events. Stop flag set. Sending will not occur.");
			return;
		}
		logger.info("Scheduler called for sending events");

		int packageSize = getEventsPerMessageCall();

		while (!queue.isEmpty()) {
			final List<Event> list = new ArrayList<Event>();
			int i = 0;
			while (i < packageSize && !queue.isEmpty()) {
				Event event = null;
				try {
					event = queue.remove();
					if (event == null)
						continue;
				} catch (MonitoringException e) {
					logger.severe("Error on event queue reading");
					e.logException(Level.SEVERE);
					return;
				}
				if (!filter(event)) {
					list.add(event);
					i++;
				}
			}
			if (list.size() > 0) {
				Date startAt = new Date();
				logger.info("Start async execution for " + list.size()
						+ " events.");

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

				Date finishedAt = new Date();
				logger.info("Finished delegating async execution for "
						+ list.size() + " events. Time="
						+ (finishedAt.getTime() - startAt.getTime()));
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
		if (eventFilter != null && eventFilter.size() > 0) {
			for (EventFilter<Event> filter : eventFilter) {
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
		UUID random = UUID.randomUUID();
		Date startAt = new Date();
		logger.info("Start sending events at " + startAt.getTime()
				+ " for identifier: " + random.toString());

		// Execute Filter (for example password filter and cutting content
		if (eventManipulator != null && eventManipulator.size() > 0) {
			for (EventManipulator<Event> current : eventManipulator) {
				for (Event event : events) {
				    current.handleEvent(event);
				}
			}
		}


		try {
	                monitoringServiceClient.putEvents(events);
		} catch (Exception e) {
			if (e instanceof MonitoringException)
				throw (MonitoringException) e;
			throw new MonitoringException("002",
					"Unknown error while chain execution", e);
		}

		Date finishedAt = new Date();
		logger.info("Finished sending events at " + finishedAt.getTime()
				+ " for identifier: " + random.toString() + ". Sending takes "
				+ (finishedAt.getTime() - startAt.getTime()) + " msec.");
	}
	
	public void stopSending(){
		logger.info("Bus is stopping. Stopping sending events to monitoring service.");
		this.stopSending = true;
	}
}
