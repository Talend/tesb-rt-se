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
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.extension.BusExtension;
import org.talend.esb.servicelocator.client.ServiceLocator;

public class ServiceLocatorManager implements BusExtension {
	
	private ServiceLocator serviceLocator;
	
	private LocatorRegistrar locatorRegistrar;
	
	private Bus bus;
	
	private LocatorRegistrarListener locatorRegistrarListener;
	
	public void registerAllServers() {
		locatorRegistrarListener.enable();
	}
	
	public void registerServer(Server server) {
		locatorRegistrar.registerServer(server);
	}

	public void setServiceLocator(ServiceLocator serviceLocator) {
		this.serviceLocator = serviceLocator;
	}

	public ServiceLocator getServiceLocator() {
		return serviceLocator;
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

	public void setLocatorRegistrarListener(LocatorRegistrarListener locatorRegistrarListener) {
		this.locatorRegistrarListener = locatorRegistrarListener;
	}

	@Override
	public Class<?> getRegistrationType() {
		return ServiceLocatorManager.class;
	}
}
