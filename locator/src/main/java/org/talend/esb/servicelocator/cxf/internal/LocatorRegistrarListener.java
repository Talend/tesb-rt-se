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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.endpoint.ServerLifeCycleListener;
import org.apache.cxf.endpoint.ServerLifeCycleManager;
import org.apache.cxf.endpoint.ServerRegistry;

public class LocatorRegistrarListener implements ServerLifeCycleListener {

	private static final Logger LOG = Logger.getLogger(LocatorRegistrarListener.class
			.getPackage().getName());

	private Bus bus;

	private LocatorRegistrar locatorRegistar;

	@Override
	public void startServer(Server server) {
		if (LOG.isLoggable(Level.FINE)) {
			LOG.log(Level.FINE, "Server " + server + " started...");
		}
		locatorRegistar.registerServer(server);
	}

	@Override
	public void stopServer(Server server) {}

	public void setBus(Bus bus) {
		if (bus != this.bus) {
			this.bus = bus;
		}
	}

	public void setLocatorRegistrar(LocatorRegistrar locatorRegistrar) {
		this.locatorRegistar = locatorRegistrar;
	}

	public void enable() {
		check(bus, "bus", "enable");
		registerListener();
		registerAvailableServers();
	}
	
	public void disable() {
		check(bus, "bus", "enable");
		unregisterListener();
	}

	private void registerAvailableServers() {
		ServerRegistry serverRegistry = bus.getExtension(ServerRegistry.class);
		List<Server> servers = serverRegistry.getServers();
		for (Server server : servers) {
			locatorRegistar.registerServer(server);
		}
	}

	private void registerListener() {
		ServerLifeCycleManager manager = bus
				.getExtension(ServerLifeCycleManager.class);
		if (manager != null) {
			manager.registerListener(this);
			if (LOG.isLoggable(Level.FINE)) {
				LOG.log(Level.FINE, "LocatorRegistrarListener registered as server life cycle listener.");
			}
		} else {
			if (LOG.isLoggable(Level.WARNING)) {
				LOG.log(Level.WARNING, "ServerLifeCycleManager is not available.");
			}
		}
	}

	private void unregisterListener() {
		ServerLifeCycleManager manager = bus
				.getExtension(ServerLifeCycleManager.class);
		if (manager != null) {
			manager.unRegisterListener(this);
			if (LOG.isLoggable(Level.FINE)) {
				LOG.log(Level.FINE, "LocatorRegistrarListener unregistered as server life cycle listener.");
			}
		} else {
			if (LOG.isLoggable(Level.WARNING)) {
				LOG.log(Level.WARNING, "ServerLifeCycleManager is not available.");
			}
		}
	}

	void check(Object obj, String propertyName, String methodName) {
		if (obj == null) {
			throw new IllegalStateException(
				"The property " + propertyName + " must be set before "
					+ methodName + " can be called.");
		}
	}

}
