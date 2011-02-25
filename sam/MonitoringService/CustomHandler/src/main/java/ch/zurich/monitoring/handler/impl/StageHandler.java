package ch.zurich.monitoring.handler.impl;

import org.sopera.monitoring.handler.CustomHandlerPostProcessing;
import org.sopera.monitoring.handler.CustomHandlerPreProcessing;
import org.sopera.monitoring.handler.PersistenceHandler;
import org.sopera.monitoring.handler.impl.AbstractFilteredHandler;

import ch.zurich.monitoring.event.ZurichEvent;

/**
 * Stage handler can add stage attribute to Event. It can be used as pre or as
 * post handler. Using it at both phases makes no sence.
 * 
 * @author cschmuelling
 * 
 */
public class StageHandler extends AbstractFilteredHandler<ZurichEvent> implements
		CustomHandlerPostProcessing<ZurichEvent>, CustomHandlerPreProcessing<ZurichEvent> {

	private String stage;

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	public void handleEvent(ZurichEvent event) {
		event.setStage(getStage());
	}

	public void handleEvent(ZurichEvent event,
			PersistenceHandler<ZurichEvent> persistenceHandler) {
		event.setStage(getStage());
		persistenceHandler.getEntityManager().persist(event);
	}

}
