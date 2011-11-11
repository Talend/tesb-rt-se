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

import java.util.Collections;
import java.util.List;


import org.apache.cxf.Bus;
import org.apache.cxf.buslifecycle.BusLifeCycleListener;
import org.apache.cxf.buslifecycle.BusLifeCycleManager;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.endpoint.ServerLifeCycleListener;
import org.apache.cxf.endpoint.ServerLifeCycleManager;
import org.apache.cxf.endpoint.ServerRegistry;
import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.talend.esb.servicelocator.client.ServiceLocator;
import org.talend.esb.servicelocator.client.ServiceLocator.PostConnectAction;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.fail;
import static org.talend.esb.servicelocator.TestValues.*;
import static org.talend.esb.servicelocator.cxf.internal.CXFTestStubs.REL_SERVER_1;
import static org.talend.esb.servicelocator.cxf.internal.CXFTestStubs.SERVER_1;
import static org.talend.esb.servicelocator.cxf.internal.CXFTestStubs.SERVER_2;
import static org.talend.esb.servicelocator.cxf.internal.CXFTestStubs.SERVER_3;
import static org.talend.esb.servicelocator.cxf.internal.CXFTestStubs.SERVER_4;

public class LocatorRegistrarTest extends EasyMockSupport {
    
    public static final CXFEndpointProvider CXF_ENDPOINT_1 =
        new CXFEndpointProvider(SERVICE_QNAME_1, ENDPOINT_1, null);

    public static final CXFEndpointProvider CXF_ENDPOINT_2 =
        new CXFEndpointProvider(SERVICE_QNAME_2, ENDPOINT_2, null);

    public static final CXFEndpointProvider CXF_ENDPOINT_3 =
        new CXFEndpointProvider(SERVICE_QNAME_3, ENDPOINT_3, null);

    public static final CXFEndpointProvider CXF_ENDPOINT_4 =
        new CXFEndpointProvider(SERVICE_QNAME_4, ENDPOINT_4, null);

    private Bus bus1 = createMock(Bus.class);
    
    private Bus bus2 = createMock(Bus.class);

    private ServiceLocator sl;
    
    @Before
    public void setUp() {
        sl = createMock(ServiceLocator.class);
        sl.setPostConnectAction((PostConnectAction) anyObject());
        expectLastCall().anyTimes();
        
    }

    @Test
    public void registerEndpoint() throws Exception {
        addServerLifeCycleManager(bus1);
        addBusLifeCycleManager(bus1);
        sl.register(CXF_ENDPOINT_1);

        replayAll();

        LocatorRegistrar locatorRegistrar = new LocatorRegistrar();
        locatorRegistrar.setServiceLocator(sl);

        locatorRegistrar.registerServer(SERVER_1, bus1);

        verifyAll();
    }

    @Test
    public void register2EndpointsFromSameBus() throws Exception {
        addServerLifeCycleManager(bus1);
        addBusLifeCycleManager(bus1);

        sl.register(CXF_ENDPOINT_1);
        sl.register(CXF_ENDPOINT_2);
        replayAll();

        LocatorRegistrar locatorRegistrar = new LocatorRegistrar();
        locatorRegistrar.setServiceLocator(sl);

        locatorRegistrar.registerServer(SERVER_1, bus1);
        locatorRegistrar.registerServer(SERVER_2, bus1);

        verifyAll();
    }

    @Test
    public void registerEndpointsFromDiferentBusses() throws Exception {
        addServerLifeCycleManager(bus1);
        addBusLifeCycleManager(bus1);

        addServerLifeCycleManager(bus2);
        addBusLifeCycleManager(bus2);

        sl.register(CXF_ENDPOINT_1);
        sl.register(CXF_ENDPOINT_2);
        sl.register(CXF_ENDPOINT_3);
        sl.register(CXF_ENDPOINT_4);
        replayAll();

        LocatorRegistrar locatorRegistrar = new LocatorRegistrar();
        locatorRegistrar.setServiceLocator(sl);

        locatorRegistrar.registerServer(SERVER_1, bus1);
        locatorRegistrar.registerServer(SERVER_2, bus2);
        locatorRegistrar.registerServer(SERVER_3, bus1);
        locatorRegistrar.registerServer(SERVER_4, bus2);

        verifyAll();
    }

