package org.talend.esb.mep.requestcallback.test;


import org.apache.cxf.jaxws.DispatchImpl;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.transport.Conduit;
import org.apache.cxf.transport.jms.JMSConduit;
import org.apache.cxf.transport.jms.JMSConfiguration;
import org.junit.Assert;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.talend.esb.mep.requestcallback.beans.JmsConfigurator;
import org.talend.esb.mep.requestcallback.feature.Configuration;
import org.talend.esb.mep.requestcallback.test.internal.HelloWorldImpl;
import org.talend.esb.mep.requestcallback.test.internal.HelloWorldImpl2;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;
import java.util.List;

public class JmsConfiguratorTest {

    private static final String ADDRESS = "local://JmsUriConfiguratorTest";
    private static final String SERVICE_NS = "http://internal.test.requestcallback.mep.esb.talend.org/";
    private static final QName SERVICE_NAME = new QName(SERVICE_NS, "HelloWorldImpl2Service");
    private static final QName PORT_NAME = new QName(SERVICE_NS, "SoapPort");

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
    public void testCreateAndConfigureEndpoint() {
        HelloWorldImpl2 implementor = new HelloWorldImpl2();
        String address = "local://JmsUriConfiguratorTest";
        ep = Endpoint.publish(address, implementor);

        JmsConfigurator jmsConfigurator = JmsConfigurator.create(ep);
        Assert.assertNotNull(jmsConfigurator);

        Assert.assertEquals("HelloWorldImpl2Port", jmsConfigurator.getConfigurationPrefix());
        Assert.assertEquals(SERVICE_NAME, jmsConfigurator.getServiceName());


        JMSConfiguration jmsConf = new JMSConfiguration();
        jmsConfigurator.setJmsConfiguration(jmsConf);
        Endpoint ep2 = jmsConfigurator.configureEndpoint(ep);
        Assert.assertNotNull(ep2);

        Configuration cnf = jmsConfigurator.getConfiguration();
        Assert.assertNotNull(cnf);
        Assert.assertEquals("org.apache.activemq.jndi.ActiveMQInitialContextFactory", cnf.get("jndiInitialContextFactory"));
        Assert.assertEquals("jndi", cnf.get("variant"));
        Assert.assertEquals("ConnectionFactory", cnf.get("jndiConnectionFactoryName"));
        Assert.assertEquals("tcp://localhost:61616", cnf.get("jndiURL"));
        Assert.assertEquals("dynamicQueues/libraryprovider.queue", cnf.get("destinationName"));

        List<Feature> features = ((EndpointImpl)ep2).getFeatures();
        boolean jmsConfigFeaturePresent = false;
        for (Feature f : features) {
            if (f instanceof  org.apache.cxf.transport.jms.JMSConfigFeature) {
                jmsConfigFeaturePresent = true;
                break;
            }
        }
        Assert.assertTrue(jmsConfigFeaturePresent);
    }

    @Test
    public void testCreateAndConfigureFactory() {
        JaxWsServerFactoryBean factory = new JaxWsServerFactoryBean();
        factory.setServiceName(SERVICE_NAME);
        factory.setEndpointName(new QName(SERVICE_NS, "HelloWorldImplServiceInstance1"));

        JmsConfigurator jmsConfigurator = JmsConfigurator.create(factory);
        Assert.assertNotNull(jmsConfigurator);
        Assert.assertEquals("HelloWorldImplServiceInstance1", jmsConfigurator.getConfigurationPrefix());
        Assert.assertEquals(SERVICE_NAME, jmsConfigurator.getServiceName());

        JaxWsServerFactoryBean factory2 = jmsConfigurator.configureServerFactory(factory);
        Assert.assertNotNull(factory2);

        List<Feature> features = factory2.getFeatures();
        boolean jmsConfigFeaturePresent = false;
        for (Feature f : features) {
            if (f instanceof  org.apache.cxf.transport.jms.JMSConfigFeature) {
                jmsConfigFeaturePresent = true;
                break;
            }
        }
        Assert.assertTrue(jmsConfigFeaturePresent);
    }

    @Test
    public void createAndConfigureDispatch() {
        Service service = Service.create(SERVICE_NAME);
        service.addPort(PORT_NAME, SOAPBinding.SOAP11HTTP_BINDING, "jms://");
        Dispatch<Source> dispatch = service.createDispatch(PORT_NAME, Source.class, Service.Mode.PAYLOAD);
        Assert.assertNotNull(dispatch);

        JmsConfigurator jmsConfigurator = JmsConfigurator.create(dispatch);
        Assert.assertNotNull(jmsConfigurator);
        Assert.assertEquals("SoapPort", jmsConfigurator.getConfigurationPrefix());
        Assert.assertEquals(SERVICE_NAME, jmsConfigurator.getServiceName());

        Dispatch<Source> dispatch2 =  jmsConfigurator.configureDispatch(dispatch);
        Assert.assertNotNull(dispatch2);

        Conduit conduit = ((DispatchImpl)dispatch2).getClient().getConduit();
        Assert.assertNotNull(conduit);
        Assert.assertTrue(conduit instanceof JMSConduit);

        JMSConduit jmsConduit = (JMSConduit)conduit;
        JMSConfiguration jmsConfiguration = jmsConduit.getJmsConfig();
        Assert.assertNotNull(jmsConfiguration);
    }

    @Test
    public void createAndConfigureDispatchAddressing() {
        Service service = Service.create(SERVICE_NAME);
        service.addPort(PORT_NAME, SOAPBinding.SOAP11HTTP_BINDING, "jms://");
        Dispatch<Source> dispatch = service.createDispatch(PORT_NAME, Source.class, Service.Mode.PAYLOAD);
        Assert.assertNotNull(dispatch);

        JmsConfigurator jmsConfigurator = JmsConfigurator.create(dispatch);
        Assert.assertNotNull(jmsConfigurator);
        Assert.assertEquals("SoapPort", jmsConfigurator.getConfigurationPrefix());
        Assert.assertEquals(SERVICE_NAME, jmsConfigurator.getServiceName());

        Dispatch<Source> dispatch2 =  jmsConfigurator.configureDispatch(dispatch, "jms://");
        Assert.assertNotNull(dispatch2);

        Conduit conduit = ((DispatchImpl)dispatch2).getClient().getConduit();
        Assert.assertNotNull(conduit);
        Assert.assertTrue(conduit instanceof JMSConduit);

        JMSConduit jmsConduit = (JMSConduit)conduit;
        JMSConfiguration jmsConfiguration = jmsConduit.getJmsConfig();
        Assert.assertNotNull(jmsConfiguration);
    }
}
