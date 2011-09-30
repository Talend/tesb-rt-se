package org.talend.esb.locator.proxy.client;

/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.talend.schemas.esb._2011._09.locator.EndpointReferenceListType;
import org.talend.schemas.esb._2011._09.locator.LookupRequestType;
import org.talend.schemas.esb._2011._09.locator.RegisterEndpointRequestType;
import org.talend.schemas.esb._2011._09.locator.UnregisterEndpointRequestType;
import org.talend.webservices.esb.locator_v1.InterruptedExceptionFault;
import org.talend.webservices.esb.locator_v1.LocatorServiceV10;
import org.talend.webservices.esb.locator_v1.LocatorServiceV10_Service;
import org.talend.webservices.esb.locator_v1.ServiceLocatorFault;

public final class LocatorProxyService_Localhost_Client {

	private static final QName SERVICE_NAME = new QName(
			"http://webservices.talend.org/esb/locator_v1",
			"LocatorService_v1_0");

	private LocatorProxyService_Localhost_Client() {
	}

	public static void main(String args[]) throws Exception {
		URL wsdlURL = LocatorServiceV10_Service.WSDL_LOCATION;
		if (args.length > 0 && args[0] != null && !"".equals(args[0])) {
			File wsdlFile = new File(args[0]);
			if (wsdlFile.exists()) {
				wsdlURL = wsdlFile.toURI().toURL();
			} else {
				wsdlURL = new URL(args[0]);
			}
		}

		LocatorServiceV10_Service locatorService = new LocatorServiceV10_Service(wsdlURL, SERVICE_NAME);
		LocatorServiceV10 port = locatorService.getLocatorServiceSoap();

		registerEndpoint(port);
		lookupEndpoint(port);
		lookupEndpoints(port);
		unregisterEndpoint(port);

		System.exit(0);
	}

	private static void unregisterEndpoint(LocatorServiceV10 port)
			throws InterruptedExceptionFault, ServiceLocatorFault {
		System.out.println("Invoking unregisterEnpoint...");
		QName serviceName = new QName("http://services.talend.org/TestService", "TestServiceProvider");
		String endpointURL = "http://Service";
		UnregisterEndpointRequestType unregisterEndpointRequestType = new UnregisterEndpointRequestType();
		unregisterEndpointRequestType.setServiceName(serviceName);
		unregisterEndpointRequestType.setEndpointURL(endpointURL);
		port.unregisterEnpoint(unregisterEndpointRequestType);
	}

	private static void lookupEndpoints(LocatorServiceV10 port)
			throws InterruptedExceptionFault, ServiceLocatorFault {
		System.out.println("Invoking lookupEndpoints...");
		QName serviceName = new QName("http://services.talend.org/TestService", "TestServiceProvider");
		LookupRequestType lookupRequestType = new LookupRequestType();
		lookupRequestType.setServiceName(serviceName);
		EndpointReferenceListType endpoints = port.lookupEndpoints(lookupRequestType);
		List<W3CEndpointReference> endpointsList = endpoints.getReturn();
		System.out.println("lookupEndpoints.result=" + endpointsList);
		for (int i = 0; i < endpointsList.size(); i++) {
			W3CEndpointReference endpoint = endpointsList.get(i);
			System.out.println(" #" + i + " lookupEndpoint.result="	+ endpoint);
		}
	}

	private static void lookupEndpoint(LocatorServiceV10 port)
			throws InterruptedExceptionFault, ServiceLocatorFault {
		System.out.println("Invoking lookupEndpoint...");
		QName serviceName = new QName("http://services.talend.org/TestService", "TestServiceProvider");
		LookupRequestType lookupRequestType = new LookupRequestType();
		lookupRequestType.setServiceName(serviceName);
		W3CEndpointReference endpoint = port.lookupEndpoint(lookupRequestType);
		System.out.println("lookupEndpoint.result="	+ endpoint);
	}

	private static void registerEndpoint(LocatorServiceV10 port)
			throws InterruptedExceptionFault, ServiceLocatorFault {
		System.out.println("Invoking registerEndpoint...");
		QName serviceName = new QName("http://services.talend.org/TestService", "TestServiceProvider");
		String endpointURL = "http://Service";
		RegisterEndpointRequestType input = new RegisterEndpointRequestType();
		input.setServiceName(serviceName);
		input.setEndpointURL(endpointURL);
		port.registerEndpoint(input);
	}

}
