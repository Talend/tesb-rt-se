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

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import org.apache.cxf.binding.soap.SoapBinding;
import org.apache.cxf.binding.soap.model.SoapBindingInfo;
import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageUtils;
import org.apache.cxf.security.SecurityContext;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.service.model.ServiceInfo;
import org.apache.cxf.ws.addressing.AddressingProperties;
import org.apache.cxf.ws.addressing.ContextUtils;
//import org.apache.cxf.ws.addressing.impl.AddressingPropertiesImpl;
import org.apache.cxf.service.model.ServiceModelUtil;

import org.talend.esb.sam.agent.message.CustomInfo;
import org.talend.esb.sam.agent.message.FlowIdHelper;
import org.talend.esb.sam.agent.util.Converter;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.event.EventTypeEnum;
import org.talend.esb.sam.common.event.MessageInfo;
import org.talend.esb.sam.common.event.Originator;

/**
 * The Class MessageToEventMapper.
 */
public class MessageToEventMapper {

    private static final Logger LOG = Logger.getLogger(MessageToEventMapper.class.getName());

    private static final String CUT_START_TAG = "<cut><![CDATA[";
    private static final String CUT_END_TAG = "]]></cut>";

    private int maxContentLength = -1;

    /**
     * Map to event.
     * 
     * @param message
     *            the message
     * @return the event
     */
    public Event mapToEvent(Message message) {
        Event event = new Event();
        MessageInfo messageInfo = new MessageInfo();
        Originator originator = new Originator();
        boolean isRestMessage = isRestMessage(message);

        event.setMessageInfo(messageInfo);
        event.setOriginator(originator);
        String content = getPayload(message);
        event.setContent(content);
        handleContentLength(event);
        event.setEventType(null);
        Date date = new Date();
        event.setTimestamp(date);
        
        messageInfo.setFlowId(FlowIdHelper.getFlowId(message));
        if (!isRestMessage) {
            messageInfo.setMessageId(getMessageId(message));
            ServiceInfo serviceInfo = message.getExchange().getBinding().getBindingInfo().getService();
            if (null != serviceInfo) {
                String portTypeName = serviceInfo.getInterface().getName().toString();
                messageInfo.setPortType(portTypeName);
                messageInfo.setOperationName(getOperationName(message));
            }
            SoapBinding soapBinding = (SoapBinding) message.getExchange().getBinding();
            if (soapBinding.getBindingInfo() instanceof SoapBindingInfo) {
                SoapBindingInfo soapBindingInfo = (SoapBindingInfo) soapBinding.getBindingInfo();
                messageInfo.setTransportType(soapBindingInfo.getTransportURI());
            }
        } else {
            messageInfo.setTransportType("http://cxf.apache.org/transports/http");
            messageInfo.setPortType(message.getExchange().getEndpoint().getEndpointInfo().getName()
                    .toString());
            String opName = getRestOperationName(message);
            messageInfo.setOperationName(opName);
        }

        if (messageInfo.getTransportType() == null) {
            messageInfo.setTransportType("Unknown transport type");
        }

        String addr = null;
        if(isRestMessage) addr = (String) message.get(Message.BASE_PATH);
        else addr = message.getExchange().getEndpoint().getEndpointInfo().getAddress();
        if (null != addr) {
            event.getCustomInfo().put("address", addr);
        }

        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            originator.setIp(inetAddress.getHostAddress());
            originator.setHostname(inetAddress.getHostName());
        } catch (UnknownHostException e) {
            originator.setHostname("Unknown hostname");
            originator.setIp("Unknown ip address");
        }
        originator.setProcessId(Converter.getPID());

        if (isRestMessage) {
            String accept = (String) message.get(Message.ACCEPT_CONTENT_TYPE);
            if (null != accept) {
                event.getCustomInfo().put("Accept", accept);
            }
        }

        String httpMethod = (String) message.get(Message.HTTP_REQUEST_METHOD);
        if (null != httpMethod) {
            event.getCustomInfo().put("HTTP_Method", httpMethod);
        }

        String contentType = (String) message.get(Message.CONTENT_TYPE);
        event.getCustomInfo().put("Content_Type", contentType);

        Integer responseCode = (Integer) message.get(Message.RESPONSE_CODE);
        if (null != responseCode) {
            event.getCustomInfo().put("Response_Code", responseCode.toString());
        }

        SecurityContext sc = message.get(SecurityContext.class);
        if (sc != null && sc.getUserPrincipal() != null) {
            originator.setPrincipal(sc.getUserPrincipal().getName());
        }

        if (originator.getPrincipal() == null) {
            AuthorizationPolicy authPolicy = message.get(AuthorizationPolicy.class);
            if (authPolicy != null) {
                originator.setPrincipal(authPolicy.getUserName());
            }
        }

        EventTypeEnum eventType = getEventType(message);
        event.setEventType(eventType);

        CustomInfo customInfo = CustomInfo.getOrCreateCustomInfo(message);
        // System.out.println("custom props: " + customInfo);
        event.getCustomInfo().putAll(customInfo);

