package org.talend.esb.locator.rest.proxy.example;

/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.namespace.QName;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.FileRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.client.ResponseReader;
import org.apache.cxf.jaxrs.client.ServerWebApplicationException;
import org.apache.cxf.jaxrs.client.WebClient;
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
		System.out.println("Register service with endpoints");
		RegisterEndpointRequestType registerEndpointRequestType = new RegisterEndpointRequestType();
		registerEndpointRequestType.setEndpointURL("http://services.talend.org/TestEndpoint");
		registerEndpointRequestType.setServiceName("{http://service.proxy.locator.esb.talend.org}LocatorProxyServiceImpl");
		try {
			client.registerEndpoint(registerEndpointRequestType);	
		} catch (ServerWebApplicationException ex) {
			System.err.println(ex.getMessage());
		}
		
	}
	
	private void unregisterExample(LocatorProxyService client) throws UnsupportedEncodingException {
		System.out.println("Unregister endpoints");
		try {
			client.unregisterEndpoint(URLEncoder.encode("{http://service.proxy.locator.esb.talend.org}LocatorProxyServiceImpl", "UTF-8"), URLEncoder.encode("http://services.talend.org/TestEndpoint", "UTF-8"));	
		} catch (ServerWebApplicationException ex) {
			System.err.println(ex.getResponse().getStatus() + ": " + ex.getMessage());
		}
		
	}
	
	private void lookupEndpointsExample(LocatorProxyService client) throws IOException {
		System.out.println("lookupEndpoints");
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
