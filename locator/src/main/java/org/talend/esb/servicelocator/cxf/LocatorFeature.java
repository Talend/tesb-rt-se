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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.feature.AbstractFeature;
import org.talend.esb.servicelocator.cxf.internal.ServiceLocatorManager;

/**
 * CXF feature to enable the locator client with an CXF service.
 *
 */
public class LocatorFeature extends AbstractFeature {

	private static final Logger LOG = Logger.getLogger(LocatorFeature.class
			.getName());

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
		slm.registerServer(server);
	}

	/**
	 * Specify the endpoints of all the instances belonging to the service locator ensemble the
	 * service locator client might be talking to. The service locator client will one by one pick
	 * an endpoint (the order is non-deterministic) to connect to the service locator until a
	 * connection is established.
	 * 
	 * @param endpoints comma separated list of endpoints,each corresponding to a servicelocator
	 *           instance. Each endpoint is specified as a host:port pair. At least one endpoint
	 *           must be specified. Valid exmaples are: "127.0.0.1:2181" or
	 *           "sl1.example.com:3210, sl2.example.com:3210, sl3.example.com:3210"
	 */
/*
	public void setLocatorEndpoints(String endpoints) {
		locatorEndpoints = endpoints;
	}
*/
	/**
	 * Specify the time out of the session established at the server. The session is kept alive by
	 * requests sent by the client. If the session is idle for a period of time that would timeout
	 * the session, the client will send a PING request to keep the session alive.
	 * 
	 * @param sessionTimeout timeout in milliseconds, must be greater than  zero and less than 60000. 
	 */
/*
	public void setSessionTimeout(int sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}
*/
	/**
	 * Specify the time the service locator client waits for a connection to get established.
	 * 
	 * @param connectionTimeout timeout in milliseconds, must be greater than zero
	 */
/*
	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public void setEndpointPrefix(String prefix) {
		this.prefix = prefix;
	}
*/
}