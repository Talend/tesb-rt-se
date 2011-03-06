package org.sopera.monitoring.server.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sopera.monitoring.event.Event;
import org.sopera.monitoring.event.service.MonitoringService;
import org.sopera.monitoring.server.persistence.EventRowMapper;
import org.sopera.monitoring.util.EventCreator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Tests the sevice implementation together with the database
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/monitoringServiceTest.xml"})
public class MonitoringServiceImplTest extends AbstractTransactionalJUnit4SpringContextTests {
    @Resource
    MonitoringService monitoringSerivce;

    @Before
    public void setUp() throws Exception {
        executeSqlScript("create.sql", false);
    }
    
    @Test
    public void testWritingSeveralEvents() {
        List<Event> events = new ArrayList<Event>();
        events.add(new EventCreator().generateEvent());
        events.add(new EventCreator().generateEvent());
        events.add(new EventCreator().generateEvent());

        monitoringSerivce.putEvents(events);

        for (Event event : events) {
            EventRowMapper rowMapper = new EventRowMapper();
            Event loaded = simpleJdbcTemplate.queryForObject("select * from EVENTS where ID=?", rowMapper, event.getPersistedId());
            Assert.assertNotNull(loaded);
            Assert.assertEquals(event.getPersistedId(), loaded.getPersistedId());
        }

    }

    
    //FIXME test filter
    
    //FIXME test handler
}
