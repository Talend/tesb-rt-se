package org.talend.esb.mep.requestcallback.test;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.talend.esb.mep.requestcallback.beans.JmsUriConfiguration;

import java.util.NavigableMap;

public class JmsUriConfigurationTest {

    private static  final  String uri =
            "jms:queue:callbackRequestQueue.queue?jndiInitialContextFactory=org.apache.activemq.jndi.ActiveMQInitialContextFactory&amp;jndiConnectionFactoryName=ConnectionFactory&amp;jndiURL=tcp://localhost:61616";

    private JmsUriConfiguration conf;

    @Before
    public void before() {
        conf = new JmsUriConfiguration();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJmsUriNegative() {
        conf.setValidate(true);
        Assert.assertTrue(conf.isValidate());
        conf.applyJmsUri("some:gibberish");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJmsUriNegative2() {
        conf.setValidate(true);
        conf.applyJmsUri("queue:callbackRequestQueue.queue?jndiInitialContextFactory=org.apache.activemq.jndi.ActiveMQInitialContextFactory&amp;jndiConnectionFactoryName=ConnectionFactory&amp;jndiURL=tcp://localhost:61616");
    }

    @Test
    public void testSetVariant() {
        conf.setVariant(null);
        Assert.assertEquals(null, conf.getVariant());
        conf.setVariant("JnDi");
        Assert.assertEquals("jndi", conf.getVariant());
        conf.setVariant("jndi-TOPIC");
        Assert.assertEquals("jndi-topic", conf.getVariant());
        conf.setVariant("Queue");
        Assert.assertEquals("queue", conf.getVariant());
        conf.setVariant("Topic");
        Assert.assertEquals("topic", conf.getVariant());

        conf.setValidate(false);
        conf.setVariant("smth_different");
        Assert.assertEquals("smth_different", conf.getVariant());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetVariantNegative() {
        conf.setValidate(true);
        conf.setVariant("Kafka");
    }

    @Test
    public void testSetDestName() {
        conf.setDestinationName("dest_name1");
        Assert.assertEquals("dest_name1", conf.getDestinationName());
    }

    @Test
    public void testSetEncode() {
        conf.setEncode(true);
        Assert.assertEquals(JmsUriConfiguration.UriEncoding.PARTIAL, conf.getUriEncode());
        conf.setUriEncode(JmsUriConfiguration.UriEncoding.FULL);
        Assert.assertEquals(JmsUriConfiguration.UriEncoding.FULL, conf.getUriEncode());
        Assert.assertTrue(conf.isEncode());

        conf.setUriEncode("none");
        Assert.assertEquals(JmsUriConfiguration.UriEncoding.NONE, conf.getUriEncode());

        conf.setUriEncode("partial");
        Assert.assertEquals(JmsUriConfiguration.UriEncoding.PARTIAL, conf.getUriEncode());

        conf.setUriEncode("full");
        Assert.assertEquals(JmsUriConfiguration.UriEncoding.FULL, conf.getUriEncode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetEncodeNegative() {
        conf.setUriEncode("idontwanttoencode");
    }

    @Test
    public void testPutParam() {
        conf.putParameter("ParamNam", "ParamVal");
        Assert.assertEquals("ParamVal", conf.getParameter("ParamNam"));

        NavigableMap<String, String> m = conf.getParameters();
        Assert.assertEquals(1, m.size());
        Assert.assertEquals("ParamVal", m.get("ParamNam"));

        conf.putParameter("jndi-s", "s");
        conf.putParameter("jndi:aa", "bb");
        conf.applyJmsUri(uri);
        m = conf.getJndiEnvironmentParameters();
        Assert.assertEquals(1, m.size());
        Assert.assertEquals("s", m.get("jndi-s"));
    }

    @Test
    public void test() {
        conf.applyJmsUri(null);
        Assert.assertNull(conf.getDestinationName());
        conf.setDestinationName("destName1");
        conf.clear();
        Assert.assertNull(conf.getDestinationName());
    }

    @Test
    public void testToString() {
        conf.applyJmsUri(uri);
       Assert.assertEquals(
               "jms:queue:callbackRequestQueue.queue?amp%3BjndiConnectionFactoryName=ConnectionFactory&amp%3BjndiURL=tcp://localhost:61616&jndiInitialContextFactory=org.apache.activemq.jndi.ActiveMQInitialContextFactory",
               conf.toString()
       );
    }
}
