package ch.zurich.monitoring.handler.impl;

import org.junit.Assert;
import org.junit.Test;
import org.sopera.monitoring.util.EventCreator;

import ch.zurich.monitoring.event.ZurichEvent;

public class StageHandlerTest {
	// private static final Logger logger = Logger
	// .getLogger(StageHandlerTest.class.getName());
	// private static final String PERSISTENCE_UNIT_NAME =
	// "testMonitoringServicePersistenceUnit";
	// private EntityManagerFactory factory;
	private static final String STAGE = "JUnit";

	@Test
	public void setStagePreTest() {
		StageHandler stageHandler = new StageHandler();
		stageHandler.setStage(STAGE);

		ZurichEvent event = new EventCreator<ZurichEvent>(ZurichEvent.class).generateEvent();
		stageHandler.handleEvent(event);

		Assert.assertTrue(event.getStage().equals(STAGE));
	}
}
