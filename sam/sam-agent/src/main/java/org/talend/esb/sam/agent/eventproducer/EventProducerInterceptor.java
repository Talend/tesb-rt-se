/*
 * #%L
 * Service Activity Monitoring :: Agent
 * %%
 * Copyright (C) 2011 - 2012 Talend Inc.
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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageUtils;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.ws.addressing.AddressingProperties;
import org.apache.cxf.ws.addressing.ContextUtils;
import org.apache.cxf.ws.addressing.impl.AddressingPropertiesImpl;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.spi.EventHandler;


/**
 * Maps the CXF Message to an Event and sends Event to Queue.
 */
public class EventProducerInterceptor extends AbstractPhaseInterceptor<Message> {
    private static final Logger LOG = Logger.getLogger(EventProducerInterceptor.class.getName());

    private final MessageToEventMapper mapper;
    private final Queue<Event> queue;
    private EventHandler handler;
    
    private static final String SAM_OPERATION = "{http://www.talend.org/esb/sam/MonitoringService/v1}putEvents";
    
    /**
     * Instantiates a new event producer interceptor.
     *
     * @param mapper the mapper
     * @param queue the queue
     */
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

    /**
     * Sets the handler.
     *
     * @param handler the new handler
     */
    public void setHandler(EventHandler handler) {
        this.handler = handler;
    }

    /* (non-Javadoc)
     * @see org.apache.cxf.interceptor.Interceptor#handleMessage(org.apache.cxf.message.Message)
     */
    @Override
    public void handleMessage(Message message) throws Fault {

        //ignore the messages from SAM Server service itself
        BindingOperationInfo boi = message.getExchange().getBindingOperationInfo();
        if (null != boi){
            String operationName = boi.getName().toString();
            if (SAM_OPERATION.equals(operationName)) return;
        }

        //check MessageID
        checkMessageID(message);

        Event event = mapper.mapToEvent(message);
        
        if (handler != null) {
            handler.handleEvent(event);
        }
        if (LOG.isLoggable(Level.FINE)) {
            String id = (event.getMessageInfo() != null) ? event.getMessageInfo().getMessageId() : null;
            LOG.fine("Store event [message_id=" + id + "] in cache.");
        }
        if(event != null) queue.add(event);
    }

    /**
     * check if MessageID exists in the message, if not, only generate new MessageID for outbound message.
     * @param message
     */
    private void checkMessageID(Message message) {
        if (!MessageUtils.isOutbound(message)) return;

        AddressingProperties maps =
                ContextUtils.retrieveMAPs(message, false, MessageUtils.isOutbound(message));
        if (maps == null) {
            maps = new AddressingPropertiesImpl();
        }
        if (maps.getMessageID() == null) {
            String messageID = ContextUtils.generateUUID();
            boolean isRequestor = ContextUtils.isRequestor(message);
            maps.setMessageID(ContextUtils.getAttributedURI(messageID));
            ContextUtils.storeMAPs(maps, message, ContextUtils.isOutbound(message), isRequestor);
        }

    }
}
