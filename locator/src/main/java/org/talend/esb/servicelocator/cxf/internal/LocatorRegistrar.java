/*
 * #%L
 * Service Locator Client for CXF
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
package org.talend.esb.servicelocator.cxf.internal;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.endpoint.ServerLifeCycleListener;
import org.apache.cxf.endpoint.ServerLifeCycleManager;
import org.apache.cxf.endpoint.ServerRegistry;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.service.model.ServiceInfo;
import org.talend.esb.servicelocator.client.ServiceLocator;
import org.talend.esb.servicelocator.client.internal.ServiceLocatorException;

public class LocatorRegistrar implements ServerLifeCycleListener,
		ServiceLocator.PostConnectAction {

	private static final Logger LOG = Logger.getLogger(LocatorRegistrar.class
			.getPackage().getName());

	private Bus bus;

	private ServiceLocator locatorClient;

	private String endpointPrefix = "";

	private Set<Server> registeredServers = new HashSet<Server>();
	
	private boolean listenForServersEnabled;
	
	@Override
	public void startServer(Server server) {
		if (LOG.isLoggable(Level.FINE)) {
			LOG.log(Level.FINE, "Server " + server + " starting...");
		}
		if (listenForServersEnabled) {
			registerServer(server);
		}
	
	}

	@Override
	public void stopServer(Server server) {
		if (LOG.isLoggable(Level.FINE)) {
			LOG.log(Level.FINE, "Server " + server + " stopping...");
		}
		if (registeredServers.remove(server)) {
			unregisterServer(server);
		}
	}

	public void startListenForServers() {
		check(bus, "bus", "startListenForServers");
		listenForServersEnabled = true;
		registerAvailableServers();
	}

	public void stopListenForServers() {
		listenForServersEnabled = false;
	}

	@Override
	public void process(ServiceLocator lc) {
		for (Server server : registeredServers) {
			registerServer(server);
		}
	}

	public void setBus(Bus bus) {
		if(bus != this.bus) {
			this.bus = bus;
			registerListener();
		}
	}

	public void setEndpointPrefix(String endpointPrefix) {
		this.endpointPrefix = endpointPrefix != null ? endpointPrefix : "";
	}

	public void setServiceLocator(ServiceLocator locatorClient) {
		this.locatorClient = locatorClient;
		locatorClient.setPostConnectAction(this);
		if (LOG.isLoggable(Level.FINE)) {
			LOG.log(Level.FINE, "Locator client was set.");
		}
	}

	private void registerListener() {
		ServerLifeCycleManager manager = bus
				.getExtension(ServerLifeCycleManager.class);
		if (manager != null) {
			manager.registerListener(this);
			if (LOG.isLoggable(Level.FINE)) {
				LOG.log(Level.FINE, "Server life cycle listener registered.");
			}
		} else {
			if (LOG.isLoggable(Level.WARNING)) {
				LOG.log(Level.WARNING, "ServerLifeCycleManager is not available.");
			}
		}
	}

	public void registerServer(Server server) {
		check(locatorClient, "serviceLocator", "registerEndpoint");
		QName serviceName = getServiceName(server);
		String endpointAddress = endpointPrefix + getAddress(server);
		try {
			locatorClient.register(serviceName, endpointAddress);
		} catch (ServiceLocatorException e) {
			if (LOG.isLoggable(Level.SEVERE)) {
				LOG.log(Level.SEVERE,
						"ServiceLocator Exception thrown when registering endpoint. ", e);
			}
		} catch (InterruptedException e) {
			if (LOG.isLoggable(Level.SEVERE)) {
				LOG.log(Level.SEVERE,
						"Interrupted Exception thrown when registering endpoint.", e);
			}
		}
		registeredServers.add(server);
	}

	private void unregisterServer(Server server) {
		QName serviceName = getServiceName(server);
		String endpointAddress = endpointPrefix + getAddress(server);

		try {
			locatorClient.unregister(serviceName, endpointAddress);
		} catch (ServiceLocatorException e) {
			if (LOG.isLoggable(Level.SEVERE)) {
				LOG.log(Level.SEVERE,
						"ServiceLocator Exception thrown during unregister endpoint. ", e);
			}
		} catch (InterruptedException e) {
			if (LOG.isLoggable(Level.SEVERE)) {
				LOG.log(Level.SEVERE,
						"Interrupted Exception thrown during unregister endpoint.", e);
			}
		}
	}
	
	private void registerAvailableServers() {
		ServerRegistry serverRegistry = bus.getExtension(ServerRegistry.class);
		List<Server> servers = serverRegistry.getServers();
		for (Server server : servers) {
			registerServer(server);
		}
	}


	private QName getServiceName(Server server) {
		EndpointInfo eInfo = server.getEndpoint().getEndpointInfo();
		ServiceInfo serviceInfo = eInfo.getService();
		return serviceInfo.getName();
	}

	private String getAddress(Server server) {
		EndpointInfo eInfo = server.getEndpoint().getEndpointInfo();
		return eInfo.getAddress();
	}

	void check(Object obj, String propertyName, String methodName) {
		if (obj == null) {
			throw new IllegalStateException(
				"The property " + propertyName + " must be set before "
					+ methodName + " can be called.");
		}
	}
}
