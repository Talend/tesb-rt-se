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

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.fail;
import static  org.talend.esb.servicelocator.TestValues.*;


import javax.xml.namespace.QName;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.endpoint.ServerLifeCycleListener;
import org.apache.cxf.endpoint.ServerLifeCycleManager;

import org.junit.Test;
import org.talend.esb.servicelocator.client.ServiceLocator;
import org.talend.esb.servicelocator.client.ServiceLocator.PostConnectAction;
import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.service.model.ServiceInfo;

public class LocatorRegistrarTest {

	public static final String PREFIX = "prefix";

	public static final Server SERVER_1 = createServerStub(SERVICE_QNAME_1, ENDPOINT_1);

	public static final Server SERVER_2 = createServerStub(SERVICE_QNAME_2, ENDPOINT_2);

	private ServiceLocator sl = createMock(ServiceLocator.class);
	
	@Test
	public void postConnectActionRegistered() {
		sl.setPostConnectAction((PostConnectAction) anyObject());
		replay(sl);

		LocatorRegistrar locatorRegistrar = new LocatorRegistrar();
		locatorRegistrar.setServiceLocator(sl);

		verify(sl);
		
	}

	@Test
	public void registerEndpoint() throws Exception {
		sl.setPostConnectAction((PostConnectAction) anyObject());
		sl.register(SERVICE_QNAME_1, ENDPOINT_1);
		replay(sl);

		LocatorRegistrar locatorRegistrar = new LocatorRegistrar();
		locatorRegistrar.setServiceLocator(sl);
		
		locatorRegistrar.registerServer(SERVER_1);

		verify(sl);
	}

	@Test
	public void registerEndpointWithPrefixSet() throws Exception {
		sl.setPostConnectAction((PostConnectAction) anyObject());
		sl.register(SERVICE_QNAME_1, PREFIX + ENDPOINT_1);
		replay(sl);

		LocatorRegistrar locatorRegistrar = new LocatorRegistrar();
		locatorRegistrar.setEndpointPrefix(PREFIX);
		locatorRegistrar.setServiceLocator(sl);
		
		locatorRegistrar.registerServer(SERVER_1);

		verify(sl);
	}

	@Test
	public void registerEndpointLocatorNull() throws Exception {
		replay(sl);

		LocatorRegistrar locatorRegistrar = new LocatorRegistrar();
		
		try {
			locatorRegistrar.registerServer(SERVER_1);
			fail("An IllegalStateException should have been thrown.");
		} catch (IllegalStateException e) {
			
		}

		verify(sl);
	}

	@Test
	public void registerServerLifeCycleListener() {
		ServerLifeCycleManager slcm = createMock(ServerLifeCycleManager.class);
		slcm.registerListener((ServerLifeCycleListener) anyObject());
		Bus bus = createMock(Bus.class);
		expect(bus.getExtension(ServerLifeCycleManager.class)).andStubReturn(slcm);
		replay(slcm, bus);

		LocatorRegistrar locatorRegistrar = new LocatorRegistrar();
		locatorRegistrar.setBus(bus);

		verify(slcm, bus);
	}

	@Test
	public void serverStopsThenEndpointUnregistered() throws Exception {
		sl.setPostConnectAction((PostConnectAction) anyObject());
		sl.register(SERVICE_QNAME_1, ENDPOINT_1);
		sl.unregister(SERVICE_QNAME_1, ENDPOINT_1);

		replay(sl);

		LocatorRegistrar locatorRegistrar = new LocatorRegistrar();
		locatorRegistrar.setServiceLocator(sl);

		locatorRegistrar.registerServer(SERVER_1);
		locatorRegistrar.stopServer(SERVER_1);

		verify(sl);
	}

	@Test
	public void serverStopsIfNotRegisteredBeforeDoNothing() throws Exception {
		sl.setPostConnectAction((PostConnectAction) anyObject());

		replay(sl);

		LocatorRegistrar locatorRegistrar = new LocatorRegistrar();
		locatorRegistrar.setServiceLocator(sl);

		locatorRegistrar.stopServer(SERVER_1);

		verify(sl);
	}

	@Test
	public void processReregisterAllEndpoints() throws Exception {

		sl.setPostConnectAction((PostConnectAction) anyObject());
		sl.register(SERVICE_QNAME_1, ENDPOINT_1);
		sl.register(SERVICE_QNAME_2, ENDPOINT_2);
		sl.register(SERVICE_QNAME_1, ENDPOINT_1);
		sl.register(SERVICE_QNAME_2, ENDPOINT_2);
		replay(sl);

		LocatorRegistrar locatorRegistrar = new LocatorRegistrar();
		locatorRegistrar.setServiceLocator(sl);

		locatorRegistrar.registerServer(SERVER_1);
		locatorRegistrar.registerServer(SERVER_2);

		locatorRegistrar.process(sl);
		verify(sl);
	}

	private static Server createServerStub(QName serviceName, String endpointName) {
		ServiceInfo serviceInfo = createNiceMock(ServiceInfo.class);
		expect(serviceInfo.getName()).andStubReturn(serviceName);

		EndpointInfo endpointInfo = createMock(EndpointInfo.class);
		expect(endpointInfo.getAddress()).andStubReturn(endpointName);
		expect(endpointInfo.getService()).andStubReturn(serviceInfo);

		Endpoint endpoint = createNiceMock(Endpoint.class);
		expect(endpoint.getEndpointInfo()).andStubReturn(endpointInfo);

		Server server = createNiceMock(Server.class);
		expect(server.getEndpoint()).andStubReturn(endpoint);

		replay(serviceInfo, endpointInfo, endpoint, server);
		return server;
	}
}
