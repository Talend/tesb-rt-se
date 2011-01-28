package org.talend.esb.locator.sample;

import javax.xml.namespace.QName;

public class Constants {

	public static final String ServiceHOST = "localhost";
	public static final String HOST = "localhost";
	public static final String PORT = "2181";
	public static final QName SERVICENAME = QName
	.valueOf("{http://talend.org/esb/examples/}GreeterService");
	public static final QName PORTNAME = QName
			.valueOf("{http://talend.org/esb/examples/}GreeterPortImpl");
	public static final String LOCATORENDPOINT = "localhost:2181";//HOST + ":" + PORT;
	public static final String JMS_ENDPOINT_URI = "jms:queue:test.cxf.jmstransport.queue?timeToLive=1000"
        + "&jndiConnectionFactoryName=ConnectionFactory"
        + "&jndiURL=tcp://localhost:61616"
        + "&jndiInitialContextFactory=org.apache.activemq.jndi.ActiveMQInitialContextFactory";
	public static final String GREETERNAME = "/services/Greeter";
	
}
