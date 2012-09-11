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
package org.talend.esb.servicelocator.client.internal;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;
import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.easymock.IExpectationSetters;
import org.junit.Before;
import org.talend.esb.DomMother;
import org.talend.esb.servicelocator.client.ServiceLocatorException;
import org.w3c.dom.Document;

import static java.util.Arrays.asList;
import static org.apache.zookeeper.ZooDefs.Perms;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.aryEq;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.talend.esb.servicelocator.TestValues.*;
import static org.talend.esb.servicelocator.client.internal.ServiceLocatorImpl.PostConnectAction;

public class AbstractServiceLocatorImplTest extends EasyMockSupport {

    ZooKeeper zkMock;

    PostConnectAction pcaMock;
    
    Capture<byte[]> contentCapture = new Capture<byte[]>();
    
    boolean withAuthentication;

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
    
    public static void ignore(String txt) {
    }

    List<ACL> getACLs() {
        return withAuthentication ? DEFAULT_ACLS  : Ids.OPEN_ACL_UNSAFE;
    }

    public void setAuthentication(boolean auth) {
        withAuthentication = auth;    
    }
    
    @Before
    public void setUp() throws Exception {
        zkMock = createMock(ZooKeeper.class);
        expect(zkMock.getState()).andStubReturn(ZooKeeper.States.CONNECTED);

        pcaMock = createMock(PostConnectAction.class);
    }

    protected ServiceLocatorImpl createServiceLocatorSuccess() throws InterruptedException,
            ServiceLocatorException {
        return createServiceLocator(true);
    }

    protected ServiceLocatorImpl createServiceLocator(final boolean connectSuccessful)
        throws ServiceLocatorException {
        return new ServiceLocatorImpl() {
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
    
    protected Document capturedContentAsXML() {
        byte[] content = contentCapture.getValue();
        return DomMother.parse(content);
    }

    protected byte[] getContent() { 
        return contentCapture.getValue();
    }

    protected void pathExists(String path) throws KeeperException, InterruptedException {
        expect(zkMock.exists(path, false)).andReturn(new Stat());
    }

    protected void pathExistsNot(String path) throws KeeperException, InterruptedException {
        expect(zkMock.exists(path, false)).andReturn(null);
    }

    protected void pathExists(String path, KeeperException exc) throws KeeperException, InterruptedException {
        expect(zkMock.exists(PathValues.SERVICE_PATH_1, false)).andThrow(exc);
    }

    protected void createNode(String path, CreateMode mode) throws KeeperException, InterruptedException {
        createNode(path, mode, EMPTY_CONTENT);
    }

    protected void createNode(String path, CreateMode mode, byte[] content) throws KeeperException,
            InterruptedException {
        expect(zkMock.create(eq(path), aryEq(content), eq(getACLs()), eq(mode))).andReturn(path);
    }


    protected void createNode(String path, CreateMode mode, KeeperException exc) throws KeeperException,
            InterruptedException {

        IExpectationSetters<String> expectation = expect(zkMock.create(eq(path), aryEq(new byte[0]),
                eq(getACLs()), eq(mode)));

        if (exc != null) {
            expectation.andThrow(exc);
        } else {
            expectation.andReturn(path);
        }
    }

    protected void getChildren(String node, String... children) throws KeeperException, InterruptedException {
        expect(zkMock.getChildren(node, false)).andReturn(Arrays.asList(children));
    }

    protected void getChildren(String node, KeeperException exc) throws KeeperException, InterruptedException {
        expect(zkMock.getChildren(node, false)).andThrow(exc);
    }
    
    protected void getData(String path, byte[] content) throws KeeperException, InterruptedException {
        expect(zkMock.getData(path, false, null)).andReturn(content);
    }

    protected void getContent(String path, byte[] content) throws KeeperException, InterruptedException {
        expect(zkMock.getData(eq(path), eq(false), (Stat) anyObject())).andReturn(content);
    }

    protected void setData(String path, byte[] content) throws KeeperException, InterruptedException {
        expect(zkMock.setData(path, content, -1)).andReturn(new Stat());
    }

    protected void setData(String path) throws KeeperException, InterruptedException {
        expect(zkMock.setData(eq(path), capture(contentCapture), eq(-1))).andReturn(new Stat());
    }

    protected void delete(String node) throws KeeperException, InterruptedException {
        zkMock.delete(node, -1);
    }

    protected void delete(String node, KeeperException exc) throws KeeperException, InterruptedException {
        zkMock.delete(node, -1);
        expectLastCall().andThrow(exc);
    }
}
