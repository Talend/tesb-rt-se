package org.sopera.monitoring.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.impl.ChainBase;
import org.apache.commons.chain.impl.ContextBase;
import org.sopera.monitoring.chain.CustomHandlerPostPersistCommandWrapper;
import org.sopera.monitoring.chain.EventManipulatorCommandWrapper;
import org.sopera.monitoring.chain.PersistHandlerBeginTransactionCommandWrapper;
import org.sopera.monitoring.chain.PersistHandlerCommitTransactionCommandWrapper;
import org.sopera.monitoring.chain.PersistHandlerWriteEventCommandWrapper;
import org.sopera.monitoring.event.Event;
import org.sopera.monitoring.exception.MonitoringException;
import org.sopera.monitoring.filter.EventFilter;
import org.sopera.monitoring.handler.CustomHandlerPostProcessing;
import org.sopera.monitoring.handler.CustomHandlerPreProcessing;
import org.sopera.monitoring.handler.EventManipulator;
import org.sopera.monitoring.handler.PersistenceHandler;
import org.sopera.monitoring.service.MonitoringService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of MonitoringService. This service needs all handler for
 * manipulating events.
 * 
 * @author cschmuelling
 * 
 */
public class MonitoringServiceImpl<E extends Event> implements
		MonitoringService<E> {

	private static Logger logger = Logger.getLogger(MonitoringServiceImpl.class
			.getName());

	private List<EventFilter<E>> eventFilter;
	private List<EventManipulator<E>> eventManipulator;
	private List<CustomHandlerPreProcessing<E>> preHandler;
	private List<CustomHandlerPostProcessing<E>> postHandler;
	private PersistenceHandler<E> persistenceHandler;

	/**
	 * Sets a list of event filter. A filtered event will not processed.
	 * 
	 * @param eventFilter
	 */
	public void setEventFilter(List<EventFilter<E>> eventFilter) {
		this.eventFilter = eventFilter;
	}

	/**
	 * Sets a list of event manipulator. Normally it's used for password
	 * filtering and cutting the content.
	 * 
	 * @param eventManipulator
	 */
	public void setEventManipulator(List<EventManipulator<E>> eventManipulator) {
		this.eventManipulator = eventManipulator;
	}

	/**
	 * Sets a list with all custom pre handler.
	 * 
	 * @param preHandler
	 */
	public void setPreHandler(List<CustomHandlerPreProcessing<E>> preHandler) {
		this.preHandler = preHandler;
	}

	/**
	 * Sets a list with all custom post handler
	 * 
	 * @param postHandler
	 */
	public void setPostHandler(List<CustomHandlerPostProcessing<E>> postHandler) {
		this.postHandler = postHandler;
	}

	/**
	 * Set a persistence handler. For example the DefaultDatabaseHandler
	 * 
	 * @param persistenceHandler
	 */
	public void setPersistenceHandler(PersistenceHandler<E> persistenceHandler) {
		this.persistenceHandler = persistenceHandler;
	}

	/**
	 * Executes all event manipulating handler and writes the event with persist
	 * handler
	 */
	@Transactional(value = "defaultTransaction", propagation = Propagation.REQUIRED)
	public void putEvents(List<E> events) {
		ChainBase chainBase = new ChainBase();

		logger.info("Create execution chain");

		// Execute event filter directly
		List<E> filteredEvents = new ArrayList<E>();
		if (eventFilter != null && eventFilter.size() > 0) {
			for (EventFilter<E> filter : eventFilter) {
				for (E event : events) {
					if (!filter.filter(event))
						filteredEvents.add(event);
				}
			}
			if (filteredEvents.size() == 0)
				return;
		} else {
			filteredEvents = events;
		}

		// Execute Filter (for example password filter and cutting content
		if (eventManipulator != null && eventManipulator.size() > 0)
			for (EventManipulator<E> current : eventManipulator) {
				for (E event : filteredEvents) {
					chainBase.addCommand(new EventManipulatorCommandWrapper<E>(
							current, event));
					logger.info("Added filter " + current.getClass().getName());
				}
			}

		// Execute custom handler before persisting the events
		if (preHandler != null && preHandler.size() > 0)
			for (CustomHandlerPreProcessing<E> current : preHandler) {
				for (E event : filteredEvents) {
					chainBase.addCommand(new EventManipulatorCommandWrapper<E>(
							current, event));
					logger.info("Added custom pre handler "
							+ current.getClass().getName());
				}
			}

		// Start transaction for other persistent handler. If default
		// installation is used, there is no need for this command. see
		// @Transactional on this method. Spring manages this transaction.
		chainBase
				.addCommand(new PersistHandlerBeginTransactionCommandWrapper<E>(
						persistenceHandler));
		logger.info("Added persistence handler (beginTransaction)"
				+ persistenceHandler.getClass().getName());

		// Write Events
		chainBase.addCommand(new PersistHandlerWriteEventCommandWrapper<E>(
				persistenceHandler, filteredEvents));
		logger.info("Added persistence handler (writeEvents)"
				+ persistenceHandler.getClass().getName());

		// Execute custom handler after persisting the events
		if (postHandler != null && postHandler.size() > 0)
			for (CustomHandlerPostProcessing<E> current : postHandler) {
				for (E event : filteredEvents) {
					chainBase
							.addCommand(new CustomHandlerPostPersistCommandWrapper<E>(
									current, event, persistenceHandler));
					logger.info("Added custom post handler "
							+ current.getClass().getName());
				}
			}

		chainBase
				.addCommand(new PersistHandlerCommitTransactionCommandWrapper<E>(
						persistenceHandler));
		logger.info("Added persistence handler (commitTransaction)"
				+ persistenceHandler.getClass().getName());

		try {
			chainBase.execute(new ContextBase());
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error in executing chain.");
			if(e instanceof MonitoringException)
				throw (MonitoringException)e;
			throw new MonitoringException("002",
					"Unknown error while chain execution", e);
		}
	}
}
