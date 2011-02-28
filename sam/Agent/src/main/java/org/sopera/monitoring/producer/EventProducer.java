package org.sopera.monitoring.producer;

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
import org.sopera.monitoring.collector.EventCollector;
import org.sopera.monitoring.event.Event;
import org.sopera.monitoring.event.EventInfo;
import org.sopera.monitoring.event.EventType;
import org.sopera.monitoring.event.MessageInfo;
import org.sopera.monitoring.event.Originator;
import org.sopera.monitoring.helper.FlowIdHelper;
import org.sopera.monitoring.interceptor.InterceptorType;

public class EventProducer {

	private static Logger logger = Logger.getLogger(EventProducer.class
			.getName());

	private EventCollector eventCollector;
	private boolean logMessageContent;

	public void setLogMessageContent(boolean logMessageContent) {
		this.logMessageContent = logMessageContent;
	}

	public boolean isLogMessageContent() {
	       return logMessageContent;
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
	    EventInfo eventInfo = new EventInfo();
	    MessageInfo messageInfo = new MessageInfo();
	    Originator originator = new Originator();

	    event.setEventInfo(eventInfo);
	    event.setMessageInfo(messageInfo);
	    eventInfo.setOriginator(originator);

	    event.setContent(content);

	    eventInfo.setEventType(null);

	    Date date = new Date();
	    eventInfo.setTimestamp(date);

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

	    Object messageInfoObject = message
	    .get("org.apache.cxf.service.model.MessageInfo");
	    if (messageInfoObject instanceof org.apache.cxf.service.model.MessageInfo) {
	        org.apache.cxf.service.model.MessageInfo info = (org.apache.cxf.service.model.MessageInfo) messageInfoObject;
	        if (Type.INPUT.equals(info.getType())) {
	            if (InterceptorType.IN.equals(type)) {
	                eventInfo.setEventType(EventType.REQ_IN);
	            }
	            if (InterceptorType.OUT.equals(type)) {
	                eventInfo.setEventType(EventType.REQ_OUT);
	            }
	        }
	        if (Type.OUTPUT.equals(info.getType())) {
	            if (InterceptorType.IN.equals(type)) {
	                eventInfo.setEventType(EventType.RESP_IN);
	            }
	            if (InterceptorType.OUT.equals(type)) {
	                eventInfo.setEventType(EventType.RESP_OUT);
	            }
	        }
	        if (eventInfo.getEventType() == null) {
	            logger.severe("No event type identified.");
	        }
	    }
	    if (InterceptorType.IN_FAULT.equals(type)) {
	        eventInfo.setEventType(EventType.FAULT_IN);
	    }
	    if (InterceptorType.OUT_FAULT.equals(type)) {
	        eventInfo.setEventType(EventType.FAULT_OUT);
	    }
	    return event;
	}
}
