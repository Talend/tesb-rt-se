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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.talend.esb.servicelocator.TestValues.SERVICE_QNAME_1;
import static org.talend.esb.servicelocator.TestValues.SERVICE_QNAME_2;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.zookeeper.CreateMode;
import org.junit.Before;
import org.junit.Test;
import org.talend.esb.servicelocator.client.internal.ServiceNode;
import org.talend.esb.servicelocator.client.internal.zk.ZKBackend.NodeMapper;

public class RootNodeImplTest {

    public static final String V_5_1 = "5.1";

    public static final String V_5_2_0 = "5.2.0";

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

    @Test
    public void isAuthenticationEnabledTrue() throws Exception {
        expect(backend.nodeExists(rootNode)).andReturn(true);
        expect(backend.getContent(rootNode)).andReturn(getData(V_5_2_0, true));
        replay(backend);

        assertTrue(rootNode.isAuthenticationEnabled());

        verify(backend);
    }

    @Test
    public void isAuthenticationEnabledFalse() throws Exception {
        expect(backend.nodeExists(rootNode)).andReturn(true);
        expect(backend.getContent(rootNode)).andReturn(getData(V_5_2_0, false));
        replay(backend);

        assertFalse(rootNode.isAuthenticationEnabled());

        verify(backend);
    }

    @Test
    public void isAuthenticationEnabledNoContent() throws Exception {
        expect(backend.nodeExists(rootNode)).andReturn(true);
        expect(backend.getContent(rootNode)).andReturn(new byte[0]);
        replay(backend);

        assertFalse(rootNode.isAuthenticationEnabled());

        verify(backend);
    }

    @Test
    public void getVersion() throws Exception {
        expect(backend.nodeExists(rootNode)).andReturn(true);
        expect(backend.getContent(rootNode)).andReturn(getData(V_5_2_0, true));
        replay(backend);

        String version = rootNode.getVersion();

        assertThat(version, equalTo(V_5_2_0));

        verify(backend);
    }

    @Test
    public void getVersionOtherValue() throws Exception {
        expect(backend.nodeExists(rootNode)).andReturn(true);
        expect(backend.getContent(rootNode)).andReturn(getData(V_5_1, true));
        replay(backend);

        String version = rootNode.getVersion();

        assertThat(version, equalTo(V_5_1));

        verify(backend);
    }

    @Test
    public void getVersionNoContent() throws Exception {
        expect(backend.nodeExists(rootNode)).andReturn(true);
        expect(backend.getContent(rootNode)).andReturn(new byte[0]);
        replay(backend);

        String version = rootNode.getVersion();

        assertThat(version, equalTo(V_5_1));

        verify(backend);
    }

    @Test
    public void contentOnlyRetrievedOnce() throws Exception {
        expect(backend.nodeExists(rootNode)).andReturn(true);
        expect(backend.getContent(rootNode)).andReturn(getData(V_5_1, false));
        replay(backend);

        String version = rootNode.getVersion();
        assertFalse(rootNode.isAuthenticationEnabled());
        assertThat(version, equalTo(V_5_1));

        verify(backend);
    }

    @Test
    public void retrieveContentNodeDoesntExist() throws Exception {
        expect(backend.nodeExists(rootNode)).andReturn(false);
        replay(backend);

        String version = rootNode.getVersion();
        assertFalse(rootNode.isAuthenticationEnabled());
        assertThat(version, equalTo(V_5_1));

        verify(backend);
    }

    private byte[] getData(String version, boolean authenticated) throws UnsupportedEncodingException {
        String combined = version + "," + Boolean.toString(authenticated);
        return combined.getBytes("utf-8");
    }
}
