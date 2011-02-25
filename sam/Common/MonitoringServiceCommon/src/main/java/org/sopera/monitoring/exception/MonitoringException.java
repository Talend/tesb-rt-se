package org.sopera.monitoring.exception;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sopera.monitoring.event.Event;

public class MonitoringException extends RuntimeException {
	private static Logger logger = Logger.getLogger(MonitoringException.class
			.getName());
	private static final long serialVersionUID = 3127641209174705808L;
	private String code;
	private String message;
	private List<Event> events;

	public MonitoringException(String code, String message, Throwable t) {
		super(t);
		this.code = code;
		this.message = message;
	}

	public MonitoringException(String code, String message, Throwable t,
			Event event) {
		super(t);
		this.code = code;
		this.message = message;
		this.events = new ArrayList<Event>();
		this.events.add(event);
	}

	public MonitoringException(String code, String message, Throwable t,
			List<Event> events) {
		super(t);
		this.code = code;
		this.message = message;
		this.events = events;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	/**
	 * Prints the error message as log message
	 * 
	 * @param e
	 */
	public void logException(Level level) {
		StringBuilder message = new StringBuilder();
		message.append("\n----------------------------------------------------\n");
		message.append("MonitoringException\n");
		message.append("----------------------------------------------------\n");
		message.append("Code:    " + this.code + "\n");
		message.append("Message: " + this.message + "\n");
		message.append("----------------------------------------------------\n");
		if (events != null) {
			for (Event event : events) {
				message.append("Event:\n");
				if (event.getMessageInfo() != null) {
					String flowId = event.getMessageInfo().getFlowId();
					String messageId = event.getMessageInfo().getMessageId();
					message.append("Message id: " + messageId + "\n");
					message.append("Flow id:    " + flowId + "\n");
					message.append("----------------------------------------------------\n");
				} else {
					message.append("No message id and no flow id\n");
				}
			}
		}
		message.append("----------------------------------------------------\n");
		message.append("\n");
		logger.log(level, message.toString(), this);
	}

	public void addEvent(Event event) {
		if (this.events == null)
			this.events = new ArrayList<Event>();
		this.events.add(event);
	}

	public void addEvents(List<Event> events) {
		if (this.events == null)
			this.events = new ArrayList<Event>();
		this.events.addAll(events);
	}
}
