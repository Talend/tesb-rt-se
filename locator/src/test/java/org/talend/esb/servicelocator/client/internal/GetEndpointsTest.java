/*
 * #%L
 * Service Locator Client for CXF
 * %%
 * Copyright (c) 2006-2021 Talend Inc. - www.talend.com
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
package org.talend.esb.servicelocator.client.internal;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.talend.esb.servicelocator.TestContent.CONTENT_ENDPOINT_1;
import static org.talend.esb.servicelocator.TestValues.ENDPOINT_1;
import static org.talend.esb.servicelocator.TestValues.LAST_TIME_STARTED;
import static org.talend.esb.servicelocator.TestValues.SERVICE_QNAME_1;

import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.talend.esb.servicelocator.client.SLEndpoint;

public class GetEndpointsTest extends EasyMockSupport {

    private ServiceLocatorBackend backend;

    private RootNode rootNode;

    private ServiceNode serviceNode;

    private EndpointNode endpointNode;

    public static void ignore(String txt) {
    }

    @Before
    public void setUp() throws Exception {
        backend = createMock(ServiceLocatorBackend.class);
        rootNode = createMock(RootNode.class);
        serviceNode = createMock(ServiceNode.class);
        endpointNode = createMock(EndpointNode.class);
    }

    @Test
    public void getEndpointsEndpointIsLive() throws Exception {
        expect(backend.connect()).andReturn(rootNode);
        expect(rootNode.getServiceNode(SERVICE_QNAME_1)).andReturn(serviceNode);
        expect(serviceNode.exists()).andReturn(true);

        expect(serviceNode.getEndPoints()).andReturn(Arrays.asList(endpointNode));
        expect(endpointNode.isLive()).andReturn(true);
        expect(endpointNode.getContent()).andReturn(CONTENT_ENDPOINT_1);

        replayAll();

        ServiceLocatorImpl slc = new ServiceLocatorImpl();
        slc.setBackend(backend);

        List<SLEndpoint> endpoints = slc.getEndpoints(SERVICE_QNAME_1);

        SLEndpoint endpoint = endpoints.get(0);
        assertTrue(endpoint.isLive());
        verifyAll();
    }

    @Test
    public void getEndpointsEndpointIsNotLive() throws Exception {
        expect(backend.connect()).andReturn(rootNode);
        expect(rootNode.getServiceNode(SERVICE_QNAME_1)).andReturn(serviceNode);
        expect(serviceNode.exists()).andReturn(true);

        expect(serviceNode.getEndPoints()).andReturn(Arrays.asList(endpointNode));
        expect(endpointNode.isLive()).andReturn(false);
        expect(endpointNode.getContent()).andReturn(CONTENT_ENDPOINT_1);

        replayAll();

        ServiceLocatorImpl slc = new ServiceLocatorImpl();
        slc.setBackend(backend);

        List<SLEndpoint> endpoints = slc.getEndpoints(SERVICE_QNAME_1);

        SLEndpoint endpoint = endpoints.get(0);
        assertFalse(endpoint.isLive());
        verifyAll();
    }

    @Test
    public void getEndpoint() throws Exception {
        expect(backend.connect()).andReturn(rootNode);
        expect(rootNode.getServiceNode(SERVICE_QNAME_1)).andReturn(serviceNode);

        expect(serviceNode.getEndPoint(ENDPOINT_1)).andReturn(endpointNode);
        expect(endpointNode.exists()).andReturn(true);
        expect(endpointNode.isLive()).andReturn(false);
        expect(endpointNode.getContent()).andReturn(CONTENT_ENDPOINT_1);

        replayAll();

        ServiceLocatorImpl slc = new ServiceLocatorImpl();
        slc.setBackend(backend);

        SLEndpoint endpoint = slc.getEndpoint(SERVICE_QNAME_1, ENDPOINT_1);

        assertFalse(endpoint.isLive());
        assertEquals(SERVICE_QNAME_1, endpoint.forService());
        assertEquals(LAST_TIME_STARTED, endpoint.getLastTimeStarted());
        verifyAll();
    }

    @Test
    public void getEndpointExistsNot() throws Exception {
        expect(backend.connect()).andReturn(rootNode);
        expect(rootNode.getServiceNode(SERVICE_QNAME_1)).andReturn(serviceNode);

        expect(serviceNode.getEndPoint(ENDPOINT_1)).andReturn(endpointNode);
        expect(endpointNode.exists()).andReturn(false);
        replayAll();

        ServiceLocatorImpl slc = new ServiceLocatorImpl();
        slc.setBackend(backend);

        SLEndpoint endpoint = slc.getEndpoint(SERVICE_QNAME_1, ENDPOINT_1);

        assertNull(endpoint);
        verifyAll();
    }
}
