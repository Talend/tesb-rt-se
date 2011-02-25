package ch.zurich.monitoring.handler.impl;

import java.util.Calendar;
import java.util.logging.Logger;

import org.sopera.monitoring.event.Event;
import org.sopera.monitoring.event.EventInfo;
import org.sopera.monitoring.event.EventType;
import org.sopera.monitoring.event.MessageInfo;
import org.sopera.monitoring.event.Originator;
import org.sopera.monitoring.handler.CustomHandlerPreProcessing;
import org.sopera.monitoring.handler.impl.AbstractFilteredHandler;

/**
 * SetEmptyPreHandler sets all empty elements with "<empty/>" elements or
 * "empty" Strings
 * 
 * @author cschmuelling
 * 
 */
public class SetEmptyPreHandler extends AbstractFilteredHandler<Event>
		implements CustomHandlerPreProcessing<Event> {
	private static final Logger logger = Logger
			.getLogger(SetEmptyPreHandler.class.getName());

	public void handleEvent(Event event) {
		if (event.getContent() == null || event.getContent().equals("")) {
			logger.info("Set content empty element");
			event.setContent("<empty/>");
		}
		if (event.getExtension() == null || event.getExtension().equals("")) {
			logger.info("Set extension empty element");
			event.setExtension("<empty/>");
		}

		EventInfo eventInfo = event.getEventInfo();
		if (eventInfo.getTimestamp() == null) {
			logger.info("Set timestamp to current Date");
			eventInfo.setTimestamp(Calendar.getInstance());
		}
		if (eventInfo.getEventType() == null) {
			logger.info("Set eventType to REQ_IN");
			eventInfo.setEventType(EventType.REQ_IN);
		}

		Originator originator = eventInfo.getOriginator();
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

		MessageInfo messageInfo = event.getMessageInfo();
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

	}

}
