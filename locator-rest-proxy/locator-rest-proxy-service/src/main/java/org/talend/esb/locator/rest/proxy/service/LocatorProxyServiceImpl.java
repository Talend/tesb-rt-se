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

import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;

import org.talend.esb.locator.rest.proxy.service.types.EndpointReferenceListType;
import org.talend.esb.locator.rest.proxy.service.types.RegisterEndpointRequestType;
import org.talend.esb.servicelocator.client.ServiceLocator;
import org.talend.esb.servicelocator.client.ServiceLocatorException;
import org.talend.esb.servicelocator.client.internal.ServiceLocatorImpl;
import org.w3._2005._08.addressing.EndpointReferenceType;

public class LocatorProxyServiceImpl implements LocatorProxyService {

	private static final Logger LOG = Logger
			.getLogger(LocatorProxyServiceImpl.class.getPackage().getName());

	private ServiceLocator locatorClient = null;

	private Random random = new Random();

	private String locatorEndpoints = "localhost:2181";

	private int sessionTimeout = 5000;

	private int connectionTimeout = 5000;

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

	public EndpointReferenceType lookupEndpoint(String arg0, List<String> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public EndpointReferenceListType lookupEndpoints(String arg0,
			List<String> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Response registerEndpoint(RegisterEndpointRequestType arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Response unregisterEndpoint(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}
}
