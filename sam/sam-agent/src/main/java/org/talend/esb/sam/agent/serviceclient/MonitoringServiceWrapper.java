package org.talend.esb.sam.agent.serviceclient;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.WebServiceException;

import org.talend.esb.sam._2011._03.common.EventType;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.event.MonitoringException;
import org.talend.esb.sam.monitoringservice.v1.MonitoringService;
import org.talend.esb.sam.monitoringservice.v1.PutEventsFault;

/**
 * Wraps business logic to web service logic. So web service should be changeable.
 * 
 */
public class MonitoringServiceWrapper implements org.talend.esb.sam.common.service.MonitoringService {
    private MonitoringService monitoringService;

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
    /*
     * public void setMapper(DozerBeanMapper mapper) { this.mapper = mapper; }
     */

    /**
     * Sends all events to the web service. Events will be transformed with mapper before sending.
     */
    public void putEvents(List<Event> events) {
        try {
            List<EventType> eventTypes = new ArrayList<EventType>();
            for (Event event : events) {
                EventType eventType = EventMapper.map(event);
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
                throw new MonitoringException("1104", "Could not send events to monitoring service after "
                                                      + numberOfRetries + " retries.", null, events);
            }
        } catch (PutEventsFault e) {
            MonitoringException me = new MonitoringException("1102",
                                                             "Could not send events to monitoring service",
                                                             e, events);
            throw me;
        }
    }


}
