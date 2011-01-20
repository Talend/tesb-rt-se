package org.talend.esb.locator.sample;

import javax.xml.namespace.QName;

public class Constants {

	public static final String HOST = "localhost";
	public static final String PORT = "2181";
	public static final QName SERVICENAME = QName
			.valueOf("{http://talend.org/esb/examples/}GreeterService");
	public static final QName PORTNAME = QName
			.valueOf("{http://talend.org/esb/examples/}GreeterServiceImplPort");
	public static final String LOCATORENDPOINT = HOST + ":" + PORT;
}
