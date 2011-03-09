package org.sopera.monitoring.server.service;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.activation.DataHandler;
import javax.annotation.Resource;

import junit.framework.Assert;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sopera.monitoring._2010._09.common.CustomInfoType;
import org.sopera.monitoring._2010._09.common.EventEnumType;
import org.sopera.monitoring._2010._09.common.EventType;
import org.sopera.monitoring.event.CustomInfo;
import org.sopera.monitoring.event.Event;
import org.sopera.monitoring.event.EventTypeEnum;
import org.sopera.monitoring.event.persistence.EventRepository;
import org.sopera.monitoring.monitoringservice.v1.MonitoringService;
import org.sopera.monitoring.monitoringservice.v1.PutEventsFault;
import org.sopera.monitoring.server.persistence.EventRowMapper;
import org.springframework.jdbc.core.RowMapper;
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
    
    @Resource
    private EventRepository eventRepository;
    
    @Test
    public void testSendEvents() throws PutEventsFault, MalformedURLException, URISyntaxException {
        simpleJdbcTemplate.update("delete from EVENTS");
        
        List<EventType> events = new ArrayList<EventType>();
        EventType eventType = new EventType();
        eventType.setEventType(EventEnumType.REQ_OUT);
        URL messageContentFile = this.getClass().getResource("/testmessage.xml").toURI().toURL();
        eventType.setContent(new DataHandler(messageContentFile ));
        
        CustomInfoType ciType = new CustomInfoType();
        CustomInfoType.Item prop1 = new CustomInfoType.Item();
        prop1.setKey("mykey1");
        prop1.setValue("myValue1");
        ciType.getItem().add(prop1);
        CustomInfoType.Item prop2 = new CustomInfoType.Item();
        prop2.setKey("mykey2");
        prop2.setValue("myValue2");
        ciType.getItem().add(prop2);
        eventType.setCustomInfo(ciType);
        
        events.add(eventType);
        String result = monitoringService.putEvents(events);
        Assert.assertEquals("success", result);
        

        long id = simpleJdbcTemplate.queryForLong("select id from EVENTS");
        Event readEvent = eventRepository.readEvent(id);
        Assert.assertEquals(EventTypeEnum.REQ_OUT, readEvent.getEventType());
        List<CustomInfo> ciList = readEvent.getCustomInfoList();
        Assert.assertEquals("mykey1", ciList.get(0).getCustKey());
        Assert.assertEquals("myValue1", ciList.get(0).getCustValue());
        Assert.assertEquals("mykey2", ciList.get(1).getCustKey());
        Assert.assertEquals("myValue2", ciList.get(1).getCustValue());

    }
}
