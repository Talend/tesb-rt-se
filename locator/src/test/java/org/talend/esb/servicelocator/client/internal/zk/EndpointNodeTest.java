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
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.talend.esb.servicelocator.TestContent.CONTENT_ANY_1;
import static org.talend.esb.servicelocator.TestValues.ENDPOINT_1;
import static org.talend.esb.servicelocator.TestValues.SERVICE_QNAME_1;
import static org.talend.esb.servicelocator.client.internal.zk.EndpointNodeImpl.LIVE;
import static org.talend.esb.servicelocator.client.internal.zk.EndpointNodeImpl.TIMETOLIVE;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.apache.zookeeper.CreateMode;
import org.junit.Before;
import org.junit.Test;
import org.talend.esb.servicelocator.client.internal.NodePath;

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
        backend.ensurePathDeleted(endpointNode.child(TIMETOLIVE), false);
        replay(backend);

        endpointNode.setLive(true);

        verify(backend);
    }

    @Test
    public void setLiveNonPersistent() throws Exception {
        NodePath livePath = endpointNode.child(LIVE);
        backend.ensurePathExists(livePath, CreateMode.EPHEMERAL);
        backend.ensurePathDeleted(endpointNode.child(TIMETOLIVE), false);
        replay(backend);

        endpointNode.setLive(false);

        verify(backend);
    }

    @Test
    public void setOffline() throws Exception {
        NodePath livePath = endpointNode.child(LIVE);
        NodePath expiryPath = endpointNode.child(TIMETOLIVE);
        backend.ensurePathDeleted(livePath, false);
        backend.ensurePathDeleted(expiryPath, false);
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
        NodePath expiryPath = endpointNode.child(TIMETOLIVE);
        backend.ensurePathDeleted(livePath, false);
        backend.ensurePathDeleted(expiryPath, false);
        backend.ensurePathDeleted(endpointNode, true);
        replay(backend);

        endpointNode.ensureRemoved();

        verify(backend);
    }

    @Test
    public void getExpiryTime() throws Exception {
        final Date expected = new Date();

        NodePath expiryPath = endpointNode.child(TIMETOLIVE);
        expect(backend.nodeExists(expiryPath)).andReturn(true);
        expect(backend.getContent(expiryPath)).andReturn(getDateBytes(expected));
        replay(backend);

        Date answer = endpointNode.getExpiryTime();

        assertEquals(expected, answer);

        verify(backend);
    }

    @Test
    public void getExpiryTimeMissingNode() throws Exception {
        NodePath expiryPath = endpointNode.child(TIMETOLIVE);
        expect(backend.nodeExists(expiryPath)).andReturn(false);
        replay(backend);

        Date answer = endpointNode.getExpiryTime();

        assertNull(answer);

        verify(backend);
    }

    @Test
    public void setExpiryTime() throws Exception {
        final Date expiryTime = new Date();

        NodePath expiryPath = endpointNode.child(TIMETOLIVE);
        backend.ensurePathExists(eq(expiryPath), eq(CreateMode.PERSISTENT), anyObject(byte[].class));
        replay(backend);

        endpointNode.setExpiryTime(expiryTime, true);

        verify(backend);
    }

    private static byte[] getDateBytes(Date date) {
        try {
            return Long.toString(date.getTime()).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