    @Test
    public void endpointUnregisteredWhenServerStops() throws Exception {
        Capture<ServerLifeCycleListener> slclCapture = addServerLifeCycleManager(bus1);
        addBusLifeCycleManager(bus1);

        sl.register(CXF_ENDPOINT_1);
        sl.unregister(CXF_ENDPOINT_1);

        replayAll();

        LocatorRegistrar locatorRegistrar = new LocatorRegistrar();
        locatorRegistrar.setServiceLocator(sl);

        locatorRegistrar.registerServer(SERVER_1, bus1);
        ServerLifeCycleListener listener = slclCapture.getValue();
        listener.stopServer(SERVER_1);

        verifyAll();
    }

    @Test
    public void registerEndpointWithRelativeAddressWhenPrefixSet() throws Exception {
        addServerLifeCycleManager(bus1);
        addBusLifeCycleManager(bus1);

        sl.register(CXF_ENDPOINT_1);
        replayAll();

        LocatorRegistrar locatorRegistrar = new LocatorRegistrar();
        locatorRegistrar.setEndpointPrefix(PREFIX_1);
        locatorRegistrar.setServiceLocator(sl);

        locatorRegistrar.registerServer(REL_SERVER_1, bus1);

        verifyAll();
    }

    @Test
    public void registerEndpointLocatorNull() throws Exception {
        replayAll();

        LocatorRegistrar locatorRegistrar = new LocatorRegistrar();

        try {
            locatorRegistrar.registerServer(SERVER_1, bus1);
            fail("An IllegalStateException should have been thrown.");
        } catch (IllegalStateException e) {

        }

        verifyAll();
    }

    @Test
    public void startListenForServer() throws Exception {
        Capture<ServerLifeCycleListener> slclCapture = addServerLifeCycleManager(bus1);
        addBusLifeCycleManager(bus1);

        List<Server> servers = Collections.emptyList();
        addRegisteredServers(bus1, servers);

        sl.register(CXF_ENDPOINT_1);

        replayAll();

        LocatorRegistrar locatorRegistrar = new LocatorRegistrar();
        locatorRegistrar.setServiceLocator(sl);
        locatorRegistrar.startListenForServers(bus1);

        ServerLifeCycleListener listener = slclCapture.getValue();
        listener.startServer(SERVER_1);

        verifyAll();
    }

    @Test
    public void startListenForServerOnAlreadyUsedBus() throws Exception {
        Capture<ServerLifeCycleListener> slclCapture = addServerLifeCycleManager(bus1);
        addBusLifeCycleManager(bus1);

        List<Server> servers = Collections.emptyList();
        addRegisteredServers(bus1, servers);

        sl.register(CXF_ENDPOINT_1);
        sl.register(CXF_ENDPOINT_2);

        replayAll();

        LocatorRegistrar locatorRegistrar = new LocatorRegistrar();
        locatorRegistrar.setServiceLocator(sl);
        locatorRegistrar.registerServer(SERVER_1, bus1);

        locatorRegistrar.startListenForServers(bus1);

        ServerLifeCycleListener listener = slclCapture.getValue();
        listener.startServer(SERVER_2);

        verifyAll();
    }

    private Capture<ServerLifeCycleListener> addServerLifeCycleManager(Bus bus) {
        Capture<ServerLifeCycleListener> slclCapture = new Capture<ServerLifeCycleListener>();

        ServerLifeCycleManager slcm = createMock(ServerLifeCycleManager.class);
        slcm.registerListener(capture(slclCapture));

        expect(bus.getExtension(ServerLifeCycleManager.class)).andReturn(slcm);

        return slclCapture;
    }

    private Capture<BusLifeCycleListener> addBusLifeCycleManager(Bus bus) {
        Capture<BusLifeCycleListener> slclCapture = new Capture<BusLifeCycleListener>();

        BusLifeCycleManager manager = createMock(BusLifeCycleManager.class);
        manager.registerLifeCycleListener(capture(slclCapture));

        expect(bus.getExtension(BusLifeCycleManager.class)).andReturn(manager);

        return slclCapture;
    }

    private Bus addRegisteredServers(Bus bus, List<Server> registeredServers) {
        ServerRegistry sr = createMock(ServerRegistry.class);
        expect(sr.getServers()).andStubReturn(registeredServers);

        expect(bus.getExtension(ServerRegistry.class)).andStubReturn(sr);
        return null;
    }

}
