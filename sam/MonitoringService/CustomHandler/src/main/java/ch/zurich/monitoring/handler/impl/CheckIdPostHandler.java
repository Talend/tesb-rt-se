package ch.zurich.monitoring.handler.impl;

import java.util.logging.Logger;

import org.sopera.monitoring.event.Event;
import org.sopera.monitoring.handler.CustomHandlerPostProcessing;
import org.sopera.monitoring.handler.PersistenceHandler;
import org.sopera.monitoring.handler.impl.AbstractFilteredHandler;

/**
 * Checks the database Id
 * 
 * @author cschmuelling
 * 
 */
public class CheckIdPostHandler extends AbstractFilteredHandler<Event> implements CustomHandlerPostProcessing<Event> {

	private static final Logger logger = Logger
			.getLogger(CheckIdPostHandler.class.getName());

	public CheckIdPostHandler() {
	}

	public void handleEvent(Event event, PersistenceHandler<Event> persistanceHandler) {
		if (event.getPersistedId() == null) {
			logger.severe("Persisted event didn't get a Id! Evente flowId: "
					+ event.getMessageInfo().getFlowId() + " messageId: "
					+ event.getMessageInfo().getMessageId());
		} else {
			logger.info("Id check for event flowId: "
					+ event.getMessageInfo().getFlowId() + " messageId: "
					+ event.getMessageInfo().getMessageId()
					+ " was OK. Persisted Id: " + event.getPersistedId());
		}
	}
}
