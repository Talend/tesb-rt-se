package org.talend.esb.sam.service.rest;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.ContentHandler;
import org.json.simple.parser.JSONParser;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.event.EventTypeEnum;
import org.talend.esb.sam.common.event.MessageInfo;
import org.talend.esb.sam.common.event.Originator;
import org.talend.esb.sam.common.event.persistence.EventRepository;
import org.talend.esb.sam.server.persistence.FlowEvent;
import org.talend.esb.sam.server.persistence.SAMProvider;
import org.talend.esb.sam.service.rest.SAMRestService;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.Response;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/test-context.xml" })
public class SamRestServiceImplTest {

    @Autowired
    private SAMProvider samProvider;

    @Autowired
    private EventRepository eventRepository;


    private static SAMRestService restService;


    private static final String CHECK_ALIVE_RESPONSE =
            "Talend Service Activity Monitoring Server :: REST API - local://sam-rest/list";


    @PostConstruct
    public static void init() {
        List l = new ArrayList();
        l.add(new org.apache.cxf.jaxrs.provider.jsrjsonp.JsrJsonpProvider());
        restService = JAXRSClientFactory.create("local://sam-rest", SAMRestService.class, l);
    }


    @AfterClass
    public static void shutdown() {
        System.clearProperty("derby.stream.error.file");
        try {
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
        } catch (Throwable t) {}
    }


    @Test
    public void testCheckAlive() {
        Response response = restService.checkAlive();
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(CHECK_ALIVE_RESPONSE, response.readEntity(String.class));
    }


    @Test
    public void  test() {
        Event e = writeEventtoDb("flow123", EventTypeEnum.REQ_IN);
        List<FlowEvent> flowEvents =  samProvider.getFlowDetails("flow123");
        Assert.assertNotNull(flowEvents);
        Assert.assertTrue(!flowEvents.isEmpty());


        Response response = restService.getFlow("flow123");
        String responseString = response.readEntity(String.class);
        System.out.println(responseString);

        JSONParser parser = new JSONParser();
        JSONObject obj = null;

        try {
            obj = (JSONObject)parser.parse(responseString);
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail(ex.getMessage());
        }


        Assert.assertNotNull(obj);

        JSONObject events = (JSONObject)obj.get("events");
        Assert.assertNotNull(events);
        Assert.assertFalse((Boolean) (events.get("contentCut")));
        Assert.assertEquals("custom_id1", events.get("customId"));
        Assert.assertTrue(((String) events.get("details")).startsWith("local://sam-rest/event/"));

        Assert.assertEquals("flow123", events.get("flowID"));
        Assert.assertEquals("localhost", events.get("host"));
        Assert.assertEquals(new Long(2), events.get("id"));
        Assert.assertEquals("127.0.0.1", events.get("ip"));
        Assert.assertEquals("mid1", events.get("messageID"));
        Assert.assertEquals("seekBook", events.get("operation"));
        Assert.assertEquals("portType_1", events.get("port"));
        Assert.assertEquals("principal1", events.get("principal"));
        Assert.assertEquals("pid1", events.get("process"));
        Assert.assertEquals("HTTP", events.get("transport"));
        Assert.assertEquals("REQ_IN", events.get("type"));


        // Test not existing flow
        response =restService.getFlow("notExistingFlow");
        Assert.assertEquals(404, response.getStatus());

        // Test getEvent
        response = restService.getEvent("notANumber");
        Assert.assertEquals(400, response.getStatus());


        response = restService.getEvent("-1");
        Assert.assertEquals(404, response.getStatus());



        response = restService.getEvent(Long.toString(e.getPersistedId()));
        Assert.assertEquals(200, response.getStatus());

        try {
            obj = (JSONObject)parser.parse(response.readEntity(String.class));
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail(ex.getMessage());
        }

        Assert.assertEquals("<seekBook>Survival in the Arctic</seekBook>", obj.get("content"));
        Assert.assertFalse((Boolean)obj.get("contentCut"));
        Assert.assertEquals("custom_id1", obj.get("customId"));
        Assert.assertEquals("flow123", obj.get("flowID"));
        Assert.assertEquals("localhost", obj.get("host"));
        Assert.assertEquals(new Long(2), obj.get("id"));
        Assert.assertEquals("127.0.0.1", obj.get("ip"));
        Assert.assertEquals("mid1", obj.get("messageID"));
        Assert.assertEquals("seekBook", obj.get("operation"));
        Assert.assertEquals("portType_1", obj.get("port"));
        Assert.assertEquals("principal1", obj.get("principal"));
        Assert.assertEquals("pid1", obj.get("process"));
        Assert.assertEquals("HTTP", obj.get("transport"));
        Assert.assertEquals("REQ_IN", obj.get("type"));


        //---------------------------------------------------

        Event e2 = writeEventtoDb("flow123", EventTypeEnum.REQ_OUT);

        response = restService.getFlows(0, 10);
        try {
            obj = (JSONObject)parser.parse(response.readEntity(String.class));
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail(ex.getMessage());
        }

        Assert.assertEquals(new Long(1), obj.get("count"));

        obj = (JSONObject)obj.get("aggregated");
        Assert.assertNotNull(obj);
        Assert.assertEquals("localhost", obj.get("consumerHost"));

        Assert.assertEquals("127.0.0.1", obj.get("consumerIP"));
        Assert.assertEquals("local://sam-rest/flow/flow123", obj.get("details"));
        Assert.assertEquals("flow123", obj.get("flowID"));
        Assert.assertEquals("seekBook", obj.get("operation"));
        Assert.assertEquals("portType_1", obj.get("port"));
        Assert.assertEquals("localhost", obj.get("providerHost"));
        Assert.assertEquals("127.0.0.1", obj.get("providerIP"));
        Assert.assertEquals("HTTP", obj.get("transport"));
    }




    private Event writeEventtoDb(String flowId, EventTypeEnum eventType) {
        Event e = new Event();

        Date ts = new Date();
        e.setTimestamp(ts);

        e.setEventType(eventType);

        Originator orig = new Originator("pid1", "127.0.0.1", "localhost", "custom_id1", "principal1");
        e.setOriginator(orig);

        MessageInfo mi = new MessageInfo("mid1", flowId, "portType_1", "seekBook", "HTTP");
        e.setMessageInfo(mi);

        e.setContentCut(false);
        e.setContent("<seekBook>Survival in the Arctic</seekBook>");

        e.getCustomInfo().put("key1", "value1");
        e.getCustomInfo().put("key2", "value2");

        eventRepository.writeEvent(e);

        return e;
    }


    public static class SystemPropertyInitializer {
        public SystemPropertyInitializer() {
            System.setProperty("derby.stream.error.file", "target/derby.log");
        }
    }


}
