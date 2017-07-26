package org.talend.esb.sam.common.event;


import org.junit.Assert;
import org.junit.Test;


public class MessageInfoTest {


    @Test
    public void testMessageInfo() {
        MessageInfo mi = new MessageInfo("msg123", "flow456", "port_type", "operation1", "http");

        Assert.assertEquals("msg123", mi.getMessageId());
        Assert.assertEquals("flow456", mi.getFlowId());
        Assert.assertEquals("port_type", mi.getPortType());
        Assert.assertEquals("operation1", mi.getOperationName());
        Assert.assertEquals("http", mi.getTransportType());

        mi.setMessageId("msg2");
        mi.setFlowId("flowNew");
        mi.setPortType("port_type2");
        mi.setOperationName("operationNew");
        mi.setTransportType("jms");

        Assert.assertEquals("msg2", mi.getMessageId());
        Assert.assertEquals("flowNew", mi.getFlowId());
        Assert.assertEquals("port_type2", mi.getPortType());
        Assert.assertEquals("operationNew", mi.getOperationName());
        Assert.assertEquals("jms", mi.getTransportType());

        Assert.assertEquals(mi, mi);
        Assert.assertNotEquals(mi, null);
        Assert.assertNotEquals(mi, "this is something rather different then MessageInfo");

        MessageInfo mi2 = new MessageInfo("msg2", "flowNew", "port_type2", "operationNew", "jms");
        Assert.assertTrue(mi.equals(mi2));
        Assert.assertEquals(mi.toString(), mi2.toString());


        mi2.setMessageId(null);
        Assert.assertNotEquals(mi2, mi);
        mi2.setMessageId("msg3333");
        Assert.assertNotEquals(mi2, mi);
        mi2.setMessageId("msg2");

        mi2.setFlowId(null);
        Assert.assertNotEquals(mi2, mi);
        mi2.setFlowId("flow4");
        Assert.assertNotEquals(mi2, mi);
        mi2.setFlowId("flowNew");

        mi2.setPortType(null);
        Assert.assertNotEquals(mi2, mi);
        mi2.setPortType("porttype2");
        Assert.assertNotEquals(mi2, mi);
        mi2.setPortType("port_type2");

        mi2.setOperationName(null);
        Assert.assertNotEquals(mi2, mi);
        mi2.setOperationName("request-notification");
        Assert.assertNotEquals(mi2, mi);
        mi2.setOperationName("operationNew");

        mi2.setTransportType(null);
        Assert.assertNotEquals(mi2, mi);
        mi2.setTransportType("IMAP");
        Assert.assertNotEquals(mi2, mi);
    }



}
