package org.sopera.monitoring.handler.impl;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.sopera.monitoring.event.Event;
import org.sopera.monitoring.exception.MonitoringException;
import org.sopera.monitoring.handler.PersistenceHandler;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class DefaultDatabaseHandler<E extends Event> extends
		AbstractFilteredHandler<E> implements PersistenceHandler<E> {
	private static Logger logger = Logger
			.getLogger(DefaultDatabaseHandler.class.getName());

	// Holds a shared thread safe EntityManager, injected by spring.
	@PersistenceContext(unitName = "monitoringServicePersistenceUnit")
	private EntityManager em;

	@SuppressWarnings("unused")
	private void setEm(EntityManager em) {
		this.em = em;
	}

	public void beginTransaction() {
		logger.fine("Starting transaction will be ignored. Spring controles the transaction.");
	}

	@Transactional(value = "defaultTransaction", propagation = Propagation.REQUIRED)
	public void writeEvents(List<E> events) {
		logger.info("Writing events");
		Event current = null;
		try {
			for (Event event : events) {
				current = event;
				em.persist(event);
			}
		} catch (RuntimeException e) {
			logger.log(Level.SEVERE,
					"Rollback transaction because of exception: ", e);
			throw new MonitoringException("100",
					"Could not store event in database. Database error", e,
					current);
		}
	}

	public void commitTransaction() {
		logger.fine("Commit transaction will be ignored. Spring controles the transaction.");
	}

	public EntityManager getEntityManager() {
		return em;
	}

}
