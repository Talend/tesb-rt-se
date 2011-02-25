package ch.zurich.monitoring.handler.impl;

import java.util.logging.Logger;

import javax.persistence.EntityManager;

import org.sopera.monitoring.event.Event;
import org.sopera.monitoring.handler.CustomHandlerPostProcessing;
import org.sopera.monitoring.handler.PersistenceHandler;
import org.sopera.monitoring.handler.impl.AbstractFilteredHandler;

/**
 * Example update handler. Sets the message id to 'post'
 * @author cschmuelling
 *
 */
public class UpdateHandler extends AbstractFilteredHandler<Event> implements
		CustomHandlerPostProcessing<Event> {
	private static Logger logger = Logger.getLogger(UpdateHandler.class
			.getName());

	public void handleEvent(Event event,
			PersistenceHandler<Event> persistenceHandler) {
		logger.info("UpdateHandler called for event "
				+ event.getMessageInfo().getMessageId());
		event.getMessageInfo().setMessageId("post");
		
		 EntityManager em = persistenceHandler.getEntityManager();
		 logger.info("Updating event");
		 em.persist(event);
	}
}
