package org.sopera.monitoring.chain;

import java.util.List;

import org.apache.commons.chain.Context;
import org.sopera.monitoring.event.Event;
import org.sopera.monitoring.handler.PersistenceHandler;

/**
 * Wrapper for hiding Cammand interface and dependency to commons-chain 
 * @author cschmuelling
 *
 */
public class PersistHandlerWriteEventCommandWrapper<E extends Event> extends
		PersistHandlerCommandWrapper<E> {

	public PersistHandlerWriteEventCommandWrapper(
			PersistenceHandler<E> persistanceHandler, List<E> events) {
		super(persistanceHandler, events);
	}

	public boolean execute(Context context) throws Exception {
		persistanceHandler.writeEvents(events);
		return false;
	}
}
