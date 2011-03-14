package org.talend.esb.sam.agent.mapper;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.logging.Logger;

import org.apache.cxf.binding.soap.SoapBinding;
import org.apache.cxf.binding.soap.model.SoapBindingInfo;
import org.apache.cxf.message.Message;
import org.apache.cxf.service.model.MessageInfo.Type;
import org.apache.cxf.ws.addressing.ContextUtils;
import org.talend.esb.sam.agent.interceptor.FlowIdHelper;
import org.talend.esb.sam.agent.interceptor.InterceptorType;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.event.EventTypeEnum;
import org.talend.esb.sam.common.event.MessageInfo;
import org.talend.esb.sam.common.event.Originator;

public final class MessageToEventMapperImpl implements MessageToEventMapper {
    private static Logger logger = Logger.getLogger(MessageToEventMapperImpl.class.getName());
    
    /* (non-Javadoc)
     * @see org.talend.esb.sam.agent.producer.MessageToEventMapper#mapToEvent(org.apache.cxf.message.Message, org.talend.esb.sam.agent.interceptor.InterceptorType, java.lang.String)
     */
    @Override
    public Event mapToEvent(Message message, InterceptorType type, String content) {
        logger.info("Create event and delegate to collector.");
        Event event = new Event();
        MessageInfo messageInfo = new MessageInfo();
        Originator originator = new Originator();

        event.setMessageInfo(messageInfo);
        event.setOriginator(originator);
        event.setContent(content);
        event.setEventType(null);
        Date date = new Date();
        event.setTimestamp(date);

        messageInfo.setFlowId(FlowIdHelper.getFlowIdFromProperty(message));
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

        EventTypeEnum eventType = getEventType(message, type);
        event.setEventType(eventType);
        return event;
    }

    private EventTypeEnum getEventType(Message message, InterceptorType type) {
        Object messageInfoObject = message.get("org.apache.cxf.service.model.MessageInfo");
        if (messageInfoObject instanceof org.apache.cxf.service.model.MessageInfo) {
            org.apache.cxf.service.model.MessageInfo info = (org.apache.cxf.service.model.MessageInfo)messageInfoObject;
            if (Type.INPUT.equals(info.getType())) {
                if (InterceptorType.IN.equals(type)) {
                    return EventTypeEnum.REQ_IN;
                } else if (InterceptorType.OUT.equals(type)) {
                    return EventTypeEnum.REQ_OUT;
                }
            } else if (Type.OUTPUT.equals(info.getType())) {
                if (InterceptorType.IN.equals(type)) {
                    return EventTypeEnum.RESP_IN;
                } else if (InterceptorType.OUT.equals(type)) {
                    return EventTypeEnum.RESP_OUT;
                }
            }
            logger.severe("No event type identified.");
        }
        if (InterceptorType.IN_FAULT.equals(type)) {
            return EventTypeEnum.FAULT_IN;
        }
        if (InterceptorType.OUT_FAULT.equals(type)) {
            return EventTypeEnum.FAULT_OUT;
        }
        return EventTypeEnum.UNKNOWN;
    }
}
