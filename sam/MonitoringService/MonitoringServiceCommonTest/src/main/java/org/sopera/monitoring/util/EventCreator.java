package org.sopera.monitoring.util;

import java.util.Calendar;
import java.util.logging.Logger;

import org.sopera.monitoring.event.Event;
import org.sopera.monitoring.event.EventInfo;
import org.sopera.monitoring.event.EventType;
import org.sopera.monitoring.event.MessageInfo;
import org.sopera.monitoring.event.Originator;

public class EventCreator<E extends Event> {
	private static final Logger logger = Logger.getLogger(EventCreator.class
			.getName());
	
	private Class<E> clazz;
	
	public EventCreator(Class<E> clazz){
		this.clazz = clazz;
	}
	
	public E generateEvent(){
		Calendar cal = Calendar.getInstance();
		String generated = Integer.valueOf(cal.toString().hashCode()).toString();
		return createEvent("<request><values><value>a</value></values></request>", "<noExtension/>", cal, EventType.REQ_IN, "JUnit", "localhost", "127.0.0.1", generated, generated, generated, "testOperation", "testPort", "JAVA");
	}

	public E createEvent(String content, String extension, Calendar timestamp, EventType eventType
			, String customOriginatorId, String hostname, String ip, String processId, String flowId,
			String messageId, String operarionName, String portType, String transportType) {
		
		E event;
		try {
			event = clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			return null;
		}
		event.setContent(content);
		event.setExtension(extension);
		
		event.setEventInfo(new EventInfo());
		EventInfo eventInfo = event.getEventInfo();
		eventInfo.setTimestamp(timestamp);
		eventInfo.setEventType(eventType);
		
		eventInfo.setOriginator(new Originator());
		Originator originator = eventInfo.getOriginator();
		originator.setCustomId(customOriginatorId);
		originator.setHostname(hostname);
		originator.setIp(ip);
		originator.setProcessId(processId);
		
		event.setMessageInfo(new MessageInfo());
		MessageInfo messageInfo = event.getMessageInfo();
		messageInfo.setFlowId(flowId);
		messageInfo.setMessageId(messageId);
		messageInfo.setOperationName(operarionName);
		messageInfo.setPortType(portType);
		messageInfo.setTransportType(transportType);

		if (event.getContent() == null || event.getContent().equals("")) {
			logger.info("Set content empty element");
			event.setContent("<empty/>");
		}
		if (event.getExtension() == null || event.getExtension().equals("")) {
			logger.info("Set extension empty element");
			event.setExtension("<empty/>");
		}

		if (eventInfo.getTimestamp() == null) {
			logger.info("Set timestamp to current Date");
			eventInfo.setTimestamp(Calendar.getInstance());
		}
		if (eventInfo.getEventType() == null) {
			logger.info("Set eventType to REQ_IN");
			eventInfo.setEventType(EventType.REQ_IN);
		}

		if (originator.getCustomId() == null
				|| originator.getCustomId().equals("")) {
			logger.info("Set custom id to empty");
			originator.setCustomId("empty");
		}
		if (originator.getHostname() == null
				|| originator.getHostname().equals("")) {
			logger.info("Set hostname to empty");
			originator.setHostname("empty");
		}
		if (originator.getIp() == null || originator.getIp().equals("")) {
			logger.info("Set ip to empty");
			originator.setIp("empty");
		}
		if (originator.getProcessId() == null
				|| originator.getProcessId().equals("")) {
			logger.info("Set process id to empty");
			originator.setProcessId("empty");
		}

		if (messageInfo.getFlowId() == null
				|| messageInfo.getFlowId().equals("")) {
			logger.info("Set flow id to empty");
			messageInfo.setFlowId("empty");
		}
		if (messageInfo.getMessageId() == null
				|| messageInfo.getMessageId().equals("")) {
			logger.info("Set message id to empty");
			messageInfo.setMessageId("empty");
		}
		if (messageInfo.getOperationName() == null
				|| messageInfo.getOperationName().equals("")) {
			logger.info("Set operation name to empty");
			messageInfo.setOperationName("empty");
		}
		if (messageInfo.getPortType() == null
				|| messageInfo.getPortType().equals("")) {
			logger.info("Set porttype to empty");
			messageInfo.setPortType("empty");
		}
		if (messageInfo.getTransportType() == null
				|| messageInfo.getTransportType().equals("")) {
			logger.info("Set pransport type to empty");
			messageInfo.setTransportType("empty");
		}

		if (event.getPersistedId() != null) {
			logger.warning("Persisted Id is not null, but event is not persisted jet!");
		}

		return event;
	}
}
