package org.talend.esb.sam.server.service;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.activation.DataHandler;
import javax.annotation.Resource;

import junit.framework.Assert;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.talend.esb.sam._2011._03.common.CustomInfoType;
import org.talend.esb.sam._2011._03.common.EventEnumType;
import org.talend.esb.sam._2011._03.common.EventType;
import org.talend.esb.sam.common.event.CustomInfo;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.event.EventTypeEnum;
import org.talend.esb.sam.common.event.persistence.EventRepository;
import org.talend.esb.sam.monitoringservice.v1.MonitoringService;
import org.talend.esb.sam.monitoringservice.v1.PutEventsFault;

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
    
//    @Before
//    public void setUp() throws Exception {
//        executeSqlScript("create.sql", true);
//    }
    
    @Test
    public void testSendEvents() throws PutEventsFault, MalformedURLException, URISyntaxException {
        Client client = ClientProxy.getClient(monitoringService);
        HTTPConduit conduit = (HTTPConduit)client.getConduit();
        HTTPClientPolicy clientConfig = new HTTPClientPolicy();
        clientConfig.setReceiveTimeout(100000);
        conduit.setClient(clientConfig);
        
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
    
//    @After
//    public void tearDown() {
//        executeSqlScript("drop.sql", true);
//    }
}
