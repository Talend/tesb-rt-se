/*
 * #%L
 * Service Locator Client for CXF
 * %%
 * Copyright (C) 2011 - 2012 Talend Inc.
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

import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.zookeeper.CreateMode;
import org.junit.Before;
import org.junit.Test;
import org.talend.esb.servicelocator.client.internal.zk.ZKBackend.NodeMapper;
import org.talend.esb.servicelocator.client.internal.ServiceNode;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.verify;
import static org.easymock.EasyMock.replay;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.talend.esb.servicelocator.TestValues.SERVICE_QNAME_1;
import static org.talend.esb.servicelocator.TestValues.SERVICE_QNAME_2;

public class RootNodeImplTest {
    
    private ZKBackend backend;
    
    private RootNodeImpl rootNode;

    @Before
    public void setup() {
        backend = createMock(ZKBackend.class);
        rootNode = new RootNodeImpl(backend);
    }


    @Test
    public void getServiceNode() {
        ServiceNode node = rootNode.getServiceNode(SERVICE_QNAME_1);
        
        assertEquals(SERVICE_QNAME_1, node.getServiceName());
    }

    @Test
    public void getServiceNames() throws Exception {

        RootNodeImpl eqRootNode = eq(rootNode);
        NodeMapper<QName> anyBinder = anyObject();
        expect(backend.getChildren(eqRootNode, anyBinder)).
            andReturn(Arrays.asList(SERVICE_QNAME_1, SERVICE_QNAME_2));

        replay(backend);
        
        List<QName> serviceNames = rootNode.getServiceNames();

        assertThat(serviceNames, containsInAnyOrder(SERVICE_QNAME_1, SERVICE_QNAME_2));

        verify(backend);
    }

    @Test
    public void existsTrue() throws Exception {
        expect(backend.nodeExists(rootNode)).andReturn(true);
        replay(backend);

        assertTrue(rootNode.exists());
        
        verify(backend);
    }

    @Test
    public void existsFalse() throws Exception {
        expect(backend.nodeExists(rootNode)).andReturn(false);
        replay(backend);

        assertFalse(rootNode.exists());
        
        verify(backend);
    }

    @Test
    public void ensureExists() throws Exception {
        backend.ensurePathExists(rootNode, CreateMode.PERSISTENT);
        replay(backend);

        rootNode.ensureExists();

        verify(backend);
    }
}
