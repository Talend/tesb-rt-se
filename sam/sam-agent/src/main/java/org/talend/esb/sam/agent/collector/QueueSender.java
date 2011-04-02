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

import java.util.Queue;
import java.util.logging.Logger;

import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.spi.EventHandler;

public class QueueSender implements EventHandler {
    private static Logger logger = Logger.getLogger(EventCollectorImpl.class
                    .getName());

    private Queue<Event> queue;

    public void setQueue(Queue<Event> queue) {
        this.queue = queue;
    }

    /**
     * Stores an event in the queue and returns. So the synchronous execution of
     * this service is as short as possible.
     */
    @Override
    public void handleEvent(Event event) {
            String id = (event.getMessageInfo() != null) ? event.getMessageInfo().getMessageId() : null;
            logger.fine("Store event [message_id=" + id + "] in cache.");
            queue.add(event);
    }
}
