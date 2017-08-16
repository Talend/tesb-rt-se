package org.talend.esb.sam.server;

import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.event.MessageInfo;
import org.talend.esb.sam.common.event.Originator;
import org.talend.esb.sam.server.persistence.DBInitializer;
import org.talend.esb.sam.server.persistence.EventRepositoryImpl;
import org.talend.esb.sam.common.event.EventTypeEnum;

import java.sql.DriverManager;
import java.util.Date;
import java.util.Map;


public class EventRepositoryImplTest {


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
        Event e = new Event();

        Date ts = new Date();
        e.setTimestamp(ts);

        e.setEventType(EventTypeEnum.REQ_IN);

        Originator orig = new Originator("pid1", "127.0.0.1", "localhost", "custom_id1", "principal1");
        e.setOriginator(orig);

        MessageInfo mi = new MessageInfo("mid1", "flo123", "portType_1", "seekBook", "HTTP");
        e.setMessageInfo(mi);

        e.setContentCut(false);
        e.setContent("<seekBook>Survival in the Arctic</seekBook>");

        e.getCustomInfo().put("key1", "value1");
        e.getCustomInfo().put("key2", "value2");

        eri.writeEvent(e);

        Event e2 = eri.readEvent(e.getPersistedId());
        Assert.assertNotNull(e2);

        Assert.assertEquals(e2.getEventType(), e.getEventType());
        Assert.assertEquals(e2.getOriginator(), e.getOriginator());
        Assert.assertEquals(e2.getMessageInfo(), e.getMessageInfo());
        Assert.assertEquals(e2.isContentCut(), e.isContentCut());
        Assert.assertEquals(e2.getContent(), e.getContent());

        Map<String, String> cim = e2.getCustomInfo();
        Assert.assertNotNull(cim.get("key1"));
        Assert.assertEquals("value1", cim.get("key1"));
        Assert.assertEquals("value2", cim.get("key2"));
    }
}
