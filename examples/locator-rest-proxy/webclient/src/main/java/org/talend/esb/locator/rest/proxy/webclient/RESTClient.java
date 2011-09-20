package org.talend.esb.locator.rest.proxy.webclient;

/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.apache.cxf.jaxrs.client.ResponseReader;
import org.apache.cxf.jaxrs.client.ServerWebApplicationException;
import org.apache.cxf.jaxrs.client.WebClient;
import org.talend.esb.locator.rest.proxy.service.types.EndpointReferenceListType;
import org.talend.esb.locator.rest.proxy.service.types.EntryType;
import org.talend.esb.locator.rest.proxy.service.types.RegisterEndpointRequestType;

public final class RESTClient {
	private static final String baseAddress = "http://localhost:8040/services/ServiceLocatorRestProxyService/locator/endpoint";
	private static final String lookupAddress = "http://localhost:8040/services/ServiceLocatorRestProxyService/locator/endpoints/";

	private RESTClient() {
	}

	public static void main(String args[]) throws java.lang.Exception {
		// register first endpoint
		WebClient wc = WebClient.create(baseAddress);
		RegisterEndpointRequestType rert = new RegisterEndpointRequestType();
		rert.setEndpointURL("http://services.talend.org/TestEndpoint");
		rert.setServiceName("{http://service.proxy.locator.esb.talend.org}LocatorProxyServiceImpl");
		EntryType et = new EntryType();
		et.setKey("systemTimeout");
		et.getValue().add("200");
		rert.getEntryType().add(et);
		System.out.println("------------------------------------");
		System.out.println("Register endpoint");
		System.out.println("serviceName = " + rert.getServiceName());
		System.out.println("endpointURL = " + rert.getEndpointURL());
		System.out.println("property: " + et.getKey() + "=" + et.getValue());
		try{
		wc.post(rert);
		System.out.println("Endpoint registered successfully");
		}
		catch (WebApplicationException e) {
			System.out.println(e.getMessage());
		}
		String endpoint1URL = new String(baseAddress+"/"+URLEncoder.encode(rert.getServiceName(), "UTF-8")+"/"+URLEncoder.encode(rert.getEndpointURL(),"UTF-8"));
		
		// end register first endpoint

		// register second endpoint
		rert = new RegisterEndpointRequestType();
		rert.setEndpointURL("http://services.talend.org/TestEndpoint2");
		rert.setServiceName("{http://service.proxy.locator.esb.talend.org}LocatorProxyServiceImpl");
		et = new EntryType();
		et.setKey("systemTimeout");
		et.getValue().add("400");
		System.out.println("------------------------------------");
		System.out.println("Register endpoint");
		System.out.println("serviceName = " + rert.getServiceName());
		System.out.println("endpointURL = " + rert.getEndpointURL());
		System.out.println("property: " + et.getKey() + "=" + et.getValue());
		String endpoint2URL = new String(baseAddress+"/"+ URLEncoder.encode(rert.getServiceName(), "UTF-8")+"/"+URLEncoder.encode(rert.getEndpointURL(),"UTF-8"));
		try{
		wc.post(rert);
		System.out.println("Endpoint registered successfully");
		} catch (WebApplicationException e) {
			System.out.println(e.getMessage());
		}
		// end register second endpoint

		// lookup endpoints
		String requestURL = lookupAddress
				+ URLEncoder.encode(rert.getServiceName(), "UTF-8");
		wc = WebClient.create(requestURL);
		wc.accept(MediaType.APPLICATION_XML);
		EndpointReferenceListType erlt;
		System.out.println("------------------------------------");
		System.out.println("Envoking lookupEndpoints for service"
				+ rert.getServiceName());
		try{
		erlt = wc.get(EndpointReferenceListType.class);
		System.out.println("Found " + erlt.getReturn().size() + " endpoints");
		System.out.println(erlt.getReturn().toString());
		}catch (WebApplicationException e) {
			System.out.println(e.getMessage());
		}
		// end lookup endpoints

		// lookup specific endpoint
		/*et = new EntryType();
		et.setKey("systemTimeout");
		et.getValue().add("200");
		String address = baseAddress
		+ "/"
		+ URLEncoder.encode(
				rert.getServiceName(),"UTF-8")+";param=" +URLEncoder.encode(et.getKey() + ","
						+ et.getValue(), "UTF-8");
		wc = WebClient.create(baseAddress
				+ "/"
				+ URLEncoder.encode(
						rert.getServiceName(),"UTF-8")+";param=" +URLEncoder.encode(et.getKey() + ","
								+ et.getValue().get(0), "UTF-8"));
		wc.accept(MediaType.APPLICATION_XML);
		System.out.println("Envoking lookupEndpoint for service"
				+ rert.getServiceName());
		System.out.println("property: " + et.getKey() + "=" + et.getValue());
		resp = wc.get();
		int status4 = resp.getStatus();
		System.out.println("Responce code =" + resp.getStatus());
		if (status4 == 200) {
			System.out.println( wc.get(W3CEndpointReference.class));
		} else
			System.out.println(resp.getEntity());
		// end lookup specific endpoint */

		// delete first endpoint
		System.out.println("------------------------------------");
		System.out.println("Envoking unregisterEndpoint"
				+ URLDecoder.decode(endpoint1URL, "UTF-8"));
		wc=null;
		wc= WebClient.create(endpoint1URL);
		try{
		wc.delete();
		System.out.println("Endpoint unregistered successfully");
		}catch (WebApplicationException e) {
			System.out.println(e.getMessage());
		}
		// end delete first endpoint

		// delete second endpoint
		System.out.println("------------------------------------");
		System.out.println("Envoking unregisterEndpoint"
				+ URLDecoder.decode(endpoint2URL, "UTF-8"));
		wc = WebClient.create(endpoint2URL);
		try{
		wc.delete();
		System.out.println("Endpoint unregistered successfully");
		}catch (WebApplicationException e) {
			System.out.println(e.getMessage());
		}
		// end delete second endpoint

		// lookup Endpoints
		System.out.println("------------------------------------");
		System.out.println("Envoking lookupEndpoints for service"
				+ rert.getServiceName());
		requestURL = lookupAddress
				+ URLEncoder.encode(rert.getServiceName(), "UTF-8");
		wc = WebClient.create(requestURL);
		wc.accept(MediaType.APPLICATION_XML);
		try{
		erlt = wc.get(EndpointReferenceListType.class);
		System.out.println("Found " + erlt.getReturn().size() + " endpoints");
		System.out.println(erlt.getReturn().toString());
		}catch (WebApplicationException e) {
			System.out.println(e.getMessage());
		}
		
	}
}
