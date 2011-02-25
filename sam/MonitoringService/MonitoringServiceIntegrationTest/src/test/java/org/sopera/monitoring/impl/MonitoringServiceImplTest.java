package org.sopera.monitoring.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sopera.monitoring.event.Event;
import org.sopera.monitoring.exception.MonitoringException;
import org.sopera.monitoring.service.MonitoringService;
import org.sopera.monitoring.util.EventCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/monitoringServiceTest.xml" })
public class MonitoringServiceImplTest implements CgLibWorkaround {

	@Autowired
	private ApplicationContext applicationContext;

	@PersistenceContext
	private EntityManager em;

	public void setEm(EntityManager em) {
		this.em = em;
	}

	@Transactional(value = "defaultTransaction", propagation = Propagation.REQUIRES_NEW)
	public void clearEventTable() {
		Query query = em.createQuery("select m from Event m");

		@SuppressWarnings("unchecked")
		List<Event> list = query.getResultList();
		for (Event event : list) {
			em.remove(event);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	@Transactional(value = "defaultTransaction", propagation = Propagation.REQUIRES_NEW)
	public void integrationTest() {
		MonitoringService monitoringSerivce = (MonitoringService) applicationContext
				.getBean("monitoringService");

		List<Event> events = new ArrayList<Event>();
		events.add(new EventCreator<Event>(Event.class).generateEvent());
		events.add(new EventCreator<Event>(Event.class).generateEvent());
		events.add(new EventCreator<Event>(Event.class).generateEvent());

		try {
			monitoringSerivce.putEvents(events);
		} catch (MonitoringException e) {
			e.printStackTrace();
			throw e;
		}
		// In other cases there will be no database exception
		em.flush();

		for (Event event : events) {
			Event loaded = em.find(Event.class, event.getPersistedId());
			Assert.assertNotNull(loaded);
			Assert.assertEquals(event.getPersistedId(), loaded.getPersistedId());
			em.remove(loaded);
		}

		// Srping / Junit will rollback the transaction
		em.flush();
	}

	@Test
	@Transactional(value = "defaultTransaction", propagation = Propagation.REQUIRES_NEW)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void customEventTest() {
		MonitoringService monitoringSerivce = (MonitoringService) applicationContext
				.getBean("monitoringService");

		Class clazz = ch.zurich.monitoring.event.ZurichEvent.class;

		List<ch.zurich.monitoring.event.ZurichEvent> events = new ArrayList<ch.zurich.monitoring.event.ZurichEvent>();
		events.add(new EventCreator<ch.zurich.monitoring.event.ZurichEvent>(
				clazz).generateEvent());
		events.add(new EventCreator<ch.zurich.monitoring.event.ZurichEvent>(
				clazz).generateEvent());
		events.add(new EventCreator<ch.zurich.monitoring.event.ZurichEvent>(
				clazz).generateEvent());

		monitoringSerivce.putEvents(events);

		// In other cases there will be no database exception
		em.flush();

		for (Event event : events) {
			Object loaded = em.find(clazz, event.getPersistedId());
			Assert.assertNotNull(loaded);

			if (loaded instanceof Event) {
				Assert.assertEquals(event.getPersistedId(),
						((Event) loaded).getPersistedId());
			}
			em.remove(loaded);
		}

		// Srping / Junit will rollback the transaction
		em.flush();
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
}
