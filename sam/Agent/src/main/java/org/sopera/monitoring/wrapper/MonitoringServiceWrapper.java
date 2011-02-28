package org.sopera.monitoring.wrapper;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.WebServiceException;

import org.dozer.DozerBeanMapper;
import org.sopera.monitoring._2010._09.common.EventType;
import org.sopera.monitoring.event.Event;
import org.sopera.monitoring.event.MonitoringException;
import org.sopera.monitoring.monitoringservice.v1.MonitoringService;
import org.sopera.monitoring.monitoringservice.v1.PutEventsFault;

/**
 * Wraps business logic to web service logic. So web service should be
 * changeable.
 * 
 * @author cschmuelling
 * 
 */
public class MonitoringServiceWrapper implements org.sopera.monitoring.event.service.MonitoringService {
	// private static Logger logger = Logger
	// .getLogger(MonitoringServiceWrapper.class.getName());

	private MonitoringService monitoringService;
	private DozerBeanMapper mapper;

	private int numberOfRetries;
	private long delayBetweenRetry;

	public void setNumberOfRetries(int numberOfRetries) {
		this.numberOfRetries = numberOfRetries;
	}

	public void setDelayBetweenRetry(long delayBetweenRetry) {
		this.delayBetweenRetry = delayBetweenRetry;
	}

	/**
	 * Set by Spring. Sets the web service implementation.
	 * 
	 * @param monitoringService
	 */
	public void setMonitoringService(MonitoringService monitoringService) {
		this.monitoringService = monitoringService;
	}

	/**
	 * Set by Spring. Sets the dozer mapper for transforming objects.
	 * 
	 * @param mapper
	 */
	public void setMapper(DozerBeanMapper mapper) {
		this.mapper = mapper;
	}

	/**
	 * Sends all events to the web service. Events will be transformed with
	 * mapper before sending.
	 */
	public void putEvents(List<Event> events) {
		try {
			List<EventType> eventTypes = new ArrayList<EventType>();
			for (Event event : events) {
				// If you want prevent null values.
				// checkEvent(event);
				EventType eventType = (EventType) mapper.map(event,
						EventType.class);
				eventTypes.add(eventType);
			}

			int i = 0;
			if (numberOfRetries == 0)
				numberOfRetries = 5;

			while (i < numberOfRetries) {
				try {
					monitoringService.putEvents(eventTypes);
					break;
				} catch (WebServiceException wse) {
					i++;
					if (delayBetweenRetry <= 0) {
						delayBetweenRetry = 1000;
					}
					try {
						Thread.sleep(delayBetweenRetry);
					} catch (InterruptedException e) {
						throw new MonitoringException(
								"1103",
								"Could not send events to monitoring service. Retry interrupted.",
								e, events);
					}
				}
			}
			if (i == numberOfRetries) {
				throw new MonitoringException("1104",
						"Could not send events to monitoring service after "
								+ numberOfRetries + " retries.", null, events);
			}
		} catch (PutEventsFault e) {
			MonitoringException me = new MonitoringException("1102",
					"Could not send events to monitoring service", e, events);
			throw me;
		}
	}

}
