package org.sopera.monitoring.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.sopera.monitoring.event.Event;
import org.sopera.monitoring.exception.MonitoringException;
import org.sopera.monitoring.handler.PersistenceHandler;
import org.sopera.monitoring.handler.impl.DefaultDatabaseHandler;
import org.springframework.aop.framework.Advised;

public class DefaultDatabaseHandlerHelper {
	private static Logger logger = Logger
			.getLogger(DefaultDatabaseHandlerHelper.class.getName());

	@SuppressWarnings("unchecked")
	public static final DefaultDatabaseHandler<Event> castPersistenceHandler(
			PersistenceHandler<Event> persistenceHandler)
			throws MonitoringException {
		DefaultDatabaseHandler<Event> handler = null;

		if (persistenceHandler instanceof Advised) {
			Advised advised = (Advised) persistenceHandler;
			Object object = null;
			try {
				object = advised.getTargetSource().getTarget();
			} catch (Exception e) {
				logger.log(Level.SEVERE,
						"Error on getting persistence handler.", e);
				throw new MonitoringException("003",
						"Error on getting persistence handler.", e);
			}

			if (object instanceof DefaultDatabaseHandler) {
				handler = (DefaultDatabaseHandler<Event>) object;
			}
		} else if (persistenceHandler instanceof DefaultDatabaseHandler) {
			handler = (DefaultDatabaseHandler<Event>) persistenceHandler;
		}
		return handler;
	}
}
