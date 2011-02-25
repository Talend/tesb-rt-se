package org.sopera.monitoring.handler;

import java.util.List;

import javax.persistence.EntityManager;

import org.sopera.monitoring.event.Event;

/**
 * Minimum interface for persisting events. If the default handler is not enough
 * it can be replaced with an implementation of this interface. The replacement
 * will be configured in monitoringService.xml
 * 
 * @author cschmuelling
 * 
 */
public interface PersistenceHandler<E extends Event>{

	/**
	 * Start a new transaction<br/>
	 * Within a spring context with JPA/EclipseLink the transaction is startet
	 * on putEvents of MonitoringServiceImpl
	 */
	public void beginTransaction();

	/**
	 * Write event to database
	 * 
	 * @param events
	 */
	public void writeEvents(List<E> events);

	/**
	 * Return EntityManager for writing in database. This method can be used in
	 * custom post handler. If you have set up your own PersistenceHandler (and
	 * your own application configuration in spring) you can throw an
	 * UnsupportedOperationException for this method. This method is not used by
	 * other vendor code.
	 * 
	 * @return
	 * @exception UnsupportedOperationException
	 *                if there is a custom PersistenceHandler with no support
	 *                for EntityManager.
	 */
	public EntityManager getEntityManager()
			throws UnsupportedOperationException;

	/**
	 * Commit the transaction<br/>
	 * Within a spring context with JPA/EclipseLink the transaction is commited
	 * on putEvents of MonitoringServiceImpl
	 */
	public void commitTransaction();
}
