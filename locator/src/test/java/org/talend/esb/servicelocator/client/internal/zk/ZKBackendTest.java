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

import static java.util.Arrays.asList;
import static org.apache.zookeeper.CreateMode.PERSISTENT;
import static org.easymock.EasyMock.aryEq;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.talend.esb.servicelocator.TestContent.CONTENT_ANY_1;
import static org.talend.esb.servicelocator.TestValues.PASSWORD;
import static org.talend.esb.servicelocator.TestValues.USER_NAME;
import static org.talend.esb.servicelocator.TestValues.USER_NAME_PASSWORD_BYTES;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooDefs.Perms;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.talend.esb.servicelocator.client.ServiceLocatorException;
import org.talend.esb.servicelocator.client.internal.NodePath;
import org.talend.esb.servicelocator.client.internal.RootNode;

public class ZKBackendTest {

    public static final NodePath NODE_PATH = new NodePath("someValue");

    public static final List<ACL> DEFAULT_ACLS;

    static {
        Id readRole = new Id("sl", "SL_READ");
        Id maintainRole = new Id("sl", "SL_MAINTAIN");
        Id adminRole = new Id("sl", "SL_ADMIN");

        ACL readAcl = new ACL(Perms.READ, readRole);
        ACL maintainAcl = new ACL(Perms.READ | Perms.CREATE | Perms.WRITE | Perms.DELETE, maintainRole);
        ACL adminAcl = new ACL(Perms.ALL, adminRole);
        DEFAULT_ACLS = asList(readAcl, maintainAcl, adminAcl);
    }

    private ZooKeeper zkMock;

    private boolean  withAuthentication;

    public static void ignore(String txt) {
    }

    @Before
    public void setUp() throws Exception {
        zkMock = createMock(ZooKeeper.class);
    }

    @Test
    public void connect() throws Exception {
        expect(zkMock.getState()).andStubReturn(ZooKeeper.States.CONNECTED);
        replay(zkMock);

        ZKBackend zkb = createZKBackend(true);

        RootNode rootNode = zkb.connect();

        assertNotNull(rootNode);
        assertTrue(zkb.isConnected());
        verify(zkMock);
    }

    @Test
    public void connectFailing() throws Exception {
        ZKBackend zkb = createZKBackend(false);

        replay(zkMock);

        zkb.setConnectionTimeout(5);

        try {
            zkb.connect();
            fail("A ServiceLocatorException should have been thrown.");
        } catch (ServiceLocatorException e) {
            ignore("Expected exception");
        }

        verify(zkMock);
    }

    @Ignore
    @Test
    public void connectWithCredentialsProvided () throws Exception {
        ZKBackend zkb = createZKBackend();

        zkMock.addAuthInfo(eq("sl"), aryEq(USER_NAME_PASSWORD_BYTES));
        replay(zkMock);

        zkb.setUserName(USER_NAME);
        zkb.setPassword(PASSWORD);
        zkb.connect();

        verify(zkMock);
    }

    @Test
    public void nodeExistsTrue()  throws Exception {
        expect(zkMock.exists(NODE_PATH.toString(), false)).andReturn(new Stat());
        replay(zkMock);
        ZKBackend zkb = createZKBackend();

        zkb.connect();
        boolean exists = zkb.nodeExists(NODE_PATH);

        assertTrue(exists);
        verify(zkMock);
    }

    @Test
    public void nodeExistsFalse()  throws Exception {
        expect(zkMock.exists(NODE_PATH.toString(), false)).andReturn(null);

        ZKBackend zkb = createZKBackend();
        replay(zkMock);

        zkb.connect();
        boolean exists = zkb.nodeExists(NODE_PATH);

        assertFalse(exists);
        verify(zkMock);
    }

    @Test
    public void createNode()  throws Exception {
        expect(zkMock.create(NODE_PATH.toString(), CONTENT_ANY_1, getACLs(), PERSISTENT)).
            andReturn(NODE_PATH.toString());

        ZKBackend zkb = createZKBackend();
        replay(zkMock);

        zkb.connect();
        zkb.createNode(NODE_PATH, PERSISTENT, CONTENT_ANY_1);
        verify(zkMock);
    }

    private ZKBackend createZKBackend()
            throws ServiceLocatorException {
        return createZKBackend(true);
    }

        private ZKBackend createZKBackend(final boolean connectSuccessful)
            throws ServiceLocatorException {
        return new ZKBackend() {
            @Override
            protected ZooKeeper createZooKeeper(CountDownLatch connectionLatch)
                    throws ServiceLocatorException {
                if (connectSuccessful) {
                    connectionLatch.countDown();
                }
                return zkMock;
            }
        };
    }

    List<ACL> getACLs() {
        return withAuthentication ? DEFAULT_ACLS  : Ids.OPEN_ACL_UNSAFE;
    }
}
