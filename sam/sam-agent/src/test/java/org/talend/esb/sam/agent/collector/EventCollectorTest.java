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
import java.util.concurrent.ConcurrentLinkedQueue;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.filter.impl.StringContentFilter;
import org.talend.esb.sam.common.handler.impl.ContentLengthHandler;
import org.talend.esb.sam.common.service.MonitoringService;

public class EventCollectorTest {
    private final class MockService implements MonitoringService {
        public List<List<Event>> receivedEvents = new ArrayList<List<Event>>();

        @Override
        public void putEvents(List<Event> events) {
            receivedEvents.add(events);
        }
    }

    @Test
    public void testEventCollector() throws InterruptedException {
        Queue<Event> queue = new ConcurrentLinkedQueue<Event>();

        EventCollector eventCollector = new EventCollector();
        eventCollector.setDefaultInterval(500);
        eventCollector.getFilters().add(new StringContentFilter());
        eventCollector.getHandlers().add(new ContentLengthHandler());
        eventCollector.setEventsPerMessageCall(2);
        eventCollector.setQueue(queue);
        TaskExecutor executor = new SyncTaskExecutor();
        eventCollector.setExecutor(executor);
        MockService monitoringService = new MockService();
        eventCollector.setMonitoringServiceClient(monitoringService);

        // Add events
        queue.add(createEvent("1"));
        queue.add(createEvent("2"));
        queue.add(createEvent("3"));

        // Send from Queue
        eventCollector.sendEventsFromQueue();
        eventCollector.sendEventsFromQueue();

        Assert.assertEquals(2, monitoringService.receivedEvents.size());
        List<Event> events0 = monitoringService.receivedEvents.get(0);
        Assert.assertEquals(2, events0.size());
        List<Event> events1 = monitoringService.receivedEvents.get(1);
        Assert.assertEquals(1, events1.size());
    }

    public Event createEvent(String content) {
        Event event = new Event();
        event.setContent(content);
        return event;
    }
}
