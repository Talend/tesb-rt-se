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
import java.util.Collections;
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

	public void initLocator() throws InterruptedException, ServiceLocatorException {
    	if (locatorClient == null) {
    		ServiceLocatorImpl client = new ServiceLocatorImpl();
    		client.setLocatorEndpoints(locatorEndpoints);
    		client.setConnectionTimeout(connectionTimeout);
    		client.setSessionTimeout(sessionTimeout);
    		locatorClient = client;
    		locatorClient.connect();
    	}
    }
	
	public void disconnectLocator() throws InterruptedException, ServiceLocatorException {
		if (locatorClient != null) {
			locatorClient.disconnect();
		}
	}
	
    
	@Override
	public void registerEndpoint(RegisterEndpointRequestType imput)
			throws InterruptedExceptionFault, ServiceLocatorFault {
		String endpointURL = imput.getEndpointURL();
		QName serviceName = imput.getServiceName();
		try {
		initLocator();
        if(!endpointURL.startsWith("http://") && !endpointURL.startsWith("https://")) { // relative address
        	endpointURL = endpointPrefix + endpointURL;
        }
		locatorClient.register(serviceName, endpointURL);
	} catch (ServiceLocatorException e) {
		throw new ServiceLocatorFault("", e);
	} catch (InterruptedException e) {
		throw new InterruptedExceptionFault("", e);
	}
		
	}

//	public void registerEndpoint(QName serviceName, String endpointURL)
//			throws InterruptedExceptionFault, ServiceLocatorFault {
//		try {
//			initLocator();
//	        if(!endpointURL.startsWith("http://") && !endpointURL.startsWith("https://")) { // relative address
//	        	endpointURL = endpointPrefix + endpointURL;
//	        }
//			locatorClient.register(serviceName, endpointURL);
//		} catch (ServiceLocatorException e) {
//			throw new InterruptedExceptionFault("", e);
//		} catch (InterruptedException e) {
//			throw new ServiceLocatorFault("", e);
//		}
//	}

	@Override
	public void unregisterEnpoint(QName serviceName, String endpointURL)
			throws InterruptedExceptionFault, ServiceLocatorFault {
		try {
			initLocator();
	        if(!endpointURL.startsWith("http://") && !endpointURL.startsWith("https://")) { // relative address
	        	endpointURL = endpointPrefix + endpointURL;
	        }
			locatorClient.unregister(serviceName, endpointURL);
		} catch (ServiceLocatorException e) {
			throw new ServiceLocatorFault("", e);
		} catch (InterruptedException e) {
			throw new InterruptedExceptionFault("", e);
		}
		return;
	}

	@Override
	public W3CEndpointReference lookupEndpoint(QName serviceName)
			throws InterruptedExceptionFault, ServiceLocatorFault {
		List<String> names = null;
		String adress;
		try {
			initLocator();
			names = locatorClient.getEndpointNames(serviceName);
		} catch (ServiceLocatorException e) {
			throw new ServiceLocatorFault("", e);
		} catch (InterruptedException e) {
			throw new InterruptedExceptionFault("", e);
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

	@Override
	public EndpointReferenceListType lookupEndpoints(QName serviceName) throws InterruptedExceptionFault, ServiceLocatorFault {
		List<String> names = null;
		List<W3CEndpointReference> endpointRefList = Collections.emptyList();
		String adress;
		try {
			initLocator();
			names = locatorClient.getEndpointNames(serviceName);
		} catch (ServiceLocatorException e) {
			throw new ServiceLocatorFault("", e);
		} catch (InterruptedException e) {
			throw new InterruptedExceptionFault("", e);
		}
		if (names != null && !names.isEmpty()) {
			for (int i = 0; i < names.size(); i++) {
				adress = names.get(i);
				endpointRefList.add(buildEndpoint(serviceName, adress));
			}
			adress = names.get(0);
		} else {
			if (LOG.isLoggable(Level.WARNING)) {
				LOG.log(Level.WARNING, "lookup Endpoint " + serviceName
						+ " failed, service is not known.");
			}
			return null;
		}
		return (EndpointReferenceListType)endpointRefList;
	}

	private List<String> getRotatedList(List<String> strings) {
		int index = random.nextInt(strings.size());
		List<String> rotated = new ArrayList<String>();
		for (int i = 0; i < strings.size(); i++) {
			rotated.add(strings.get(index));
			index = (index + 1) % strings.size();
		}
		return rotated;
	}

	private W3CEndpointReference buildEndpoint(QName serviceName, String adress) {
		W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
		builder.serviceName(serviceName);
		builder.address(adress);
		return builder.build();
	}
}
