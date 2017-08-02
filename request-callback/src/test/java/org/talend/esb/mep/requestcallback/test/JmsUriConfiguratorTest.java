package org.talend.esb.mep.requestcallback.test;


import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.DispatchImpl;
import org.apache.cxf.message.Message;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.talend.esb.mep.requestcallback.beans.JmsUriConfiguration;
import org.talend.esb.mep.requestcallback.beans.JmsUriConfigurator;
import org.talend.esb.mep.requestcallback.test.internal.HelloWorldImpl;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;
import java.util.HashMap;
import java.util.Map;

public class JmsUriConfiguratorTest {

    private static final String ADDRESS = "local://JmsUriConfiguratorTest";
    private static final String SERVICE_NS = "http://internal.test.requestcallback.mep.esb.talend.org/";
    private static final QName SERVICE_NAME = new QName(SERVICE_NS, "HelloWorldImplService");
    private static final QName PORT_NAME = new QName(SERVICE_NS, "SoapPort");

    private static final QName qname = new QName("http://test.talend.org", "requestcallback");

    private Endpoint ep = null;


    @After
    public void after() {
        if (ep != null) {
            try {
                ep.stop();
            } catch (Throwable t) {}
            ep = null;
        }
    }


    @Test
    public void  testCreateEndpoint() {
        HelloWorldImpl implementor = new HelloWorldImpl();


        String address = "local://JmsUriConfiguratorTest";
        ep = Endpoint.publish(address, implementor);

        Assert.assertNotNull(ep);
        Assert.assertTrue(ep instanceof EndpointImpl);

        JmsUriConfigurator conf = JmsUriConfigurator.create(ep);
        Assert.assertNotNull(conf);

        JmsUriConfiguration jmsConf = conf.createJmsUriConfiguration();
        Assert.assertNotNull(jmsConf);
        System.out.println("Configurator identifier is: " + conf.getConfiguration().getConfigurationIdentifier());
        Assert.assertNotNull(conf.getConfiguration().getProperty("jndiInitialContextFactory"));
        Assert.assertEquals("org.apache.activemq.jndi.ActiveMQInitialContextFactory",
                                conf.getConfiguration().getProperty("jndiInitialContextFactory"));


        //--------------------------------------------------------------------------------------------------
        conf.setPresetJmsAddress("jms://");
        conf.setVariant(jmsConf.getVariant());
        conf.setDefaultVariant("jndi");
        conf.setDestinationName(jmsConf.getDestinationName());
        conf.setDefaultDestinationName("defaultDestinationName");
        conf.setEndpointName("JmsUriConfiguratorTest");

        Assert.assertEquals("jms://", conf.getPresetJmsAddress());
        Assert.assertEquals("jndi", conf.getVariant());
        Assert.assertEquals("jndi", conf.getDefaultVariant());
        Assert.assertEquals("dynamicQueues/libraryprovider.queue", conf.getDestinationName());
        Assert.assertEquals("defaultDestinationName", conf.getDefaultDestinationName());
        Assert.assertEquals(
                "jms:jndi:dynamicQueues/libraryprovider.queue?jndiConnectionFactoryName=ConnectionFactory&jndiInitialContextFactory=org.apache.activemq.jndi.ActiveMQInitialContextFactory&jndiURL=tcp://localhost:61616",
                conf.getJmsAddress());
        Assert.assertEquals("http://internal.test.requestcallback.mep.esb.talend.org/",
                conf.getServiceName().getNamespaceURI());
        Assert.assertEquals("HelloWorldImplService",
                conf.getServiceName().getLocalPart());

        Assert.assertEquals(new QName(null, "JmsUriConfiguratorTest"), conf.getEndpointName());
        Assert.assertEquals("JmsUriConfiguratorTest", conf.getConfigurationPrefix());

        Map<String, Object> m = new HashMap<String, Object>();
        m.put("key1", "val1");
        conf.setDefaultParameters(m);
        Map m2 = conf.getDefaultParameters();
        Assert.assertNotNull(m2);
        Assert.assertEquals(1, m2.size());
        Assert.assertTrue(m2.containsKey("key1"));
        Assert.assertTrue(m2.containsValue("val1"));

        conf.setParameters(m);
        m2 = conf.getParameters();
        Assert.assertNotNull(m2);
        Assert.assertEquals(1, m2.size());
        Assert.assertTrue(m2.containsKey("key1"));
        Assert.assertTrue(m2.containsValue("val1"));

        String jmsAddr = conf.getJmsAddress();
        Assert.assertEquals(jmsAddr, conf.resetJmsAddress());
        Assert.assertNotNull(conf.getJmsAddress());

        conf.setServiceName("{http://example.com/}AService");
        Assert.assertEquals(new QName("http://example.com/", "AService"), conf.getServiceName());

        conf.setEndpointName(new QName("http://example.com/", "AService"));
        Assert.assertEquals(new QName("http://example.com/", "AService"), conf.getEndpointName());

        conf.setEncodeURI(true);
        Assert.assertTrue(conf.isEncodeURI());

        conf.setEncodeURI("none");
        Assert.assertFalse(conf.isEncodeURI());

        conf.setConfiguration(null);
        Assert.assertNull(conf.getConfiguration());
    }


