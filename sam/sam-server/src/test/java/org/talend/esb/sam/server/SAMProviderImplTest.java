package org.talend.esb.sam.server;

import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.event.EventTypeEnum;
import org.talend.esb.sam.common.event.MessageInfo;
import org.talend.esb.sam.common.event.Originator;
import org.talend.esb.sam.server.persistence.*;
import org.talend.esb.sam.server.persistence.criterias.CriteriaAdapter;

import java.sql.DriverManager;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SAMProviderImplTest {

    private static EmbeddedDataSource ds;
    private static EventRepositoryImpl eri;

    @BeforeClass
    public static void init() {
        System.setProperty("derby.stream.error.file", "target/derby.log");
        try {
            ds = new EmbeddedDataSource();
            ds.setDatabaseName("memory:myDB;create=true");
            ds.setCreateDatabase("create");

            DBInitializer dbi = new DBInitializer();
            dbi.setDataSource(ds);
            dbi.setDialect("derbyDialect");
            try {
                dbi.afterPropertiesSet();
            } catch (Throwable t) {
                t.printStackTrace();
                Assert.fail(t.getMessage());
            }

            eri = new EventRepositoryImpl();
            eri.setDataSource(ds);
            eri.setDialect("derbyDialect");
            eri.init();

        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail(t.getMessage());
        }
    }

    @AfterClass
    public static void shutdown() {
        System.clearProperty("derby.stream.error.file");
        try {
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
        } catch (Throwable t) {}
    }

    @Test
    public void test() {

        SAMProviderImpl provider = new SAMProviderImpl();
        provider.setDialect("derbyDialect");
        provider.setDataSource(ds);
        provider.init();


        Event e = writeEventtoDb("SAMProviderImplTest-1");
        Event e2 = writeEventtoDb("SAMProviderImplTest-2");

        Assert.assertEquals("derbyDialect", provider.getDialect());

        List<FlowEvent> flowEvents =  provider.getFlowDetails("SAMProviderImplTest-1");
        Assert.assertNotNull(flowEvents);
        for (FlowEvent fe : flowEvents) {
            if (fe.getFlowID().equals(e.getMessageInfo().getFlowId())) {
                Assert.assertEquals(e.getTimestamp().getTime(), fe.getTimestamp());
                Assert.assertEquals(e.getEventType(), fe.getType());

                Originator o = e.getOriginator();
                Assert.assertEquals(o.getProcessId(), fe.getProcess());
                Assert.assertEquals(o.getIp(), fe.getIp());
                Assert.assertEquals(o.getHostname(), fe.getHost());
                Assert.assertEquals(o.getCustomId(), fe.getCustomId());
                Assert.assertEquals(o.getPrincipal(), fe.getPrincipal());

                MessageInfo mi = e.getMessageInfo();
                Assert.assertEquals(mi.getMessageId(), fe.getMessageID());
                Assert.assertEquals(mi.getFlowId(), fe.getFlowID());
                Assert.assertEquals(mi.getPortType(), fe.getPort());
                Assert.assertEquals(mi.getOperationName(), fe.getOperation());
                Assert.assertEquals(mi.getTransportType(), fe.getTransport());

                Assert.assertEquals(e.isContentCut(), fe.isContentCut());
//                Assert.assertEquals(e.getContent(), fe.getContent());
            } else {
                Assert.fail("record with invalid flowId " + fe.getFlowID() + " was written into database");
            }
        }


        FlowEvent fe = provider.getEventDetails(-33);
        Assert.assertNull(fe);

        int id = Integer.parseInt(e.getPersistedId().toString());
        fe = provider.getEventDetails(id);
        Assert.assertNotNull(fe);

        Assert.assertEquals(e.getTimestamp().getTime(), fe.getTimestamp());
        Assert.assertEquals(e.getEventType(), fe.getType());

        Originator o = e.getOriginator();
        Assert.assertEquals(o.getProcessId(), fe.getProcess());
        Assert.assertEquals(o.getIp(), fe.getIp());
        Assert.assertEquals(o.getHostname(), fe.getHost());
        Assert.assertEquals(o.getCustomId(), fe.getCustomId());
        Assert.assertEquals(o.getPrincipal(), fe.getPrincipal());

        MessageInfo mi = e.getMessageInfo();
        Assert.assertEquals(mi.getMessageId(), fe.getMessageID());
        Assert.assertEquals(mi.getFlowId(), fe.getFlowID());
        Assert.assertEquals(mi.getPortType(), fe.getPort());
        Assert.assertEquals(mi.getOperationName(), fe.getOperation());
        Assert.assertEquals(mi.getTransportType(), fe.getTransport());

        Assert.assertEquals(e.isContentCut(), fe.isContentCut());
        Assert.assertEquals(e.getContent(), fe.getContent());


        Map<String, String[]> criterias = new HashMap<String, String[]>();
        String[] arr = {"SAMProviderImplTest-1"};
        criterias.put("flowID", arr);
        CriteriaAdapter ca = new CriteriaAdapter(0, 100, criterias);

        FlowCollection fc = provider.getFlows(ca);
        Assert.assertNotNull(fc);
        Assert.assertNotNull(fc.getFlows());
        List<Flow> flows =fc.getFlows();
        Assert.assertFalse(flows.isEmpty());
        Assert.assertEquals(1, flows.size());

        Flow flow = flows.get(0);
        Assert.assertEquals(flow.getEventType(), e.getEventType());
        Assert.assertEquals(flow.getflowID(), e.getMessageInfo().getFlowId());
        Assert.assertEquals(flow.getHost(), e.getOriginator().getHostname());
        Assert.assertEquals(flow.getIp(), e.getOriginator().getIp());
        Assert.assertEquals(flow.getOperation(), e.getMessageInfo().getOperationName());
        Assert.assertEquals(flow.getPort(), e.getMessageInfo().getPortType());
        Assert.assertEquals(flow.getTransport(), e.getMessageInfo().getTransportType());
    }


    private Event writeEventtoDb(String flowId) {
        Event e = new Event();

        Date ts = new Date();
        e.setTimestamp(ts);

        e.setEventType(EventTypeEnum.REQ_IN);

        Originator orig = new Originator("pid1", "127.0.0.1", "localhost", "custom_id1", "principal1");
        e.setOriginator(orig);

        MessageInfo mi = new MessageInfo("mid1", flowId, "portType_1", "seekBook", "HTTP");
        e.setMessageInfo(mi);

        e.setContentCut(false);
        e.setContent("<seekBook>Survival in the Arctic</seekBook>");

        e.getCustomInfo().put("key1", "value1");
        e.getCustomInfo().put("key2", "value2");

        eri.writeEvent(e);

        return e;
    }
}
