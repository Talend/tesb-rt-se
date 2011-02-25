package org.sopera.monitoring.handler.impl;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.junit.Before;
import org.junit.Test;
import org.sopera.monitoring.event.Event;
import org.sopera.monitoring.util.EventCreator;

public class DefaultDatabaseHandlerTest {
	private static final Logger logger = Logger
			.getLogger(DefaultDatabaseHandlerTest.class.getName());
	private static final String PERSISTENCE_UNIT_NAME = "testMonitoringServicePersistenceUnit";
	private EntityManagerFactory factory;

	public void testNothing() {
		assert (true);
	}

	/**
	 * Set up at least one event within the database
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);

		EntityManager em = factory.createEntityManager();

		em.getTransaction().begin();

		Query q = em.createQuery("select m from Event m");
		// Event should be empty

		boolean createNewEntries = (q.getResultList().size() == 0);

		if (createNewEntries) {
			assert (q.getResultList().size() == 0);

			Event event = new Event();
			event.setContent("test content " + new Date().toString());
			event.setExtension("test extension " + new Date().toString());

			em.persist(event);
		}

		em.getTransaction().commit();
		em.close();

	}

	/**
	 * Check there is at least one event within the database
	 */
	@Test
	public void checkAvailableEvents() {

		EntityManager em = factory.createEntityManager();

		// Perform a simple query for all the Message entities
		Query q = em.createQuery("select m from Event m");

		@SuppressWarnings("rawtypes")
		List results = q.getResultList();
		assertTrue("Result should contain at least one event. #results="
				+ results.size(), results.size() > 0);

		em.close();
	}

	/**
	 * Test lazy loading is not an issue
	 */
	@Test
	public void testLazyLoading(){
		EntityManager em = factory.createEntityManager();
		
		//Write a new Event
		Event generated = new EventCreator<Event>(Event.class).generateEvent();
		em.getTransaction().begin();
		em.persist(generated);
		em.getTransaction().commit();
		Long id = generated.getPersistedId();
		em.close();
		
		em = factory.createEntityManager(setProperties());
		
		Event loaded = em.find(Event.class, id);
		
		assertEquals("Wrong Event loaded", generated.getMessageInfo().getMessageId(),loaded.getMessageInfo().getMessageId());
		
		logger.info("Before getContent");
		try{
		assertNotNull("Content shouldn't be null", loaded.getContent());
		}catch(RuntimeException e){
			assertTrue("Lazy loading caused exception", false);
			throw e;
		}
		logger.info("After getContent");

		em.close();
	}

	/**
	 * Set up properties for default test database. 
	 * @return
	 */
	private static Map<String, String> setProperties() {

		Map<String, String> map = new HashMap<String, String>();
		map.put(PersistenceUnitProperties.JDBC_DRIVER, "org.apache.derby.jdbc.EmbeddedDriver");
		map.put(PersistenceUnitProperties.JDBC_URL,
				"jdbc:derby:target/db;create=true");
		map.put(PersistenceUnitProperties.JDBC_USER, "test");
		map.put(PersistenceUnitProperties.JDBC_PASSWORD, "test");
		map.put(PersistenceUnitProperties.DDL_GENERATION,
				PersistenceUnitProperties.DROP_AND_CREATE);
		map.put(PersistenceUnitProperties.DDL_GENERATION_MODE,
				PersistenceUnitProperties.DDL_BOTH_GENERATION);
		map.put(PersistenceUnitProperties.TRANSACTION_TYPE, "RESOURCE_LOCAL");
		return map;
	}
}
