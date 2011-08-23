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
package org.talend.esb.sam.agent.eventproducer;

import java.util.Queue;
import java.util.logging.Logger;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.spi.EventHandler;

/**
 * Maps the CXF Message to an Event and sends Event to Queue
 * 
 */
public class EventProducerInterceptor extends AbstractPhaseInterceptor<Message> {
	private Logger logger = Logger.getLogger(EventProducerInterceptor.class.getName());
	
    private MessageToEventMapper mapper;
    private Queue<Event> queue;
    private EventHandler handler;
    
    public EventProducerInterceptor(MessageToEventMapper mapper, Queue<Event> queue) {
        super(Phase.PRE_INVOKE);
        if (mapper == null) {
            throw new RuntimeException("Mapper must be set on EventFeature");
        }
        if (queue == null) {
            throw new RuntimeException("Queue must be set on EventFeature");
        }
        this.mapper = mapper;
        this.queue = queue;
    }

    public void setHandler(EventHandler handler) {
		this.handler = handler;
	}

	@Override
    public void handleMessage(Message message) throws Fault {
        Event event = mapper.mapToEvent(message);
        
        if (handler != null){
        	handler.handleEvent(event);
        }
        
        String id = (event.getMessageInfo() != null) ? event.getMessageInfo().getMessageId() : null;
        logger.fine("Store event [message_id=" + id + "] in cache.");
        queue.add(event);
    }

}
