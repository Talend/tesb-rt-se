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
package org.talend.esb.locator.proxy.service;

import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import javax.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;

import org.talend.esb.servicelocator.client.ServiceLocator;
import org.talend.esb.servicelocator.client.ServiceLocatorException;
import org.talend.esb.servicelocator.client.internal.ServiceLocatorImpl;
import org.talend.esb.locator.proxy.service.types.EndpointReferenceListType;
import org.talend.esb.locator.proxy.service.types.RegisterEndpointRequestType;
import org.talend.esb.locator.proxy.service.types.UnregisterEndpointRequestType;

public class LocatorProxyServiceImpl implements LocatorProxyService {

	private static final Logger LOG = Logger
			.getLogger(LocatorProxyServiceImpl.class.getPackage().getName());

	private ServiceLocator locatorClient = null;
	
	private String endpointPrefix = "";

	private Random random = new Random();
	
    private String locatorEndpoints = "localhost:2181";

    private int sessionTimeout = 5000;

    private int connectionTimeout = 5000;

	public void setServiceLocator(ServiceLocator locatorClient) {
		this.locatorClient = locatorClient;
		if (LOG.isLoggable(Level.FINE)) {
			LOG.log(Level.FINE, "Locator client was set for proxy service.");
		}
	}
	
    public void setEndpointPrefix(String endpointPrefix) {
        this.endpointPrefix = endpointPrefix != null ? endpointPrefix : "";
    }
	
    public void setLocatorClient(ServiceLocator locatorClient) {
		this.locatorClient = locatorClient;
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
	 * Instantiate Service Locator client.
	 * After successful instantiation establish a connection to the Service Locator server.
	 * This method will be called if property locatorClient is null.
     * For this purpose was defined additional properties to instantiate ServiceLocatorImpl.
	 * @throws InterruptedException
	 * @throws ServiceLocatorException
	 */
	public void initLocator() throws InterruptedException, ServiceLocatorException {
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
	 * @throws InterruptedException
	 * @throws ServiceLocatorException
	 */
	public void disconnectLocator() throws InterruptedException, ServiceLocatorException {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Destroy Locator client");
        }
		if (locatorClient != null) {
			locatorClient.disconnect();
			locatorClient = null;
		}
	}
	
    
	/**
	 * Register the endpoint for given service.
	 * @param input 
	 * 			RegisterEndpointRequestType encapsulate name of service and endpointURL.
	 * 			Must not be <code>null</code>  
	 */
	@Override
	public void registerEndpoint(RegisterEndpointRequestType input)
			throws InterruptedExceptionFault, ServiceLocatorFault {
		String endpointURL = input.getEndpointURL();
		QName serviceName = input.getServiceName();
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Registering endpoint " + endpointURL + " for service "
                    + serviceName + "...");
        }
		try {
		initLocator();
        if(!endpointURL.startsWith("http://") && !endpointURL.startsWith("https://")) { // relative address
        	endpointURL = endpointPrefix + endpointURL;
        }
		locatorClient.register(serviceName, endpointURL);
	} catch (ServiceLocatorException e) {
		throw new ServiceLocatorFault(e.getMessage(), e);
	} catch (InterruptedException e) {
		throw new InterruptedExceptionFault(e.getMessage(), e);
	}
		
	}


	/**
	 * Unregister the endpoint for given service.
	 * @param input 
	 * 			UnregisterEndpointRequestType encapsulate name of service and endpointURL. 
	 * 			Must not be <code>null</code>  
	 */
	@Override
	public void unregisterEnpoint(UnregisterEndpointRequestType input)
			throws InterruptedExceptionFault, ServiceLocatorFault {
		String endpointURL = input.getEndpointURL();
		QName serviceName = input.getServiceName();
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Unregistering endpoint " + endpointURL + " for service "
                    + serviceName + "...");
        }
		try {
			initLocator();
			if (!endpointURL.startsWith("http://") && !endpointURL.startsWith("https://")) { // relative address
				endpointURL = endpointPrefix + endpointURL;
			}
			locatorClient.unregister(serviceName, endpointURL);
		} catch (ServiceLocatorException e) {
			throw new ServiceLocatorFault(e.getMessage(), e);
		} catch (InterruptedException e) {
			throw new InterruptedExceptionFault(e.getMessage(), e);
		}
	}


	/**
	 * For the given service return endpoint reference randomly selected from list of endpoints
	 * currently registered at the service locator server.
	 * @param serviceName
	 *            the name of the service for which to get the endpoints, must
	 *            not be <code>null</code>
	 * @return endpoint references or <code>null</code>
	 */
	@Override
	public W3CEndpointReference lookupEndpoint(QName serviceName)
			throws InterruptedExceptionFault, ServiceLocatorFault {
		List<String> names = null;
		String adress;
		try {
			initLocator();
			names = locatorClient.getEndpointNames(serviceName);
		} catch (ServiceLocatorException e) {
			throw new ServiceLocatorFault(e.getMessage(), e);
		} catch (InterruptedException e) {
			throw new InterruptedExceptionFault(e.getMessage(), e);
		}
		if (names != null && !names.isEmpty()) {
			names = getRotatedList(names);
			adress = names.get(0);
		} else {
			if (LOG.isLoggable(Level.WARNING)) {
				LOG.log(Level.WARNING, "lookup Endpoint " + serviceName
						+ " failed, service is not known.");
			}
			return null;
		}
		return buildEndpoint(serviceName, adress);
	}

	/**
	 * For the given service name return list of endpoint references currently registered at
	 * the service locator server endpoints.
	 * @param serviceName
	 *            the name of the service for which to get the endpoints, must
	 *            not be <code>null</code>
	 * @return EndpointReferenceListType encapsulate list of endpoint references or <code>null</code>
	 * 
	 */
	@Override
	public EndpointReferenceListType lookupEndpoints(QName serviceName) throws InterruptedExceptionFault, ServiceLocatorFault {
		List<String> names = null;
		EndpointReferenceListType result = new EndpointReferenceListType();
		String adress;
		try {
			initLocator();
			names = locatorClient.getEndpointNames(serviceName);
		} catch (ServiceLocatorException e) {
			throw new ServiceLocatorFault(e.getMessage(), e);
		} catch (InterruptedException e) {
			throw new InterruptedExceptionFault(e.getMessage(), e);
		}
		if (names != null && !names.isEmpty()) {
			for (int i = 0; i < names.size(); i++) {
				adress = names.get(i);
				result.getReturn().add(buildEndpoint(serviceName, adress));
			}
		} else {
			if (LOG.isLoggable(Level.WARNING)) {
				LOG.log(Level.WARNING, "lookup Endpoint " + serviceName
						+ " failed, service is not known.");
			}
			return null;
		}
		return result;
	}

	/**
	 * Rotate list of String. Used for randomize selection of received endpoints
	 * @param strings list of Strings
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
