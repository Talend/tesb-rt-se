package org.talend.esb.sam.agent.producer;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.cxf.binding.soap.SoapBinding;
import org.apache.cxf.binding.soap.model.SoapBindingInfo;
import org.apache.cxf.message.Message;
import org.apache.cxf.service.model.MessageInfo.Type;
import org.apache.cxf.ws.addressing.ContextUtils;
import org.talend.esb.sam.agent.collector.EventCollector;
import org.talend.esb.sam.agent.interceptor.FlowIdHelper;
import org.talend.esb.sam.agent.interceptor.InterceptorType;
import org.talend.esb.sam.common.event.CustomInfo;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.event.EventTypeEnum;
import org.talend.esb.sam.common.event.MessageInfo;
import org.talend.esb.sam.common.event.Originator;

public class EventProducer {

	private static Logger logger = Logger.getLogger(EventProducer.class
			.getName());

	private EventCollector eventCollector;
	private boolean logMessageContent;
	
	private Map<String,Object> customInfo;

	public void setLogMessageContent(boolean logMessageContent) {
		this.logMessageContent = logMessageContent;
	}

	public boolean isLogMessageContent() {
	       return logMessageContent;
	}

	public Map<String, Object> getCustomInfo() {
		return customInfo;
	}

	public void setCustomInfo(Map<String, Object> customInfo) {
		this.customInfo = customInfo;
	}

	public EventProducer() {
		logger.info("EventProducer created.");
	}

	public EventCollector getEventCollector() {
		return eventCollector;
	}

	public void setEventCollector(EventCollector eventCollector) {
		this.eventCollector = eventCollector;
		logger.info("EventCollector set.");
	}

	/**
	 * Hanldes a message and creates the event.
	 * 
	 * @param message
	 * @param type
	 * @param content
	 */
	public void handleMessage(Message message, InterceptorType type,
			String content) {
		Event event = mapToEvent(message, type, content);
		eventCollector.putEvent(event);
	}
	
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
	    messageInfo.setOperationName(message.getExchange()
	                                 .getBindingOperationInfo().getName().toString());
	    messageInfo.setPortType(message.getExchange().getBinding()
	                            .getBindingInfo().getService().getInterface().getName()
	                            .toString());

	    if (message.getExchange().getBinding() instanceof SoapBinding) {
	        SoapBinding soapBinding = (SoapBinding) message.getExchange()
	        .getBinding();
	        if (soapBinding.getBindingInfo() instanceof SoapBindingInfo) {
	            SoapBindingInfo soapBindingInfo = (SoapBindingInfo) soapBinding
	            .getBindingInfo();
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

	    originator.setProcessId(ManagementFactory.getRuntimeMXBean().getName());

	    EventTypeEnum eventType = getEventType(message, type);
	    event.setEventType(eventType);
	    
	    event.setCustomInfoList(mapToCustomInfo());

	    return event;
	}

	private List<CustomInfo> mapToCustomInfo(){
		if (customInfo == null) return null;
		
		List<CustomInfo> ciList = new ArrayList<CustomInfo>();
		for (Map.Entry<String,Object> props : customInfo.entrySet()){
			CustomInfo ci = new CustomInfo();
			ci.setCustKey(props.getKey());
			ci.setCustValue(props.getValue());
			ciList.add(ci);
		}
		return ciList;
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
