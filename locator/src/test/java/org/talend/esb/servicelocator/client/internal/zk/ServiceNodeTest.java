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
package org.talend.esb.servicelocator.client.internal.zk;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.talend.esb.servicelocator.TestValues.ENDPOINT_1;
import static org.talend.esb.servicelocator.TestValues.ENDPOINT_2;
import static org.talend.esb.servicelocator.TestValues.SERVICE_QNAME_1;

import java.util.Arrays;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.junit.Before;
import org.junit.Test;
import org.talend.esb.servicelocator.client.internal.EndpointNode;
import org.talend.esb.servicelocator.client.internal.zk.ZKBackend.NodeMapper;

public class ServiceNodeTest {

    private ZKBackend backend;

    private RootNodeImpl rootNode;

    private ServiceNodeImpl serviceNode;

    @Before
    public void setup() {
        backend = createMock(ZKBackend.class);
        rootNode = new RootNodeImpl(backend);
        serviceNode = new ServiceNodeImpl(backend, rootNode, SERVICE_QNAME_1);
    }

    @Test
    public void getServiceName() {

        assertThat(serviceNode.getServiceName(), equalTo(SERVICE_QNAME_1));
    }

    @Test
    public void existsTrue() throws Exception {
        expect(backend.nodeExists(serviceNode)).andReturn(true);
        replay(backend);

        assertTrue(serviceNode.exists());

        verify(backend);
    }

    @Test
    public void existsFalse() throws Exception {
        expect(backend.nodeExists(serviceNode)).andReturn(false);
        replay(backend);

        assertFalse(serviceNode.exists());

        verify(backend);
    }

    @Test
    public void ensureExists() throws Exception {
        backend.ensurePathExists(serviceNode, CreateMode.PERSISTENT);
        replay(backend);

        serviceNode.ensureExists();

        verify(backend);
    }

    @Test
    public void getEndpointNames() throws Exception {
        ServiceNodeImpl eqServiceNode = eq(serviceNode);
        NodeMapper<String> anyMapper = anyObject();

        expect(backend.getChildren(eqServiceNode, anyMapper)).
            andReturn(Arrays.asList(ENDPOINT_1, ENDPOINT_2));
        replay(backend);

        List<String> endpointNames = serviceNode.getEndpointNames();
        assertThat(endpointNames, containsInAnyOrder(ENDPOINT_1, ENDPOINT_2));

        verify(backend);
    }

    @Test
    public void getEndpoint() throws Exception {
        ServiceNodeImpl serviceNode = new ServiceNodeImpl(backend, rootNode, SERVICE_QNAME_1);

        EndpointNode endpointNode = serviceNode.getEndPoint(ENDPOINT_1);

        assertThat(endpointNode.getEndpointName(), equalTo(ENDPOINT_1));

    }

    @Test
    public void getEndpoints() throws Exception {
        EndpointNode endpointNode1 = new EndpointNodeImpl(backend,serviceNode, ENDPOINT_1);
        EndpointNode endpointNode2 = new EndpointNodeImpl(backend,serviceNode, ENDPOINT_2);

        ServiceNodeImpl eqServiceNode = eq(serviceNode);
        NodeMapper<EndpointNode> anyMapper = anyObject();
        expect(backend.getChildren(eqServiceNode, anyMapper)).
            andReturn(Arrays.asList(endpointNode1, endpointNode2));
        replay(backend);

        List<EndpointNode> endpoints = serviceNode.getEndPoints();
        assertThat(endpoints, containsInAnyOrder(endpointNode1, endpointNode2));

        verify(backend);
    }

    @Test
    public void ensureRemoved() throws Exception {
        backend.ensurePathDeleted(serviceNode, true);
        replay(backend);

        serviceNode.ensureRemoved();

        verify(backend);
    }
}
