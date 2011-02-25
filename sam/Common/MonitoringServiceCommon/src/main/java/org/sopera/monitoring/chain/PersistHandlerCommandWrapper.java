package org.sopera.monitoring.chain;

import java.util.List;

import org.apache.commons.chain.Command;
import org.sopera.monitoring.event.Event;
import org.sopera.monitoring.handler.PersistenceHandler;

/**
 * Wrapper for hiding Cammand interface and dependency to commons-chain 
 * @author cschmuelling
 *
 */
public abstract class PersistHandlerCommandWrapper<E extends Event> implements Command {
	protected PersistenceHandler<E> persistanceHandler;
	protected List<E> events;

	public PersistHandlerCommandWrapper(PersistenceHandler<E> persistanceHandler,
			List<E> events) {
		this.persistanceHandler = persistanceHandler;
		this.events = events;
	}
}