    @Test
    public void  testCreateFactory() {
        JaxWsServerFactoryBean factory = new JaxWsServerFactoryBean();
        factory.setServiceName(new QName("http://internal.test.requestcallback.mep.esb.talend.org/",
                                         "HelloWorldImplService"));
        factory.setEndpointName(new QName("http://internal.test.requestcallback.mep.esb.talend.org/",
                                            "HelloWorldImplServiceInstance1"));

        JmsUriConfigurator conf = JmsUriConfigurator.create(factory);
        Assert.assertNotNull(conf);
        Assert.assertEquals("HelloWorldImplServiceInstance1", conf.getConfigurationPrefix());
        Assert.assertEquals(new QName("http://internal.test.requestcallback.mep.esb.talend.org/", "HelloWorldImplService"),
                                conf.getServiceName());
    }


    @Test
    public void testCreateDispatch() {
        Service service = Service.create(SERVICE_NAME);
        service.addPort(PORT_NAME, SOAPBinding.SOAP11HTTP_BINDING, ADDRESS);
        Dispatch<Source> dispatch = service.createDispatch(PORT_NAME, Source.class, Service.Mode.PAYLOAD);

        Assert.assertNotNull(dispatch);
        JmsUriConfigurator conf = JmsUriConfigurator.create(dispatch);
        Assert.assertNotNull(conf);
        try {
            System.out.println(conf.createJmsAddress());
        } catch (Throwable t) {
            t.printStackTrace();
        }

        Assert.assertEquals(
                "jms:jndi:dynamicQueues/libraryprovider.queue?jndiConnectionFactoryName=ConnectionFactory&jndiInitialContextFactory=org.apache.activemq.jndi.ActiveMQInitialContextFactory&jndiURL=tcp://localhost:61616",
                conf.getJmsAddress()
        );

        dispatch = conf.configureDispatch(dispatch);
        Assert.assertNotNull(dispatch);
        Client cl = ((DispatchImpl<Source>)dispatch).getClient();
        Assert.assertEquals("jms:jndi:dynamicQueues/libraryprovider.queue?jndiConnectionFactoryName=ConnectionFactory&jndiInitialContextFactory=org.apache.activemq.jndi.ActiveMQInitialContextFactory&jndiURL=tcp://localhost:61616",
                cl.getRequestContext().get(Message.ENDPOINT_ADDRESS));

        Assert.assertEquals("jms:jndi:dynamicQueues/libraryprovider.queue?jndiConnectionFactoryName=ConnectionFactory&jndiInitialContextFactory=org.apache.activemq.jndi.ActiveMQInitialContextFactory&jndiURL=tcp://localhost:61616",
                cl.getEndpoint().getEndpointInfo().getAddress());
    }
}
