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
package org.talend.esb.sam.agent.serviceclient;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.springframework.beans.factory.annotation.Value;
import org.talend.esb.sam._2011._03.common.EventType;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.event.MonitoringException;
import org.talend.esb.sam.monitoringservice.v1.MonitoringService;

/**
 * Wraps business logic to web service logic. So web service should be changeable.
 *
 */
@Named("monitoringServceV1Wrapper")
@Singleton
public class MonitoringServiceWrapper implements org.talend.esb.sam.common.service.MonitoringService {
    @Inject
    private MonitoringService monitoringService;

    @Value("${service.retry.number}")
    private int numberOfRetries = 3;

    @Value("${service.retry.delay}")
    private long delayBetweenRetry = 1000;

    /**
     * Sets the number of retries.
     *
     * @param numberOfRetries the new number of retries
     */
    public void setNumberOfRetries(int numberOfRetries) {
        if (numberOfRetries <= 0) {
            throw new IllegalStateException("Number of retries must be > 0 but was " + numberOfRetries);
        }
        this.numberOfRetries = numberOfRetries;
    }

    /**
     * Sets the delay between retry.
     *
     * @param delayBetweenRetry the new delay between retry
     */
    public void setDelayBetweenRetry(long delayBetweenRetry) {
        if (delayBetweenRetry <= 0) {
            throw new IllegalStateException("Delay between retries must be > 0 but was " + delayBetweenRetry);
        }
        this.delayBetweenRetry = delayBetweenRetry;
    }

    /**
     * Set by Spring. Sets the web service implementation.
     *
     * @param monitoringService the new monitoring service
     */
    public void setMonitoringService(MonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    /**
     * Sends all events to the web service. Events will be transformed with mapper before sending.
     *
     * @param events the events
     */
    public void putEvents(List<Event> events) {
        Exception lastException;
        List<EventType> eventTypes = new ArrayList<EventType>();
        for (Event event : events) {
            EventType eventType = EventMapper.map(event);
            eventTypes.add(eventType);
        }

        int i = 0;
        lastException = null;
        while (i < numberOfRetries) {
            try {
                monitoringService.putEvents(eventTypes);
                break;
            } catch (Exception e) {
                lastException = e;
                i++;
            }
            if(i < numberOfRetries) {
                try {
                    Thread.sleep(delayBetweenRetry);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }

        if (i == numberOfRetries) {
            throw new MonitoringException("1104", "Could not send events to monitoring service after "
                                                  + numberOfRetries + " retries.", lastException, events);
        }
    }

}
