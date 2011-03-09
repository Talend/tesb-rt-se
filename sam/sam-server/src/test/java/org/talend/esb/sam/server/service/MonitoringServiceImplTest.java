package org.talend.esb.sam.server.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.service.MonitoringService;
import org.talend.esb.sam.common.util.EventCreator;
import org.talend.esb.sam.server.persistence.EventRowMapper;

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
        executeSqlScript("create.sql", true);
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
