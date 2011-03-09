package org.talend.esb.sam.common.util;

import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.event.EventTypeEnum;
import org.talend.esb.sam.common.event.MessageInfo;
import org.talend.esb.sam.common.event.Originator;

/**
 * TODO Sue Builder pattern?
 */
public class EventCreator {
	private static final Logger logger = Logger.getLogger(EventCreator.class
			.getName());
	
	public EventCreator(){
	}
	
	public Event generateEvent(){
		Date cal = Calendar.getInstance().getTime();
		String generated = Integer.valueOf(cal.toString().hashCode()).toString();
		return createEvent("<request><values><value>a</value></values></request>", cal, EventTypeEnum.REQ_IN, "JUnit", "localhost", "127.0.0.1", generated, generated, generated, "testOperation", "testPort", "JAVA");
	}

	public Event createEvent(String content, Date timestamp, EventTypeEnum eventType
			, String customOriginatorId, String hostname, String ip, String processId, String flowId,
			String messageId, String operarionName, String portType, String transportType) {
		
		Event event = new Event();
		event.setContent(content);
		event.setTimestamp(timestamp);
		event.setEventType(eventType);
		
		Originator originator = new Originator();
		originator.setCustomId(customOriginatorId);
		originator.setHostname(hostname);
		originator.setIp(ip);
		originator.setProcessId(processId);
		event.setOriginator(originator);
		
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

		if (event.getTimestamp() == null) {
			logger.info("Set timestamp to current Date");
			event.setTimestamp(Calendar.getInstance().getTime());
		}
		if (event.getEventType() == null) {
			logger.info("Set eventType to REQ_IN");
			event.setEventType(EventTypeEnum.REQ_IN);
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
