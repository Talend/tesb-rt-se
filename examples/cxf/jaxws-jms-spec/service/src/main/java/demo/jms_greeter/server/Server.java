/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package demo.jms_greeter.server;

import javax.xml.ws.Endpoint;

public class Server {

    protected Server() throws Exception {
        System.out.println("Starting Server");
        Object implementor = new GreeterJMSImpl();
        String address = "jms:jndi:dynamicQueues/test.cxf.jmstransport.queue?jndiInitialContextFactory="
            + "org.apache.activemq.jndi.ActiveMQInitialContextFactory&jndiConnectionFactoryName="
            + "ConnectionFactory&jndiURL=tcp://localhost:61616";
        Endpoint.publish(address, implementor);
    }

    public static void main(String args[]) throws Exception {
        new Server();
        System.out.println("Server ready...");

        Thread.sleep(125 * 60 * 1000);
        System.out.println("Server exiting");
        System.exit(0);
    }
}
