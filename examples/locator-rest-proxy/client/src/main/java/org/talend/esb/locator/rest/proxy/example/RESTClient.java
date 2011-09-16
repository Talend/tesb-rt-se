package org.talend.esb.locator.rest.proxy.example;

/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
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
import org.apache.cxf.jaxrs.client.WebClient;
import org.talend.esb.locator.rest.proxy.service.LocatorProxyService;
import org.talend.esb.locator.rest.proxy.service.types.RegisterEndpointRequestType;

public final class RESTClient {
	private static final String baseAddress = "http://localhost:8040/services/ServiceLocatorRestProxyService/locator/endpoint";

	private RESTClient() {
	}

	public static void main(String args[]) throws java.lang.Exception {
		WebClient wc = WebClient.create(baseAddress);
		RegisterEndpointRequestType rert = new RegisterEndpointRequestType();
		rert.setEndpointURL("http://MyEndpoint.org");
		rert.setServiceName("{http://MyService.org}MyService");
		Response resp = wc.post(rert);
		int status = resp.getStatus();
		System.out
				.println("Using a simple JAX-RS proxy to get all the persons...");
		// String baseAddress =
		// "http://localhost:8040/services/ServiceLocatorRestProxyService/locator/";
		/*LocatorProxyService lps = JAXRSClientFactory.create(baseAddress,
				LocatorProxyService.class);
		RegisterEndpointRequestType rert = new RegisterEndpointRequestType();
		rert.setEndpointURL("http://MyEndpoint.org");
		rert.setServiceName("{http://MyService.org}MyService");
		System.out.println("Invoking registerEndpoint method: serviceName="
				+ rert.getServiceName() + "  enpointURL="
				+ rert.getEndpointURL());
		Response response = lps.registerEndpoint(rert);
		System.out.println("Response status code: " + response.getStatus());
		if (response.getStatus() == 200) {
			System.out.println("Location ="
					+ response.getMetadata().getFirst("Location").toString());
		}*/
//		Response response;
//		System.out
//				.println("Invoking registerEndpoint method: registerEndpoint1.xml");
//		RESTClient client = new RESTClient();
//		String input =client.getClass().getResource("registerEndpoint1.xml").getFile();
//		response = wc.post(client);
//		int k=0;
	}

}
