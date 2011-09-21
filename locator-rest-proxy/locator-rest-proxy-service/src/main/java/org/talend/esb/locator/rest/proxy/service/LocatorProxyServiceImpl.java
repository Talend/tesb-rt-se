/*
 * #%L
 * Service Locator :: Proxy
 * %%
 * Copyright (C) 2011 Talend Inc.
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
package org.talend.esb.locator.rest.proxy.service;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import javax.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;

import org.talend.esb.locator.rest.proxy.service.types.EndpointReferenceListType;
import org.talend.esb.locator.rest.proxy.service.types.EntryType;
import org.talend.esb.locator.rest.proxy.service.types.RegisterEndpointRequestType;
import org.talend.esb.servicelocator.client.SLPropertiesImpl;
import org.talend.esb.servicelocator.client.SLPropertiesMatcher;
import org.talend.esb.servicelocator.client.ServiceLocator;
import org.talend.esb.servicelocator.client.ServiceLocatorException;
import org.talend.esb.servicelocator.client.internal.ServiceLocatorImpl;

public class LocatorProxyServiceImpl implements LocatorProxyService {

	private static final Logger LOG = Logger
			.getLogger(LocatorProxyServiceImpl.class.getPackage().getName());

	private ServiceLocator locatorClient = null;

	private Random random = new Random();

	private String locatorEndpoints = "localhost:2181";

	private int sessionTimeout = 5000;

	private int connectionTimeout = 5000;
	@Context
    private UriInfo uriInfo;

	public void setLocatorClient(ServiceLocator locatorClient) {
		this.locatorClient = locatorClient;
		if (LOG.isLoggable(Level.FINE)) {
			LOG.log(Level.FINE, "Locator client was set for proxy service.");
		}
	}

	public void setLocatorEndpoints(String locatorEndpoints) {
		this.locatorEndpoints = locatorEndpoints;
	}

	public void setSessionTimeout(int sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	/**
	 * Instantiate Service Locator client. After successful instantiation
	 * establish a connection to the Service Locator server. This method will be
	 * called if property locatorClient is null. For this purpose was defined
	 * additional properties to instantiate ServiceLocatorImpl.
	 * 
	 * @throws InterruptedException
	 * @throws ServiceLocatorException
	 */
	public void initLocator() throws InterruptedException,
			ServiceLocatorException {
		if (locatorClient == null) {
			if (LOG.isLoggable(Level.FINE)) {
				LOG.fine("Instantiate locatorClient client for Locator Server "
						+ locatorEndpoints + "...");
			}
			ServiceLocatorImpl client = new ServiceLocatorImpl();
			client.setLocatorEndpoints(locatorEndpoints);
			client.setConnectionTimeout(connectionTimeout);
			client.setSessionTimeout(sessionTimeout);
			locatorClient = client;
			locatorClient.connect();
		}
	}

	/**
	 * Should use as destroy method. Disconnects from a Service Locator server.
	 * All endpoints that were registered before are removed from the server.
	 * Set property locatorClient to null.
	 * 
	 * @throws InterruptedException
	 * @throws ServiceLocatorException
	 */
	public void disconnectLocator() throws InterruptedException,
			ServiceLocatorException {
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("Destroy Locator client");
		}
		if (locatorClient != null) {
			locatorClient.disconnect();
			locatorClient = null;
		}
	}

	public void registerEndpoint(RegisterEndpointRequestType arg0) {
		String endpointURL = arg0.getEndpointURL();
		QName serviceName = QName.valueOf(arg0.getServiceName());
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("Registering endpoint " + endpointURL + " for service "
					+ serviceName + "...");
		}
		try {
			initLocator();
			if (arg0.getEntryType().isEmpty()) {
				locatorClient.register(serviceName, endpointURL);
			} else {
				SLPropertiesImpl slProps = new SLPropertiesImpl();
				List<EntryType> entries = arg0.getEntryType();
				for (EntryType entry : entries) {
					slProps.addProperty(entry.getKey(), entry.getValue());
				}
				locatorClient.register(serviceName, endpointURL, slProps);
			}
		} catch (ServiceLocatorException e) {
			//throw new ServiceLocatorFault(e.getMessage(), e);
			throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build());
		} catch (InterruptedException e) {
			//throw new InterruptedExceptionFault(e.getMessage(), e);
			throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build());
		}
		UriBuilder locationBuilder = uriInfo.getBaseUriBuilder();
		locationBuilder = locationBuilder.uri(uriInfo.getRequestUri());
		System.out.println(serviceName);
		URI endpointLocation = null;
		URI serviceLocation = null;
		try{
		    endpointLocation = locationBuilder.path("{seviceName}/{endpointURL}").buildFromEncoded( URLEncoder.encode(arg0.getServiceName(), "UTF-8") ,URLEncoder.encode(endpointURL,"UTF-8"));
		    locationBuilder = uriInfo.getBaseUriBuilder();
			serviceLocation = locationBuilder.path("{seviceName}").buildFromEncoded( URLEncoder.encode(arg0.getServiceName(), "UTF-8"));
		}
		catch (UnsupportedEncodingException e) {
			throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build());
		}
		//return Response.status(Response.Status.OK).entity(serviceLocation.toString()).location(endpointLocation).build();
	}

	public void unregisterEndpoint(String arg0, String arg1) {
		String endpointURL = null;
		QName serviceName = null;
		try {
			serviceName = QName.valueOf(URLDecoder.decode(arg0, "UTF-8"));
			endpointURL = URLDecoder.decode(arg1, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity(e1.getMessage()).build());
		}
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("Unregistering endpoint " + endpointURL + " for service "
					+ serviceName + "...");
		}
		try {
			initLocator();
			locatorClient.unregister(serviceName, endpointURL);
		} catch (ServiceLocatorException e) {
//			throw new ServiceLocatorFault(e.getMessage(), e);
			throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build());
		} catch (InterruptedException e) {
//			throw new InterruptedExceptionFault(e.getMessage(), e);
			throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build());
		}
		//return Response.status(Response.Status.OK).build();
	}

	@Override
	public W3CEndpointReference lookupEndpoint(String arg0, List<String> arg1) {
		QName serviceName = null;
		try {
			serviceName = QName.valueOf(URLDecoder.decode(arg0, "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error during decoding serviceName").build());
		}
		List<String> names = null;
		String adress = null;
		SLPropertiesMatcher matcher = null;
		try {
			matcher = createMatcher(arg1);
		} catch (UnsupportedEncodingException e1) {
			throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error during decoding serviceName").build());
		}
		try {
			initLocator();
			if (matcher == null) {
				names = locatorClient.lookup(serviceName);
			} else {
				names = locatorClient.lookup(serviceName, matcher);
			}
		} catch (ServiceLocatorException e) {
			throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build());
			//throw new ServiceLocatorFault(e.getMessage(), e);
		} catch (InterruptedException e) {
			//throw new InterruptedExceptionFault(e.getMessage(), e);
			throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build());
		}
		if (names != null && !names.isEmpty()) {
			names = getRotatedList(names);
			adress = names.get(0);
		} else {
			if (LOG.isLoggable(Level.WARNING)) {
				LOG.log(Level.WARNING, "lookup Endpoint for " + serviceName 	+ " failed, service is not known.");
			}
			throw new WebApplicationException(Response.status(Status.NOT_FOUND).entity("lookup Endpoint for " + serviceName	+ " failed, service is not known.").build());
		}
		W3CEndpointReference ref = buildEndpoint(serviceName, adress);
		return ref;
	}

	@Override
	public EndpointReferenceListType lookupEndpoints(String arg0, List<String> arg1) {
		QName serviceName = null;
		try {
			serviceName = QName.valueOf(URLDecoder.decode(arg0, "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity(e1.getMessage()).build());
		}
		List<String> names = null;
		String adress = null;
		SLPropertiesMatcher matcher = null;
		try {
			matcher = createMatcher(arg1);
		} catch (UnsupportedEncodingException e1) {
			throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity(e1.getMessage()).build());
		}
		EndpointReferenceListType refs = new EndpointReferenceListType();
		try {
			initLocator();
			if (matcher == null) {
				names = locatorClient.lookup(serviceName);
			} else {
				names = locatorClient.lookup(serviceName, matcher);
			}
		} catch (ServiceLocatorException e) {
			throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build());
			//throw new ServiceLocatorFault(e.getMessage(), e);
		} catch (InterruptedException e) {
			throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build());
			//throw new InterruptedExceptionFault(e.getMessage(), e);
		}
		if (names != null && !names.isEmpty()) {
			for (int i = 0; i < names.size(); i++) {
				adress = names.get(i);
				refs.getReturn().add(buildEndpoint(serviceName, adress));
			}
		} else {
			if (LOG.isLoggable(Level.WARNING)) {
				LOG.log(Level.WARNING, "lookup Endpoints for " + serviceName + " failed, service is not known.");
			}
			throw new WebApplicationException(Response.status(Status.NOT_FOUND).entity("Service not found").build());
		}
		return refs;
	}
	
	private SLPropertiesMatcher createMatcher(List<String> input) throws UnsupportedEncodingException {
		SLPropertiesMatcher matcher = null;
		if (input != null && input.size() > 0) {
			matcher = new SLPropertiesMatcher();
			for (String pair : input) {
				String[] assertion = URLDecoder.decode(pair, "UTF-8").split(",");
				if(assertion.length == 2)
				{
					matcher.addAssertion(assertion[0], assertion[1]);
				}
			}
		}
		return matcher;
	}

	/**
	 * Rotate list of String. Used for randomize selection of received endpoints
	 * 
	 * @param strings
	 *            list of Strings
	 * @return the same list in random order
	 */
	private List<String> getRotatedList(List<String> strings) {
		int index = random.nextInt(strings.size());
		List<String> rotated = new ArrayList<String>();
		for (int i = 0; i < strings.size(); i++) {
			rotated.add(strings.get(index));
			index = (index + 1) % strings.size();
		}
		return rotated;
	}

	/**
	 * Build Endpoint Reference for giving service name and address
	 * 
	 * @param serviceName
	 * @param adress
	 * @return
	 */
	private W3CEndpointReference buildEndpoint(QName serviceName, String adress) {
		W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
		builder.serviceName(serviceName);
		builder.address(adress);
		return builder.build();
	}
}
