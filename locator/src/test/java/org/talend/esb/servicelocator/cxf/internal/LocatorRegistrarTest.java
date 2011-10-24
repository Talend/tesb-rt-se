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

import javax.xml.namespace.QName;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.endpoint.ServerLifeCycleListener;
import org.apache.cxf.endpoint.ServerLifeCycleManager;
import org.apache.cxf.endpoint.ServerRegistry;
import org.easymock.Capture;
import org.easymock.CaptureType;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.talend.esb.servicelocator.client.Endpoint;
import org.talend.esb.servicelocator.client.SLEndpoint;
import org.talend.esb.servicelocator.client.ServiceLocator;
import org.talend.esb.servicelocator.client.ServiceLocator.PostConnectAction;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.talend.esb.servicelocator.TestValues.*;
import static org.talend.esb.servicelocator.cxf.internal.CXFTestStubs.SERVER_1;
import static org.talend.esb.servicelocator.cxf.internal.CXFTestStubs.SERVER_2;

public class LocatorRegistrarTest extends EasyMockSupport {

    public static final String PREFIX = "prefix";

    private ServiceLocator sl = createMock(ServiceLocator.class);
    
    private Capture<Endpoint> eppCapture = new Capture<Endpoint>(CaptureType.ALL);

    private SLEndpoint oldEndointContent;

    @Before
    public void startUp() {
        oldEndointContent = createMock(SLEndpoint.class);
        expect(oldEndointContent.getLastTimeStopped()).andStubReturn(LAST_TIME_STOPPED);

    }

    @Test
    public void postConnectActionRegistered() {
        sl.setPostConnectAction((PostConnectAction) anyObject());
        replayAll();

        LocatorRegistrar locatorRegistrar = new LocatorRegistrar();
        locatorRegistrar.setServiceLocator(sl);

        verifyAll();
    }

    @Test
    public void registerEndpoint() throws Exception {
        sl.setPostConnectAction((PostConnectAction) anyObject());
        sl.register(capture(eppCapture));
        expect(sl.getEndpoint(SERVICE_QNAME_1, ENDPOINT_1)).andReturn(oldEndointContent);
        replayAll();

        LocatorRegistrar locatorRegistrar = new LocatorRegistrar();
        locatorRegistrar.setServiceLocator(sl);

        locatorRegistrar.registerServer(SERVER_1);

        Endpoint epp = eppCapture.getValue();
        assertEquals(SERVICE_QNAME_1, epp.getServiceName());
        assertEquals(ENDPOINT_1, epp.getAddress());
        assertEquals(LAST_TIME_STOPPED, epp.getLastTimeStopped());
        verifyAll();
    }

    @Test
    public void registerEndpointNotYetInSL() throws Exception {
        sl.setPostConnectAction((PostConnectAction) anyObject());
        sl.register(capture(eppCapture));
        expect(sl.getEndpoint(SERVICE_QNAME_1, ENDPOINT_1)).andReturn(null);
        replayAll();

        LocatorRegistrar locatorRegistrar = new LocatorRegistrar();
        locatorRegistrar.setServiceLocator(sl);

        locatorRegistrar.registerServer(SERVER_1);

        Endpoint epp = eppCapture.getValue();
        assertEquals(SERVICE_QNAME_1, epp.getServiceName());
        assertEquals(ENDPOINT_1, epp.getAddress());
        assertEquals(-1, epp.getLastTimeStopped());
        verifyAll();
    }

    @Test
    public void registerEndpointWithPrefixSet() throws Exception {
        sl.setPostConnectAction((PostConnectAction) anyObject());
        sl.register(capture(eppCapture));
        expect(sl.getEndpoint(SERVICE_QNAME_1, /*PREFIX +*/ ENDPOINT_1)).andReturn(oldEndointContent);
        replayAll();

        LocatorRegistrar locatorRegistrar = new LocatorRegistrar();
        locatorRegistrar.setEndpointPrefix(PREFIX);
        locatorRegistrar.setServiceLocator(sl);

        locatorRegistrar.registerServer(SERVER_1);

        Endpoint epp = eppCapture.getValue();
        assertEquals(SERVICE_QNAME_1, epp.getServiceName());
        assertEquals(/*PREFIX +*/ ENDPOINT_1, epp.getAddress());

        verifyAll();
    }

    @Test
    public void registerEndpointLocatorNull() throws Exception {
        replayAll();

        LocatorRegistrar locatorRegistrar = new LocatorRegistrar();

        try {
            locatorRegistrar.registerServer(SERVER_1);
            fail("An IllegalStateException should have been thrown.");
        } catch (IllegalStateException e) {

        }

        verifyAll();
    }

    @Test
    public void registerServerLifeCycleListener() {
        ServerLifeCycleManager slcm = createMock(ServerLifeCycleManager.class);
        slcm.registerListener((ServerLifeCycleListener) anyObject());
        Bus bus = createMock(Bus.class);
        expect(bus.getExtension(ServerLifeCycleManager.class)).andStubReturn(slcm);
        EasyMock.replay(slcm, bus);

        LocatorRegistrar locatorRegistrar = new LocatorRegistrar();
        locatorRegistrar.setBus(bus);

        EasyMock.verify(slcm, bus);
    }

