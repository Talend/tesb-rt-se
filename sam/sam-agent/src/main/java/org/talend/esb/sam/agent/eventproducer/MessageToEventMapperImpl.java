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

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.logging.Logger;

import org.apache.cxf.binding.soap.SoapBinding;
import org.apache.cxf.binding.soap.model.SoapBindingInfo;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageUtils;
import org.apache.cxf.ws.addressing.ContextUtils;
import org.apache.cxf.security.SecurityContext;
import org.talend.esb.sam.agent.flowid.FlowIdHelper;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.event.EventTypeEnum;
import org.talend.esb.sam.common.event.MessageInfo;
import org.talend.esb.sam.common.event.Originator;

public final class MessageToEventMapperImpl implements MessageToEventMapper {
    private Logger log = Logger.getLogger(MessageToEventMapperImpl.class.getName());
    private int maxContentLength = -1;

    /*
     * (non-Javadoc)
     * @see org.talend.esb.sam.agent.producer.MessageToEventMapper#mapToEvent(org.apache.cxf.message.Message,
     * org.talend.esb.sam.agent.interceptor.InterceptorType, java.lang.String)
     */
    @Override
    public Event mapToEvent(Message message) {
        Event event = new Event();
        MessageInfo messageInfo = new MessageInfo();
        Originator originator = new Originator();

        event.setMessageInfo(messageInfo);
        event.setOriginator(originator);
        String content = getPayload(message);
        
        if (maxContentLength != -1 && content.length() > maxContentLength) {
            content = content.substring(0, maxContentLength);
            event.setContentCut(true);
        }
        
        event.setContent(content);
        event.setEventType(null);
        Date date = new Date();
        event.setTimestamp(date);

        messageInfo.setFlowId(FlowIdHelper.getFlowId(message));
        messageInfo.setMessageId(ContextUtils.generateUUID());
        String opName = message.getExchange().getBindingOperationInfo().getName().toString();
        messageInfo.setOperationName(opName);
        String portTypeName = message.getExchange().getBinding().getBindingInfo().getService().getInterface()
            .getName().toString();
        messageInfo.setPortType(portTypeName);

        if (message.getExchange().getBinding() instanceof SoapBinding) {
            SoapBinding soapBinding = (SoapBinding)message.getExchange().getBinding();
            if (soapBinding.getBindingInfo() instanceof SoapBindingInfo) {
                SoapBindingInfo soapBindingInfo = (SoapBindingInfo)soapBinding.getBindingInfo();
                messageInfo.setTransportType(soapBindingInfo.getTransportURI());
            }
        }
        if (messageInfo.getTransportType() == null) {
            messageInfo.setTransportType("Unknown transport type");
        }

        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            originator.setIp(inetAddress.getHostAddress());
            originator.setHostname(inetAddress.getHostName());
        } catch (UnknownHostException e) {
            originator.setHostname("Unknown hostname");
            originator.setIp("Unknown ip address");
        }
        String mxName = ManagementFactory.getRuntimeMXBean().getName();
        String pId = mxName.split("@")[0];
        originator.setProcessId(pId);
        
        SecurityContext sc = message.get(SecurityContext.class);
        if (sc != null && sc.getUserPrincipal() != null){
        	originator.setPrincipal(sc.getUserPrincipal().getName());
        }
        
        EventTypeEnum eventType = getEventType(message);
        event.setEventType(eventType);
        return event;
    }

    private EventTypeEnum getEventType(Message message) {
        boolean isRequestor = MessageUtils.isRequestor(message);
        boolean isFault = MessageUtils.isFault(message);
        boolean isOutbound = MessageUtils.isOutbound(message);

        if (isOutbound) {
            if (isFault) {
                return EventTypeEnum.FAULT_OUT;
            } else {
                return isRequestor ? EventTypeEnum.REQ_OUT : EventTypeEnum.RESP_OUT;
            }
        } else {
            if (isFault) {
                return EventTypeEnum.FAULT_IN;
            } else {
                return isRequestor ? EventTypeEnum.RESP_IN : EventTypeEnum.REQ_IN;
            }
        }
    }

    protected String getPayload(Message message) {
        try {
            String encoding = (String)message.get(Message.ENCODING);
            if (encoding == null) {
                encoding = "UTF-8";
            }
            CachedOutputStream cos = message.getContent(CachedOutputStream.class);
            if (cos == null) {
                log.warning("Could not find CachedOutputStream in message. Continuing without message content");
                return "";
            }
            return new String(cos.getBytes(), encoding);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getMaxContentLength() {
        return maxContentLength;
    }

    public void setMaxContentLength(int maxContentLength) {
        this.maxContentLength = maxContentLength;
    }
}
