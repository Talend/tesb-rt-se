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
package org.talend.esb.sam.agent.eventproducer;

import java.lang.reflect.Method;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageUtils;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.ws.addressing.AddressingProperties;
import org.apache.cxf.ws.addressing.ContextUtils;
import org.talend.esb.sam.common.event.Event;


/**
 * Maps the CXF Message to an Event and sends Event to Queue.
 */
public class EventProducerInterceptor extends AbstractPhaseInterceptor<Message> {
    private static final Logger LOG = Logger.getLogger(EventProducerInterceptor.class.getName());

    private final MessageToEventMapper mapper;
    private final Queue<Event> queue;

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

    /* (non-Javadoc)
     * @see org.apache.cxf.interceptor.Interceptor#handleMessage(org.apache.cxf.message.Message)
     */
    @Override
    public void handleMessage(Message message) throws Fault {

        //ignore the messages from SAM Server service itself
        BindingOperationInfo boi = message.getExchange().getBindingOperationInfo();
        if (null != boi){
            String operationName = boi.getName().toString();
            if (SAM_OPERATION.equals(operationName)) {
                return;
            }
        }

        if (isRestWadlRequest(message)) {
            // skip handling REST service WADL requests - temporary fix since will be fixed in CXF soon
            return;
        }

        if (isOnewayResponse(message)) {
            // skip oneway response events
            return;
        }

        // Skip Swagger UI web static, swagger.json and swagger.yaml requests
        if (isSwaggerResourceRequest(message)) {
            return;
        }

        //check MessageID
        checkMessageID(message);

        Event event = mapper.mapToEvent(message);

        if (LOG.isLoggable(Level.FINE)) {
            String id = (event.getMessageInfo() != null) ? event.getMessageInfo().getMessageId() : null;
            LOG.fine("Store event [message_id=" + id + "] in cache.");
        }
        if (null != event) {
            queue.add(event);
        }

        // TESB-20624 skip duplicated event processing
        message.getInterceptorChain().remove(this);
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
            maps = new AddressingProperties();
        }
        if (maps.getMessageID() == null) {
            String messageID = ContextUtils.generateUUID();
            boolean isRequestor = ContextUtils.isRequestor(message);
            maps.setMessageID(ContextUtils.getAttributedURI(messageID));
            ContextUtils.storeMAPs(maps, message, ContextUtils.isOutbound(message), isRequestor);
        }
    }

    private boolean isRestWadlRequest(Message message) {
        if (MessageToEventMapper.isRestMessage(message)) {
            String queryString = (String) message.get(Message.QUERY_STRING);
            boolean isRestWadlRequest = isRestWadlRequest(queryString);
            if (isRestWadlRequest) {
                return true;
            }

            // unfortunately responses for WADL request do not contain request QUERY_STRING
            if (MessageUtils.isOutbound(message)) {
                Exchange ex = message.getExchange();
                if (null != ex) {
                    Message requestMessage = ex.getInMessage();
                    if (null != requestMessage) {
                        queryString = (String) requestMessage.get(Message.QUERY_STRING);
                        return isRestWadlRequest(queryString);
                    }
                }
            }
        }
        return false;
    }

    private boolean isRestWadlRequest(String requestQueryString) {
        if (null != requestQueryString) {
            String[] queryParams = requestQueryString.split("&");
            for (String param : queryParams) {
                if ("_wadl".equals(param) || param.startsWith("_wadl=")) {
                    return true;
                }
            }
        }
        return false;
    }


    private boolean isOnewayResponse(Message message) {

          boolean isRequestor = MessageUtils.isRequestor(message);
          boolean isFault = MessageUtils.isFault(message);
          boolean isOutbound = MessageUtils.isOutbound(message);

          boolean isOnewayOutResp =  message.getExchange().isOneWay()
                                         && isOutbound
                                         && !isRequestor
                                         && !isFault
                                         && !MessageToEventMapper.isRestMessage(message);

          boolean isOnewayInResp  =  message.getExchange().isOneWay()
                                         && !isOutbound
                                         && isRequestor
                                         && !isFault
                                         && !MessageToEventMapper.isRestMessage(message);

          return isOnewayOutResp || isOnewayInResp;
    }

    private boolean isSwaggerResourceRequest(Message message) {
        Object method = message.get("org.apache.cxf.resource.method");
        if (method instanceof Method) {
            return  isSwaggerResourceHandler((Method)method);
        }

        // unfortunately, response messages do not contain handler method
        if (MessageUtils.isOutbound(message) && message.getExchange().getInMessage() != null) {
            method = message.getExchange().getInMessage().get("org.apache.cxf.resource.method");
            if (method instanceof Method) {
                return isSwaggerResourceHandler((Method)method);
            } else if (method == null
                         && MessageToEventMapper.isRestMessage(message)
                         && message.get(Message.RESPONSE_CODE) != null) { // supposedly Swagger response in a route
                Integer responseCode = (Integer) message.get(Message.RESPONSE_CODE);
                return ((responseCode < 400 && message.getExchange().get("org.apache.cxf.resource.operation.name") == null)
                         || isSwaggerImage(message.getExchange().getInMessage()));
            }
        }

        return  false;
    }

    private boolean isSwaggerImage(Message message) {
        String acceptContentType = (String) message.get(Message.ACCEPT_CONTENT_TYPE);
        return acceptContentType != null && acceptContentType.contains("image/");
    }

    private boolean isSwaggerResourceHandler(Method method) {
        String handlerClassName = method.getDeclaringClass().getCanonicalName();
        return handlerClassName.startsWith("org.apache.cxf.jaxrs.swagger.") // Swagger UI static resources
                || "io.swagger.jaxrs.listing.ApiListingResource".equals(handlerClassName); // swagger.json (or yaml)
    }
}
