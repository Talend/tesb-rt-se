package org.sopera.monitoring.server.persistence;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.annotation.Resource;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sopera.monitoring.event.Event;
import org.sopera.monitoring.event.EventType;
import org.sopera.monitoring.event.persistence.EventRepository;
import org.sopera.monitoring.server.persistence.EventRowMapper;
import org.sopera.monitoring.util.EventCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration("/EventRepositoryTest-context.xml")
public class EventRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {
    
    @Resource
    private EventRepository eventRepository;

    @Before
    public void setUp() throws Exception {
        executeSqlScript("create.sql", false);
    }

    @Test
    public void testWriteEvent() {
        EventCreator creator = new EventCreator();
        GregorianCalendar cal = new GregorianCalendar(2000, Calendar.JANUARY, 1, 01 , 01, 10);
        Event event = creator.createEvent("content", "extension", cal.getTime(),
                            EventType.REQ_IN, "orig_id", "localhost", "10.0.0.1", "1", "2", "3", "operation",
                            "service", "http");
        Assert.assertNull(event.getPersistedId());
        eventRepository.writeEvent(event);
        Assert.assertNotNull(event.getPersistedId());
        RowMapper<Event> rowMapper = new EventRowMapper();
        Event readEvent = simpleJdbcTemplate.queryForObject("select * from EVENTS", rowMapper);
        Assert.assertTrue(EqualsBuilder.reflectionEquals(event, readEvent));
    }

    @After
    public void tearDown() {
        simpleJdbcTemplate.update("drop table EVENTS");
    }

}
