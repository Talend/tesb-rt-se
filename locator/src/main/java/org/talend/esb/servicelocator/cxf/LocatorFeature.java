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
package org.talend.esb.servicelocator.cxf;

import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.feature.AbstractFeature;
import org.talend.esb.servicelocator.client.SLPropertiesImpl;
import org.talend.esb.servicelocator.cxf.internal.ServiceLocatorManager;

/**
 * CXF feature to enable the locator client with an CXF service.
 *
 */
public class LocatorFeature extends AbstractFeature {

	private static final Logger LOG = Logger.getLogger(LocatorFeature.class
			.getName());

    private SLPropertiesImpl slProps;

	@Override
	public void initialize(Bus bus) {
		if (LOG.isLoggable(Level.FINE)) {
			LOG.log(Level.FINE, "Initializing Locator feature for bus " + bus + ".");
		}

		ServiceLocatorManager slm = bus.getExtension(ServiceLocatorManager.class);
		slm.listenForAllServers();
		slm.listenForAllClients();
		
	}
	
	@Override
	public void initialize(Client client, Bus bus) {
		if (LOG.isLoggable(Level.FINE)) {
			LOG.log(Level.FINE, "Initializing Locator feature for bus " + bus + " and client ." + client);
		}

		ServiceLocatorManager slm = bus.getExtension(ServiceLocatorManager.class);
		slm.enableClient(client);
	}

	@Override
	public void initialize(Server server, Bus bus) {
		if (LOG.isLoggable(Level.FINE)) {
			LOG.log(Level.FINE, "Initializing Locator feature for bus " + bus + " and server ." + server);
		}

		ServiceLocatorManager slm = bus.getExtension(ServiceLocatorManager.class);
		slm.registerServer(server, slProps);
	}

	protected ServiceLocatorManager getLocatorManager(Bus bus) {
		return bus.getExtension(ServiceLocatorManager.class);
	}

	/**
	 *
	 *
	 * @param properties
	 */
    @SuppressWarnings("unchecked")
	public void setEndpointProperties(Map<String, ?> properties) {
	    slProps = new SLPropertiesImpl();
	    
	    for(String key : properties.keySet()) {
	        Object val = properties.get(key);
	        if (val instanceof Collection) {
                Collection<String> values = (Collection<String>) val;
	            slProps.addProperty(key, values);
	        } else if (val instanceof String) {
                slProps.addProperty(key, (String)val);
	        }
	    }
	}

}