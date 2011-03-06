package org.sopera.monitoring.server.service;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.activation.DataHandler;
import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sopera.monitoring._2010._09.common.EventEnumType;
import org.sopera.monitoring._2010._09.common.EventType;
import org.sopera.monitoring.event.Event;
import org.sopera.monitoring.event.EventTypeEnum;
import org.sopera.monitoring.monitoringservice.v1.MonitoringService;
import org.sopera.monitoring.monitoringservice.v1.PutEventsFault;
import org.sopera.monitoring.server.persistence.EventRowMapper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Tests the monitoring service using webservice calls
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/spring/server.xml", "/fulltest-client.xml"})
public class MonitoringServiceFullTest extends AbstractTransactionalJUnit4SpringContextTests {
    @Resource(name = "monitoringServiceV1Client")
    MonitoringService monitoringService;
    
    @Test
    public void testSendEvents() throws PutEventsFault, MalformedURLException, URISyntaxException {
        simpleJdbcTemplate.update("delete from EVENTS");
        List<EventType> events = new ArrayList<EventType>();
        EventType eventType = new EventType();
        eventType.setEventType(EventEnumType.REQ_OUT);
        URL messageContentFile = this.getClass().getResource("/testmessage.xml").toURI().toURL();
        eventType.setContent(new DataHandler(messageContentFile ));
        events.add(eventType);
        String result = monitoringService.putEvents(events);
        Assert.assertEquals("success", result);
        Event event = simpleJdbcTemplate.queryForObject("select * from EVENTS", new EventRowMapper());
        Assert.assertEquals(EventTypeEnum.REQ_OUT, event.getEventType());
    }
}