    @Test
    public void startListenForServer() throws Exception {
        Bus bus = createMock(Bus.class);

        Capture<ServerLifeCycleListener> slclCapture = addServerLifeCycleManager(bus);

        List<Server> servers = Collections.emptyList();
        addRegisteredServers(bus, servers);

        sl.setPostConnectAction((PostConnectAction) anyObject());
        expect(sl.getEndpoint(SERVICE_QNAME_1, ENDPOINT_1)).andReturn(oldEndointContent);
        sl.register(capture(eppCapture));

        replayAll();

        LocatorRegistrar locatorRegistrar = new LocatorRegistrar();
        locatorRegistrar.setBus(bus);
        locatorRegistrar.setServiceLocator(sl);
        locatorRegistrar.startListenForServers();

        ServerLifeCycleListener listener = slclCapture.getValue();
        listener.startServer(SERVER_1);

        verifyAll();
    }

    @Test
    public void ignoreStartedServersIfNotStartListenForServerCalled() throws Exception {
        Bus bus = createMock(Bus.class);

        Capture<ServerLifeCycleListener> slclCapture = addServerLifeCycleManager(bus);

        List<Server> servers = Collections.emptyList();
        addRegisteredServers(bus, servers);

        sl.setPostConnectAction((PostConnectAction) anyObject());

        replayAll();

        LocatorRegistrar locatorRegistrar = new LocatorRegistrar();
        locatorRegistrar.setBus(bus);
        locatorRegistrar.setServiceLocator(sl);

        ServerLifeCycleListener listener = slclCapture.getValue();
        listener.startServer(SERVER_1);

        verifyAll();
    }

    @Test
    public void serverStopsThenEndpointUnregistered() throws Exception {
        sl.setPostConnectAction((PostConnectAction) anyObject());
        expect(sl.getEndpoint(SERVICE_QNAME_1, ENDPOINT_1)).andReturn(oldEndointContent);
        sl.register(capture(eppCapture));
        sl.unregister((Endpoint)anyObject());

        replayAll();

        LocatorRegistrar locatorRegistrar = new LocatorRegistrar();
        locatorRegistrar.setServiceLocator(sl);

        locatorRegistrar.registerServer(SERVER_1);
        locatorRegistrar.stopServer(SERVER_1);

        verifyAll();
    }

    @Test
    public void serverStopsIfNotRegisteredBeforeDoNothing() throws Exception {
        sl.setPostConnectAction((PostConnectAction) anyObject());

        replayAll();

        LocatorRegistrar locatorRegistrar = new LocatorRegistrar();
        locatorRegistrar.setServiceLocator(sl);

        locatorRegistrar.stopServer(SERVER_1);

        verifyAll();
    }

    @Test
    public void processReregisterAllEndpoints() throws Exception {

        sl.setPostConnectAction((PostConnectAction) anyObject());
        expect(sl.getEndpoint(SERVICE_QNAME_1, ENDPOINT_1)).andReturn(oldEndointContent);
        expect(sl.getEndpoint(SERVICE_QNAME_2, ENDPOINT_2)).andReturn(oldEndointContent);

        sl.register(capture(eppCapture));
        expectLastCall().times(4);
        replayAll();

        LocatorRegistrar locatorRegistrar = new LocatorRegistrar();
        locatorRegistrar.setServiceLocator(sl);

        locatorRegistrar.registerServer(SERVER_1);
        locatorRegistrar.registerServer(SERVER_2);

        locatorRegistrar.process(sl);

        List<Endpoint> epps = eppCapture.getValues();
        assertHasValues(SERVICE_QNAME_1, ENDPOINT_1, epps.get(0));
        assertHasValues(SERVICE_QNAME_2, ENDPOINT_2, epps.get(1));
        assertHasValues(SERVICE_QNAME_1, ENDPOINT_1, epps.get(2));
        assertHasValues(SERVICE_QNAME_2, ENDPOINT_2, epps.get(3));

        verifyAll();
    }

    private static void assertHasValues(QName serviceName, String address, Endpoint epp) {
        assertEquals(serviceName, epp.getServiceName());
        assertEquals(address, epp.getAddress());
    }
    
    private Capture<ServerLifeCycleListener> addServerLifeCycleManager(Bus bus) {
        Capture<ServerLifeCycleListener> slclCapture = new Capture<ServerLifeCycleListener>();

        ServerLifeCycleManager slcm = createMock(ServerLifeCycleManager.class);
        slcm.registerListener(capture(slclCapture));

        expect(bus.getExtension(ServerLifeCycleManager.class)).andStubReturn(slcm);

        return slclCapture;
    }

    private Bus addRegisteredServers(Bus bus, List<Server> registeredServers) {
        ServerRegistry sr = createMock(ServerRegistry.class);
        expect(sr.getServers()).andStubReturn(registeredServers);

        expect(bus.getExtension(ServerRegistry.class)).andStubReturn(sr);
        return null;
    }
}
