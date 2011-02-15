package org.talend.esb.locator.sample;

import javax.xml.namespace.QName;

public class Constants {

	public static final String ServiceHOST = "localhost";
	public static final String HOST = "192.168.40.14";
	public static final String PORT = "2181";
	public static final QName SERVICENAME = QName
	.valueOf("{http://cxf.sample.esb.talend.org/}GreeterImplService");
	public static final QName SERVICENAME1 = QName
	.valueOf("{http://cxf.sample.esb.talend.org/}GreeterImplService");
	public static final QName PORTNAME = QName
			.valueOf("{http://talend.org/esb/examples/}GreeterPortImpl");
	public static final String LOCATORENDPOINT = "sop-td57:2181";//HOST + ":" + PORT;
	public static final String JMS_ENDPOINT_URI = "jms:queue:test.cxf.jmstransport.queue?timeToLive=1000"
        + "&jndiConnectionFactoryName=ConnectionFactory"
        + "&jndiURL=tcp://mom:61616"
        + "&jndiInitialContextFactory=org.apache.activemq.jndi.ActiveMQInitialContextFactory";
	public static final String JMS_ENDPOINT_URI_ALT = "jms:queue:testAlt.cxf.jmstransport.queue?timeToLive=1000"
        + "&jndiConnectionFactoryName=ConnectionFactory"
        + "&jndiURL=tcp://mom:61616"
        + "&jndiInitialContextFactory=org.apache.activemq.jndi.ActiveMQInitialContextFactory";
	
	public static final String GREETERNAME = "/services/Greeter";
	
}
