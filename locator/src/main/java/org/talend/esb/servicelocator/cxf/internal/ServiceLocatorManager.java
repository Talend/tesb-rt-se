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

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.extension.BusExtension;
import org.talend.esb.servicelocator.client.SLProperties;

public class ServiceLocatorManager implements BusExtension {
	
	private LocatorRegistrar locatorRegistrar;
	
	private LocatorClientEnabler clientEnabler;
	
	private Bus bus;
	
	public void listenForAllServers() {
		locatorRegistrar.startListenForServers();
	}

	public void registerServer(Server server) {
		locatorRegistrar.registerServer(server);
	}

    public void registerServer(Server server, SLProperties props) {
        locatorRegistrar.registerServer(server, props);
    }
//	registerServer(Server server, SLProperties props)
	
	public void listenForAllClients() {
		clientEnabler.startListenForAllClients();
	}

	public void enableClient(Client client) {
		clientEnabler.enable(client);
	}

	public void setBus(Bus bus) {
		if (bus != this.bus) {
			this.bus = bus;
			if (bus != null) {
	            bus.setExtension(this, ServiceLocatorManager.class);
			}	 
		}
	}

	public void setLocatorRegistrar(LocatorRegistrar locatorRegistrar) {
		this.locatorRegistrar = locatorRegistrar;
	}

	public void setLocatorClientEnabler(LocatorClientEnabler clientEnabler) {
		this.clientEnabler = clientEnabler;
	}

	@Override
	public Class<?> getRegistrationType() {
		return ServiceLocatorManager.class;
	}
}
