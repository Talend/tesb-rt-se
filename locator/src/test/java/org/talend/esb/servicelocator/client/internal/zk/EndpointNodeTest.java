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

import org.apache.zookeeper.CreateMode;
import org.junit.Before;
import org.junit.Test;
import org.talend.esb.servicelocator.client.internal.NodePath;
import org.talend.esb.servicelocator.client.internal.zk.RootNodeImpl;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import static org.talend.esb.servicelocator.TestValues.ENDPOINT_1;
import static org.talend.esb.servicelocator.TestContent.CONTENT_ANY_1;
import static org.talend.esb.servicelocator.TestValues.SERVICE_QNAME_1;
import static org.talend.esb.servicelocator.client.internal.zk.EndpointNodeImpl.LIVE;

public class EndpointNodeTest {
    
    private ZKBackend backend = createMock(ZKBackend.class);

    private RootNodeImpl rootNode = new RootNodeImpl(backend);

    private ServiceNodeImpl serviceNode;

    private EndpointNodeImpl endpointNode;

    @Before
    public void setup() {
        backend = createMock(ZKBackend.class);
        rootNode = new RootNodeImpl(backend);
        serviceNode = new ServiceNodeImpl(backend, rootNode, SERVICE_QNAME_1);
        endpointNode = new EndpointNodeImpl(backend,serviceNode, ENDPOINT_1);
    }
    
    @Test
    public void getEndpointName() {
        assertThat(endpointNode.getEndpointName(), equalTo(ENDPOINT_1));
    }

    @Test
    public void existsTrue() throws Exception {
        expect(backend.nodeExists(endpointNode)).andReturn(true);
        replay(backend);

        assertTrue(endpointNode.exists());
        
        verify(backend);
    }

    @Test
    public void existsFalse() throws Exception {
        expect(backend.nodeExists(endpointNode)).andReturn(false);
        replay(backend);

        assertFalse(endpointNode.exists());
        
        verify(backend);
    }

    @Test
    public void ensureExists() throws Exception {
        backend.ensurePathExists(endpointNode, CreateMode.PERSISTENT, CONTENT_ANY_1);
        replay(backend);
        
        endpointNode.ensureExists(CONTENT_ANY_1);

        verify(backend);
    }

    @Test
    public void setLivePersistent() throws Exception {
        NodePath livePath = endpointNode.child(LIVE);
        backend.ensurePathExists(livePath, CreateMode.PERSISTENT);
        replay(backend);
        
        endpointNode.setLive(true);

        verify(backend);
    }

    @Test
    public void setLiveNonPersistent() throws Exception {
        NodePath livePath = endpointNode.child(LIVE);
        backend.ensurePathExists(livePath, CreateMode.EPHEMERAL);
        replay(backend);
        
        endpointNode.setLive(false);

        verify(backend);
    }

    @Test
    public void setOffline() throws Exception {
        NodePath livePath = endpointNode.child(LIVE);
        backend.ensurePathDeleted(livePath, false);
        replay(backend);
        
        endpointNode.setOffline();

        verify(backend);
    }

    @Test
    public void getContent() throws Exception {
        expect(backend.getContent(endpointNode)).andReturn(CONTENT_ANY_1);
        replay(backend);
        
        byte[] content = endpointNode.getContent();

        assertThat(content, equalTo(CONTENT_ANY_1));

        verify(backend);
    }

    @Test
    public void setContent() throws Exception {
        backend.setNodeData(endpointNode, CONTENT_ANY_1);
        replay(backend);
        
        endpointNode.setContent(CONTENT_ANY_1);

        verify(backend);
    }

    @Test
    public void isLiveTrue() throws Exception {
        NodePath livePath = endpointNode.child(LIVE);
        expect(backend.nodeExists(livePath)).andReturn(true);
        replay(backend);
        
        assertTrue(endpointNode.isLive());

        verify(backend);
    }

    @Test
    public void isLiveFalse() throws Exception {
        NodePath livePath = endpointNode.child(LIVE);
        expect(backend.nodeExists(livePath)).andReturn(false);
        replay(backend);
        
        assertFalse(endpointNode.isLive());

        verify(backend);
    }

    @Test
    public void ensureRemoved() throws Exception {
        NodePath livePath = endpointNode.child(LIVE);
        backend.ensurePathDeleted(livePath, false);
        backend.ensurePathDeleted(endpointNode, true);
        replay(backend);
        
        endpointNode.ensureRemoved();

        verify(backend);
    }
}
