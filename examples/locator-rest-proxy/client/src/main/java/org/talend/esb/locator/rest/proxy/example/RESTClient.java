package org.talend.esb.locator.rest.proxy.example;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.apache.cxf.jaxrs.client.ServerWebApplicationException;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.talend.esb.locator.rest.proxy.service.LocatorProxyService;
import org.talend.esb.locator.rest.proxy.service.types.EndpointReferenceListType;
import org.talend.esb.locator.rest.proxy.service.types.RegisterEndpointRequestType;

public final class RESTClient {
	
	private RESTClient() throws IOException {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "META-INF/spring/beans.xml" });
		LocatorProxyService client = (LocatorProxyService) context.getBean("restClient");
		try {
			registerExample(client);
			lookupEndpointsExample(client);
			unregisterExample(client);
			lookupEndpointsExample(client);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) throws java.lang.Exception {
		new RESTClient();
	}
	
	private void registerExample(LocatorProxyService client) {
		System.out.println("------------------------------");
		System.out.println("Register service with endpoint");
		System.out.println("ServiceName: {http://service.proxy.locator.esb.talend.org}LocatorProxyServiceImpl");
		System.out.println("EndpointURL:  http://services.talend.org/TestEndpoint");
		RegisterEndpointRequestType registerEndpointRequestType = new RegisterEndpointRequestType();
		registerEndpointRequestType.setEndpointURL("http://services.talend.org/TestEndpoint");
		registerEndpointRequestType.setServiceName("{http://service.proxy.locator.esb.talend.org}LocatorProxyServiceImpl");
		try {
			client.registerEndpoint(registerEndpointRequestType);
			System.out.println("Endpoint registered successfully");
		} catch (ServerWebApplicationException ex) {
			System.err.println(ex.getMessage());
		}
		
	}
	
	private void unregisterExample(LocatorProxyService client) throws UnsupportedEncodingException {
		System.out.println("------------------------------");
		System.out.println("Unregister endpoint");
		System.out.println("ServiceName: {http://service.proxy.locator.esb.talend.org}LocatorProxyServiceImpl");
		System.out.println("EndpointURL:  http://services.talend.org/TestEndpoint");
		try {
			client.unregisterEndpoint(URLEncoder.encode("{http://service.proxy.locator.esb.talend.org}LocatorProxyServiceImpl", "UTF-8"), URLEncoder.encode("http://services.talend.org/TestEndpoint", "UTF-8"));	
		} catch (ServerWebApplicationException ex) {
			System.err.println(ex.getResponse().getStatus() + ": " + ex.getMessage());
		}
		
	}
	
	private void lookupEndpointsExample(LocatorProxyService client) throws IOException {
		System.out.println("------------------------------");
		System.out.println("LookupEndpoints");
		try {
			EndpointReferenceListType r = client.lookupEndpoints(URLEncoder.encode("{http://service.proxy.locator.esb.talend.org}LocatorProxyServiceImpl", "UTF-8"), null);
			if(r.getReturn().size() > 0)
			{
				for (W3CEndpointReference ref : r.getReturn()) {
					System.out.println(ref.toString());
				}	
			}
		} catch(ServerWebApplicationException ex) {
			System.out.println(ex.getMessage());
		}
	}

}
