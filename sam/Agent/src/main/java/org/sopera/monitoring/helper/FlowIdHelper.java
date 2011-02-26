package org.sopera.monitoring.helper;

import java.util.List;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.cxf.headers.Header;
import org.apache.cxf.message.Message;
import org.sopera.monitoring.event.MonitoringEventData;
import org.sopera.monitoring.event.MonitoringEventDataImpl;
import org.w3c.dom.Node;


public class FlowIdHelper {

	private static Logger logger = Logger.getLogger(FlowIdHelper.class
			.getName());

	public static final QName FLOW_ID_QNAME = new QName(
			"http://www.sopera.com/monitoring/flowId/v1", "flowId");

	
	/**
	 * Get MonitoringEventData from message
	 * 
	 * @param message
	 * @return new instance of MonitoringEventData if there is none.
	 */
	public static MonitoringEventData getMonitoringEventData(Message message) {
		return getMonitoringEventData(message, true);
	}
	
	/**
	 * Get MonitoringEventData from message
	 * 
	 * @param message
	 * @return new instance of MonitoringEventData if there is none.
	 */
	public static MonitoringEventData getMonitoringEventData(Message message, boolean create) {
		MonitoringEventData ed = (MonitoringEventData)message.get(MonitoringEventData.class);
		if (ed == null && create == true) {
			ed = new MonitoringEventDataImpl();
			message.put(MonitoringEventData.class, ed);	
		}
		return ed;
	}
	

	/**
	 * Get flowId from message soap header.
	 * 
	 * @param message
	 * @return null if there is no flowId found.
	 */
	public static String getFlowIdFromSoapHeader(Message message) {
		logger.fine("Try to find flowId in message");

		Message inMessage = null;
		Message outMessage = null;
		Message outFaultMessage = null;
		String flowId = null;

		if (message.getExchange() != null) {
			inMessage = message.getExchange().getInMessage();
			outMessage = message.getExchange().getOutMessage();
			outFaultMessage = message.getExchange().getOutFaultMessage();
		}

		// Try to find flowId in outgoiung messages
		if (outMessage != null) {
			flowId = getFlowIdFromMessage(outMessage);
			logger.fine("There is an outgoing message. Try to find flowId. flowId="
					+ flowId);
		} else if (outFaultMessage != null) {
			flowId = getFlowIdFromMessage(outFaultMessage);
			logger.fine("There is an outgoing fault message. Try to find flowId. flowId="
					+ flowId);
		}

		// if there was no outgoing message or no flowId was found -> try find
		// flowId from incoming message
		if (flowId == null) {
			flowId = getFlowIdFromMessage(inMessage != null ? inMessage
					: message);
			logger.fine("No outgoing messages or no flowId inside outgoing messages. Try to find flowId in incoming message. flowId="
					+ flowId);
		}

		return flowId;
	}
	
	public static String getFlowIdFromProperty(Message message) {
		MonitoringEventData ed = (MonitoringEventData)message.get(MonitoringEventData.class);
		if (ed != null) return ed.getFlowId();
		else return null;
	}
	
	@SuppressWarnings("rawtypes")
	private static String getFlowIdFromMessage(Message message) {
		Message currentMessage = message;

		// Get HeaderList
		Object headerObject = currentMessage.get(Header.HEADER_LIST);

		if (headerObject == null) {
			logger.fine("No headers found");
			return null;
		}

		if (!(headerObject instanceof List)) {
			logger.fine("No headers found. (No list of headers)");
			return null;
		}

		List headerList = (List) headerObject;

		for (Object object : headerList) {
			logger.finest("Check object for header: "
					+ object.getClass().toString());
			if (object instanceof Header) {
				Header header = (Header) object;
				logger.fine("Search flowId in header: "
						+ header.getName().toString());
				if (FLOW_ID_QNAME.equals(header.getName())) {
					if (header.getObject() instanceof String) {
						String flowId = (String) header.getObject();
						return flowId;
					} else if (header.getObject() instanceof Node) {
						Node headerNode = (Node) header.getObject();
						String textContent = headerNode.getTextContent();
						return textContent;
					} else {
						logger.severe("Found FlowId header but value is not a String or a Node! Value: "
								+ header.getObject().toString());
					}
				}
			}
		}

		// nothing found
		return null;
	}

}
