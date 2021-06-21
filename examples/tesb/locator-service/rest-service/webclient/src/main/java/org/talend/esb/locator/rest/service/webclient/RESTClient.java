/*
 * #%L
 * Locator REST Service Example :: WebClient
 * %%
 * Copyright (c) 2006-2021 Talend Inc. - www.talend.com
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.talend.esb.locator.rest.service.webclient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.apache.cxf.jaxrs.client.WebClient;
import org.talend.schemas.esb.locator._2011._11.BindingType;
import org.talend.schemas.esb.locator.rest._2011._11.EndpointReferenceList;
import org.talend.schemas.esb.locator.rest._2011._11.EntryType;
import org.talend.schemas.esb.locator.rest._2011._11.RegisterEndpointRequest;
import org.talend.schemas.esb.locator._2011._11.TransportType;

public final class RESTClient {
	private static final String BASE_ADDRESS = "http://localhost:8040/services/ServiceLocatorRestService/locator/endpoint";
	private static final String LOOKUP_ADDRESS = "http://localhost:8040/services/ServiceLocatorRestService/locator/endpoints/";

	private RESTClient() {
		try {
			registerEndpointExample(
					"{http://service.locator.esb.talend.org}LocatorRestServiceImpl",
					"http://services.talend.org/TestEndpoint", "systemTimeout",
					"200");
			registerEndpointExample(
					"{http://service.locator.esb.talend.org}LocatorRestServiceImpl",
					"http://services.talend.org/TestEndpoint1",
					"systemTimeout", "400");
			lookupEndpointsExample("{http://service.locator.esb.talend.org}LocatorRestServiceImpl");
			lookupEndpointExample(
					"{http://service.locator.esb.talend.org}LocatorRestServiceImpl",
					"systemTimeout", "200");
			lookupEndpointExample(
					"{http://service.locator.esb.talend.org}LocatorRestServiceImpl",
					"systemTimeout", "400");
			unregisterEndpointExample(
					"{http://service.locator.esb.talend.org}LocatorRestServiceImpl",
					"http://services.talend.org/TestEndpoint");
			unregisterEndpointExample(
					"{http://service.locator.esb.talend.org}LocatorRestServiceImpl",
					"http://services.talend.org/TestEndpoint1");
			lookupEndpointsExample("{http://service.locator.esb.talend.org}LocatorRestServiceImpl");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}

	public static void main(String args[]) throws java.lang.Exception {
		new RESTClient();
	}

	private void registerEndpointExample(String service, String endpoint,
			String key, String value) {
		System.out.println("------------------------------");
		System.out.println("Register service endpoint");
		System.out.println("ServiceName: ".concat(service));
		System.out.println("EndpointURL: ".concat(endpoint));
		System.out.println("Property: " + key + "=" + value);
		WebClient wc = WebClient.create(BASE_ADDRESS);
		EntryType et = new EntryType();
		et.setKey(key);
		et.getValue().add(value);
		RegisterEndpointRequest registerEndpointRequest = new RegisterEndpointRequest();
		registerEndpointRequest.setEndpointURL(endpoint);
		registerEndpointRequest.setBinding(BindingType.JAXRS);
		registerEndpointRequest.setTransport(TransportType.HTTPS);
		registerEndpointRequest.setServiceName(service);
		registerEndpointRequest.getEntryType().add(et);
		try {
			wc.post(registerEndpointRequest);
			System.out.println("Endpoint registered successfully");
		} catch (WebApplicationException ex) {
			System.err.println(ex.getMessage());
		}

	}

	private void unregisterEndpointExample(String service, String endpoint)
			throws UnsupportedEncodingException {
		System.out.println("------------------------------");
		System.out.println("Unregister endpoint");
		System.out.println("ServiceName: ".concat(service));
		System.out.println("EndpointURL: ".concat(endpoint));
		WebClient wc = WebClient.create(BASE_ADDRESS.concat("/")
				.concat(URLEncoder.encode(service, "UTF-8")).concat("/")
				.concat(URLEncoder.encode(endpoint, "UTF-8")));
		try {
			wc.delete();
		} catch (WebApplicationException ex) {
			System.err.println(ex.getResponse().getStatus() + ": "
					+ ex.getMessage());
		}

	}

	private void lookupEndpointsExample(String service)
			throws UnsupportedEncodingException {
		System.out.println("------------------------------");
		System.out.println("LookupEndpoints for service ".concat(service));
		WebClient wc = WebClient.create(LOOKUP_ADDRESS.concat("/").concat(
				URLEncoder.encode(service, "UTF-8")));
		wc.accept(MediaType.APPLICATION_XML);
		try {
			EndpointReferenceList erlt = wc
					.get(EndpointReferenceList.class);
			System.out.println("Found ".concat(
					String.valueOf(erlt.getEndpointReference().size())).concat(
					" endpoints"));
			if (erlt.getEndpointReference().size() > 0) {
				for (W3CEndpointReference w3cEndpointReference : erlt
						.getEndpointReference()) {
					System.out.println(w3cEndpointReference.toString());
				}
			}
		} catch (WebApplicationException ex) {
			System.out.println(ex.getMessage());
		}
	}

	private void lookupEndpointExample(String service, String key, String value)
			throws UnsupportedEncodingException {
		System.out.println("------------------------------");
		System.out.println("LookupEndpoint for service ".concat(service));
		System.out.println(("Param: ").concat(key).concat("=").concat(value));
		WebClient wc = WebClient.create(BASE_ADDRESS
				.concat("/")
				.concat(URLEncoder.encode(service, "UTF-8"))
				.concat(";param=")
				.concat(URLEncoder.encode(key.concat(",").concat(value),
						"UTF-8")));
		wc.accept(MediaType.APPLICATION_XML);
		try {
			W3CEndpointReference w3cEndpointReference = wc
					.get(W3CEndpointReference.class);
			System.out.println(w3cEndpointReference.toString());
		} catch (WebApplicationException ex) {
			System.out.println(ex.getMessage());
		}
	}

}
