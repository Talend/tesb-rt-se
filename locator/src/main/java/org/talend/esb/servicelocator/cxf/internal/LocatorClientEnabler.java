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

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.ClientLifeCycleListener;
import org.apache.cxf.endpoint.ClientLifeCycleManager;
import org.talend.esb.servicelocator.client.ServiceLocator;

public class LocatorClientEnabler implements ClientLifeCycleListener {

	private static final Logger LOG = Logger.getLogger(LocatorClientEnabler.class
			.getPackage().getName());
	
	private static final String DEFAULT_STRATEGY = "defaultSelectionStrategy";
	
	private ServiceLocator locatorClient;

	private Bus bus;

	private Map<String,LocatorSelectionStrategy> locatorSelectionStrategies;
	
	private LocatorSelectionStrategy locatorSelectionStrategy;

	public void setServiceLocator(ServiceLocator locatorClient) {
		this.locatorClient = locatorClient;
		if (LOG.isLoggable(Level.FINE)) {
			LOG.log(Level.FINE, "Locator client " + locatorClient + " was set for LocatorClientRegistrar.");
		}
	}
	
	public void setBus(Bus bus) {
		this.bus = bus;
		if (LOG.isLoggable(Level.FINE)) {
			LOG.log(Level.FINE, "Bus " + bus + " was set for LocatorClientRegistrar.");
		}
	}

	public void setLocatorSelectionStrategies(
			Map<String, LocatorSelectionStrategy> locatorSelectionStrategies) {
		this.locatorSelectionStrategies = locatorSelectionStrategies;
		this.locatorSelectionStrategy = locatorSelectionStrategies.get(DEFAULT_STRATEGY);
	}

	public void setLocatorSelectionStrategy(String locatorSelectionStrategy) {
		if (LOG.isLoggable(Level.FINE)) {
			LOG.log(Level.FINE, "Strategy " + locatorSelectionStrategy + " was set for LocatorClientRegistrar.");
		}
		if (locatorSelectionStrategies.containsKey(locatorSelectionStrategy)) {
			this.locatorSelectionStrategy = locatorSelectionStrategies.get(locatorSelectionStrategy);
		} else {
			if (LOG.isLoggable(Level.WARNING))
				LOG.log(Level.WARNING, "LocatorSelectionStrategy " + locatorSelectionStrategy + " not registered at LocatorClientEnabler.");
		}
	}

	public void enable(Client client) {
		LocatorTargetSelector selector = new LocatorTargetSelector();
        selector.setEndpoint(client.getEndpoint());

        locatorSelectionStrategy.setServiceLocator(locatorClient);
		selector.setLocatorSelectionStrategy(locatorSelectionStrategy);
		if (LOG.isLoggable(Level.FINE)) {
			LOG.log(Level.FINE, "Client enabled with strategy " + locatorSelectionStrategy.getClass().getName() + ".");
		}
        client.setConduitSelector(selector);

		if (LOG.isLoggable(Level.FINE)) {
			LOG.log(Level.FINE, "Successfully enabled client " + client + " for the service locator");
		}
	}

	public void startListenForAllClients() {
		ClientLifeCycleManager clcm = bus.getExtension(ClientLifeCycleManager.class);
		clcm.registerListener(this);
	}

	public void stopListenForAllClients() {
		ClientLifeCycleManager clcm = bus.getExtension(ClientLifeCycleManager.class);
		clcm.unRegisterListener(this);
	}

	@Override
	public void clientCreated(Client client) {
		enable(client);
	}

	@Override
	public void clientDestroyed(Client client) {
	}
}
