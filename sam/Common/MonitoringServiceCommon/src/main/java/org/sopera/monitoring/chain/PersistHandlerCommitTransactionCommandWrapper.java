package org.sopera.monitoring.chain;

import org.apache.commons.chain.Context;
import org.sopera.monitoring.event.Event;
import org.sopera.monitoring.handler.PersistenceHandler;

/**
 * Wrapper for hiding Cammand interface and dependency to commons-chain 
 * @author cschmuelling
 *
 */
public class PersistHandlerCommitTransactionCommandWrapper<E extends Event> extends
		PersistHandlerCommandWrapper<E> {

	public PersistHandlerCommitTransactionCommandWrapper(
			PersistenceHandler<E> persistanceHandler) {
		super(persistanceHandler, null);
	}

	public boolean execute(Context context) throws Exception {
		persistanceHandler.commitTransaction();
		return false;
	}
}