        return event;
    }

    private String getRestOperationName(Message message) {
        boolean isRequestor = MessageUtils.isRequestor(message);
        boolean isOutbound = MessageUtils.isOutbound(message);
        String opName = "";
        if (isRequestor) {
            if (isOutbound) {
                String path = message.get(Message.REQUEST_URI).toString()
                        .substring(message.get(Message.BASE_PATH).toString().length());
                if (null == path || path.isEmpty())
                    path = "/";
                opName = message.get(Message.HTTP_REQUEST_METHOD).toString()
                        .concat("[".concat(path).concat("]"));
            } else {
                String path = message
                        .getExchange()
                        .getOutMessage()
                        .get(Message.REQUEST_URI)
                        .toString()
                        .substring(
                                message.getExchange().getOutMessage().get(Message.BASE_PATH).toString()
                                        .length());
                if (null == path || path.isEmpty())
                    path = "/";
                opName = message.getExchange().getOutMessage().get(Message.HTTP_REQUEST_METHOD).toString()
                        .concat("[").concat(path).concat("]");
            }
        } else {
            if (!isOutbound) {
                String path = message.get(Message.REQUEST_URI).toString()
                        .substring(message.get(Message.BASE_PATH).toString().length());
                if (null == path || path.isEmpty())
                    path = "/";
                opName = message.get(Message.HTTP_REQUEST_METHOD).toString()
                        .concat("[".concat(path).concat("]"));
            } else {
                String path = message
                        .getExchange()
                        .getInMessage()
                        .get(Message.REQUEST_URI)
                        .toString()
                        .substring(
                                message.getExchange().getInMessage().get(Message.BASE_PATH).toString()
                                        .length());
                if (null == path || path.isEmpty())
                    path = "/";
                opName = message.getExchange().getInMessage().get(Message.HTTP_REQUEST_METHOD).toString()
                        .concat("[").concat(path).concat("]");
            }
        }
        return opName;
    }

    /**
     * Get MessageId string. 
     * if enforceMessageIDTransfer=true or WS-Addressing enabled explicitly (i.e with <wsa:addressing/> feature), 
     * then MessageId is not null and conform with the definition in the WS-Addressing Spec;
     * if enforceMessageIDTransfer=false and WS-Addressing doesn't enable,
     * then MessageId is null.
     * @param message the message
     * @return the message id
     */
    private String getMessageId(Message message) {
        String messageId = null;

        AddressingProperties addrProp = ContextUtils.retrieveMAPs(message, false,
                MessageUtils.isOutbound(message));
        if (addrProp != null) {
            messageId = addrProp.getMessageID().getValue();
        }

        return messageId;
    }

    /**
     * Gets the event type from message.
     *
     * @param message the message
     * @return the event type
     */
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

    private String getOperationName(Message message) {
        String operationName = null;
        BindingOperationInfo boi = null;

        boi = message.getExchange().getBindingOperationInfo();
        if (null == boi) {
            // get BindingOperationInfo from message content
            boi = getOperationFromContent(message);
        }

        // if BindingOperationInfo is still null, try to get it from Request
        // message content
        if (null == boi) {
            Message inMsg = message.getExchange().getInMessage();
            if (null != inMsg) {
                Message reqMsg = inMsg.getExchange().getInMessage();
                if (null != reqMsg) {
                    boi = getOperationFromContent(reqMsg);
                }
            }
        }

        if (null != boi) {
            operationName = boi.getName().toString();
        }

        return operationName;
    }

    private BindingOperationInfo getOperationFromContent(Message message) {
        BindingOperationInfo boi = null;
        XMLStreamReader xmlReader = message.getContent(XMLStreamReader.class);
        if (null != xmlReader) {
            QName qName = xmlReader.getName();
            boi = ServiceModelUtil.getOperation(message.getExchange(), qName);
        }
        return boi;
    }

    /**
     * Gets the message payload.
     *
     * @param message the message
     * @return the payload
     */
    protected String getPayload(Message message) {
        try {
            String encoding = (String) message.get(Message.ENCODING);
            if (encoding == null) {
                encoding = "UTF-8";
            }
            CachedOutputStream cos = message.getContent(CachedOutputStream.class);
            if (cos == null) {
                LOG.warning("Could not find CachedOutputStream in message."
                        + " Continuing without message content");
                return "";
            }
            return new String(cos.getBytes(), encoding);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the max message content length.
     * 
     * @return the max content length
     */
    public int getMaxContentLength() {
        return maxContentLength;
    }

    /**
     * Sets the max message content length.
     * 
     * @param maxContentLength
     *            the new max content length
     */
    public void setMaxContentLength(int maxContentLength) {
        this.maxContentLength = maxContentLength;
    }

    /**
     * Handle content length.
     * 
     * @param event
     *            the event
     */
    private void handleContentLength(Event event) {
        if (event.getContent() == null) {
            return;
        }

        if (maxContentLength == -1 || event.getContent().length() <= maxContentLength) {
            return;
        }

        if (maxContentLength < CUT_START_TAG.length() + CUT_END_TAG.length()) {
            event.setContent("");
            event.setContentCut(true);
            return;
        }

        int contentLength = maxContentLength - CUT_START_TAG.length() - CUT_END_TAG.length();
        event.setContent(CUT_START_TAG + event.getContent().substring(0, contentLength) + CUT_END_TAG);
        event.setContentCut(true);
    }

    /**
     * check if a Message is a Rest Message
     * 
     * @param message
     * @return
     */
    private boolean isRestMessage(Message message) {
        String resName = (String) message.getExchange().get("org.apache.cxf.resource.operation.name");
        return (resName != null && !resName.isEmpty()) || (!(message.getExchange().getBinding() instanceof SoapBinding));
    }
}
