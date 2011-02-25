package org.sopera.monitoring.chain;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.sopera.monitoring.event.Event;
import org.sopera.monitoring.handler.CustomHandlerPostProcessing;
import org.sopera.monitoring.handler.PersistenceHandler;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Wrapper for hiding Cammand interface and dependency to commons-chain 
 * @author cschmuelling
 *
 */
public class CustomHandlerPostPersistCommandWrapper<E extends Event> implements Command {
	private E event;
	private CustomHandlerPostProcessing<E> customHandlerPostPersist;
	private PersistenceHandler<E> persistanceHandler;

	public CustomHandlerPostPersistCommandWrapper(
			CustomHandlerPostProcessing<E> customHandlerPostPersist, E event,
			PersistenceHandler<E> persistanceHandler) {
		this.customHandlerPostPersist = customHandlerPostPersist;
		this.event = event;
		this.persistanceHandler = persistanceHandler;
	}

	@Transactional(value = "defaultTransaction", propagation = Propagation.REQUIRED)
	public boolean execute(Context context) throws Exception {
		if(customHandlerPostPersist.filter(event)==false)
			customHandlerPostPersist.handleEvent(event, persistanceHandler);
		return false;
	}

}
