package org.talend.esb.sam.common.event;

import org.junit.Assert;
import org.junit.Test;

public class OriginatorTest {


    @Test
    public void testOriginator() {
        Originator originator = new Originator("process_id", "127.0.0.1", "localhost", "custom_id", "principal");

        originator.setProcessId("new_process");
        Assert.assertEquals("new_process", originator.getProcessId());
        originator.setProcessId("process_id");

        originator.setIp("192.168.1.1");
        Assert.assertEquals("192.168.1.1", originator.getIp());
        originator.setIp("127.0.0.1");

        originator.setHostname("remotehost");
        Assert.assertEquals("remotehost", originator.getHostname());
        originator.setHostname("localhost");

        originator.setCustomId("iddddd");
        Assert.assertEquals("iddddd", originator.getCustomId());
        originator.setCustomId("custom_id");

        originator.setPrincipal("another_principal");
        Assert.assertEquals("another_principal", originator.getPrincipal());
        originator.setPrincipal("principal");

        Originator originator2 = new Originator("process_id", "127.0.0.1", "localhost", "custom_id", "principal");
        Assert.assertEquals(originator, originator2);

        Assert.assertNotEquals(originator, null);
        Assert.assertNotEquals(originator, "yet another string");

        originator2.setProcessId(null);
        Assert.assertNotEquals(originator, originator2);
        originator2.setProcessId("pid");
        Assert.assertNotEquals(originator, originator2);
        originator2.setProcessId("process_id");

        originator2.setIp(null);
        Assert.assertNotEquals(originator, originator2);
        originator2.setIp("1.2.3.4");
        Assert.assertNotEquals(originator, originator2);
        originator2.setIp("127.0.0.1");

        originator2.setHostname(null);
        Assert.assertNotEquals(originator, originator2);
        originator2.setHostname("remotehost");
        Assert.assertNotEquals(originator, originator2);
        originator2.setHostname("localhost");

        originator2.setCustomId(null);
        Assert.assertNotEquals(originator, originator2);
        originator2.setCustomId("customs");
        Assert.assertNotEquals(originator, originator2);
        originator2.setCustomId("custom_id");

        originator2.setPrincipal(null);
        Assert.assertNotEquals(originator, originator2);
        originator2.setPrincipal("no principal");
        Assert.assertNotEquals(originator, originator2);
        originator2.setPrincipal("principal");

    }

}
