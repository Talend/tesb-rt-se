package org.talend.esb.examples.ebook.importer;

import java.io.IOException;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class ImporterTest extends CamelTestSupport {
    @Test
    public void testImport() throws IOException {
        System.in.read();
    }
    
    @Override
    protected CamelContext createCamelContext() throws Exception {
        context = (ModelCamelContext)super.createCamelContext();
        JmsComponent jmsComponent = createJmsComponent();
        context.addComponent("jms", jmsComponent);
        return context;
    }

    private JmsComponent createJmsComponent() {
        JmsComponent jmsComponent = new JmsComponent();
        PooledConnectionFactory pooled = createConnectionFactory();
        jmsComponent.setConnectionFactory(pooled);
        return jmsComponent;
    }

    private PooledConnectionFactory createConnectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("localhost:61616");
        RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setMaximumRedeliveries(2);
        connectionFactory.setRedeliveryPolicy(redeliveryPolicy);
        PooledConnectionFactory pooled = new PooledConnectionFactory();
        pooled.setConnectionFactory(connectionFactory);
        return pooled;
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new ImportRoutes();
    }

}
