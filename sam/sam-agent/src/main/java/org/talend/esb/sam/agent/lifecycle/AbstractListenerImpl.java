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
package org.talend.esb.sam.agent.lifecycle;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

//import javax.inject.Inject;

import org.apache.cxf.binding.soap.SoapBinding;
import org.apache.cxf.binding.soap.model.SoapBindingInfo;
import org.apache.cxf.endpoint.Endpoint;
//import org.springframework.beans.factory.annotation.Value;
import org.talend.esb.sam.agent.queue.EventQueue;
import org.talend.esb.sam.agent.util.Converter;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.event.EventTypeEnum;
import org.talend.esb.sam.common.event.MessageInfo;
import org.talend.esb.sam.common.event.Originator;
import org.talend.esb.sam.common.service.MonitoringService;

/**
 * The Class AbstractListenerImpl implementing abstract life cycle for SAM agent event.
 */
public class AbstractListenerImpl {

    private static final Logger LOG = Logger.getLogger(AbstractListenerImpl.class.getName());

    //@Value("${collector.lifecycleEvent}")
    private boolean sendLifecycleEvent;

    //@Inject
    private EventQueue queue;

    //@Inject
    private MonitoringService monitoringServiceClient;

    /**
     * Sets the lifecycle event for sending.
     *
     * @param sendLifecycleEvent the new lifecycle event
     */
    public void setSendLifecycleEvent(boolean sendLifecycleEvent) {
        this.sendLifecycleEvent = sendLifecycleEvent;
    }

    /**
     * Sets the queue.
     *
     * @param queue the new queue
     */
    public void setQueue(EventQueue queue) {
        this.queue = queue;
    }

    /**
     * Sets the monitoring service client.
     *
     * @param monitoringServiceClient the new monitoring service client
     */
    public void setMonitoringServiceClient(MonitoringService monitoringServiceClient) {
        this.monitoringServiceClient = monitoringServiceClient;
    }

    /**
     * Process start.
     *
     * @param endpoint the endpoint
     * @param eventType the event type
     */
    protected void processStart(Endpoint endpoint, EventTypeEnum eventType) {
        if (!sendLifecycleEvent) {
            return;
        }

        Event event = createEvent(endpoint, eventType);
        queue.add(event);
    }

    /**
     * Process stop.
     *
     * @param endpoint the endpoint
     * @param eventType the event type
     */
    protected void processStop(Endpoint endpoint, EventTypeEnum eventType) {
        if (!sendLifecycleEvent) {
            return;
        }

        Event event = createEvent(endpoint, eventType);
        monitoringServiceClient.putEvents(Collections.singletonList(event));
        if (LOG.isLoggable(Level.INFO)) {
            LOG.info("Send " + eventType + " event to SAM Server successful!");
        }
    }

    /**
     * Creates the event for endpoint with specific type.
     *
     * @param endpoint the endpoint
     * @param type the type
     * @return the event
     */
    private Event createEvent(Endpoint endpoint, EventTypeEnum type) {

        Event event = new Event();
        MessageInfo messageInfo = new MessageInfo();
        Originator originator = new Originator();
        event.setMessageInfo(messageInfo);
        event.setOriginator(originator);

        Date date = new Date();
        event.setTimestamp(date);
        event.setEventType(type);

        messageInfo.setPortType(
                endpoint.getBinding().getBindingInfo().getService().getInterface().getName().toString());

        String transportType = null;
        if (endpoint.getBinding() instanceof SoapBinding) {
            SoapBinding soapBinding = (SoapBinding)endpoint.getBinding();
            if (soapBinding.getBindingInfo() instanceof SoapBindingInfo) {
                SoapBindingInfo soapBindingInfo = (SoapBindingInfo)soapBinding.getBindingInfo();
                transportType = soapBindingInfo.getTransportURI();
            }
        }
        messageInfo.setTransportType((transportType != null) ? transportType : "Unknown transport type");

        originator.setProcessId(Converter.getPID());
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            originator.setIp(inetAddress.getHostAddress());
            originator.setHostname(inetAddress.getHostName());
        } catch (UnknownHostException e) {
            originator.setHostname("Unknown hostname");
            originator.setIp("Unknown ip address");
        }

        String address = endpoint.getEndpointInfo().getAddress();
        event.getCustomInfo().put("address", address);

        return event;
    }
}
