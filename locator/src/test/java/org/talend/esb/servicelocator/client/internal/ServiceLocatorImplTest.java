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
package org.talend.esb.servicelocator.client.internal;

import static org.apache.zookeeper.CreateMode.EPHEMERAL;
import static org.apache.zookeeper.CreateMode.PERSISTENT;
import static org.easymock.EasyMock.*;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.collection.IsEmptyIterable.emptyIterable;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.talend.esb.servicelocator.TestValues.*;
import static org.talend.esb.servicelocator.client.internal.ServiceLocatorImpl.PostConnectAction;


import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.xml.namespace.QName;

//import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.easymock.EasyMock;
import org.easymock.IExpectationSetters;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.talend.esb.servicelocator.client.internal.ServiceLocatorException;
import org.talend.esb.servicelocator.client.internal.ServiceLocatorImpl;

public class ServiceLocatorImplTest {

	ZooKeeper zkMock;

	PostConnectAction pcaMock;

	@Before
	public void setup() {
		zkMock = createNiceMock(ZooKeeper.class);
		expect(zkMock.getState()).andStubReturn(ZooKeeper.States.CONNECTED);		

		pcaMock = createMock(PostConnectAction.class);
	}
	
	@Test
	public void connect() throws Exception {
		ServiceLocatorImpl slc = createServiceLocator(true);

		pcaMock.process(slc);
		replay();

		slc.setPostConnectAction(pcaMock);
		slc.connect();

		verify();
	}

	@Test
	public void connectFailing() throws Exception {
		ServiceLocatorImpl slc = createServiceLocator(false);

		replay();

		slc.setConnectionTimeout(10);
		slc.setPostConnectAction(pcaMock);

		try {
			slc.connect();
			fail("A ServiceLocatorException should have been thrown.");
		} catch (ServiceLocatorException e) {
		}

		verify();
	}

	@Test
	public void registerServiceExistsEndPointExists()
			throws Exception {
		pathExists(SERVICE_PATH_1);
		pathExists(ENDPOINT_PATH_11);

		pathExistsNot(ENDPOINT_STATUS_PATH_11);
		createNode(ENDPOINT_STATUS_PATH_11, EPHEMERAL);

		replay();

		ServiceLocatorImpl slc = createServiceLocatorAndConnect();
		slc.register(SERVICE_QNAME_1, ENDPOINT_1);

		verify();
	}

	@Test
	public void registerServiceExistsEndPointExistsNot()
			throws Exception {
		pathExists(SERVICE_PATH_1);

		pathExistsNot(ENDPOINT_PATH_11);
		createNode(ENDPOINT_PATH_11, PERSISTENT);

		pathExistsNot(ENDPOINT_STATUS_PATH_11);
		createNode(ENDPOINT_STATUS_PATH_11, EPHEMERAL);

		replay();

		ServiceLocatorImpl slc = createServiceLocatorAndConnect();
		slc.register(SERVICE_QNAME_1, ENDPOINT_1);

		verify();
	}

	@Test
//	@Ignore
	public void registerServiceExistsEndPointExistsNotWithProperties()
			throws Exception {
		pathExists(SERVICE_PATH_1);

		pathExistsNot(ENDPOINT_PATH_11);
		createNode(ENDPOINT_PATH_11, PERSISTENT, SERIALIZED_PROPERTIES);

		pathExistsNot(ENDPOINT_STATUS_PATH_11);
		createNode(ENDPOINT_STATUS_PATH_11, EPHEMERAL);

		replay();

		ServiceLocatorImpl slc = createServiceLocatorAndConnect();
		slc.register(SERVICE_QNAME_1, ENDPOINT_1, PROPERTIES);

		verify();
	}

	@Test
	public void sucessfulRegisterServiceExistsNot() throws Exception {
		pathExistsNot(SERVICE_PATH_1);
		createNode(SERVICE_PATH_1, PERSISTENT);

		pathExistsNot(ENDPOINT_PATH_11);
		createNode(ENDPOINT_PATH_11, PERSISTENT);

		pathExistsNot(ENDPOINT_STATUS_PATH_11);
		createNode(ENDPOINT_STATUS_PATH_11, EPHEMERAL);

		replay();

		ServiceLocatorImpl slc = createServiceLocatorAndConnect();
		slc.register(SERVICE_QNAME_1, ENDPOINT_1);

		verify();
	}

	@Test
	public void sucessfulRegisterServiceDoesNotExistButConcurrentlyCreatedByOtherClient()
			throws Exception {
		pathExistsNot(SERVICE_PATH_1);
		createNode(SERVICE_PATH_1, PERSISTENT, new KeeperException.NodeExistsException());

		pathExistsNot(ENDPOINT_PATH_11);
		createNode(ENDPOINT_PATH_11, PERSISTENT);

		pathExistsNot(ENDPOINT_STATUS_PATH_11);
		createNode(ENDPOINT_STATUS_PATH_11, EPHEMERAL);

		replay();

		ServiceLocatorImpl slc = createServiceLocatorAndConnect();
		slc.register(SERVICE_QNAME_1, ENDPOINT_1);

		verify();
	}

	@Test
	public void failureWhenRegisteringService() throws Exception {
		pathExists(SERVICE_PATH_1, new KeeperException.RuntimeInconsistencyException());

		replay();

		ServiceLocatorImpl slc = createServiceLocatorAndConnect();

		try {
			slc.register(SERVICE_QNAME_1, ENDPOINT_1);
			fail("A ServiceLocatorException should have been thrown.");
		} catch (ServiceLocatorException e) {
		}

		verify();
	}

	@Test
	public void sucessfulUnregisterEndpoint() throws Exception {
		delete(ENDPOINT_PATH_11);

		replay();

		ServiceLocatorImpl slc = createServiceLocatorAndConnect();
		slc.connect();
		slc.unregister(SERVICE_QNAME_1, ENDPOINT_1);

		verify();
	}

	@Test
	public void unregisterEndpointDeleteFails() throws Exception {
		delete(ENDPOINT_PATH_11, new KeeperException.RuntimeInconsistencyException());
		replay();

		ServiceLocatorImpl slc = createServiceLocatorAndConnect();
		slc.connect();
		try {
			slc.unregister(SERVICE_QNAME_1, ENDPOINT_1);
			fail("A ServiceLocatorException should have been thrown.");
		} catch (ServiceLocatorException e) {
		}

		verify();
	}

	@Test
	public void lookupServiceKnownEndpointsAvailable() throws Exception {
		pathExists(SERVICE_PATH_1);
		getChildren(SERVICE_PATH_1, ENDPOINT_NODE_1, ENDPOINT_NODE_2);

		pathExists(ENDPOINT_STATUS_PATH_11);
		pathExistsNot(ENDPOINT_STATUS_PATH_12);

		
		replay();

		ServiceLocatorImpl slc = createServiceLocatorAndConnect();

		List<String> endpoints = slc.lookup(SERVICE_QNAME_1);

		assertThat(endpoints, hasItem(ENDPOINT_1));
		verify();
	}

	@Test
	public void lookupServiceNotKnown() throws Exception {
		pathExistsNot(SERVICE_PATH_1);
		replay();

		ServiceLocatorImpl slc = createServiceLocatorAndConnect();

		List<String> endpoints = slc.lookup(SERVICE_QNAME_1);

		Matcher<Iterable<String>> emptyStringIterable = emptyIterable();
		assertThat(endpoints, emptyStringIterable);
		verify();
	}

	@Test
	public void getServicesSuccessful() throws Exception {
		getChildren(ServiceLocatorImpl.LOCATOR_ROOT_PATH.toString(),
				SERVICE_NAME_1, SERVICE_NAME_2);
		replay();

		ServiceLocatorImpl slc = createServiceLocatorAndConnect();

		List<QName> services = slc.getServices();

		assertThat(services, containsInAnyOrder(SERVICE_QNAME_1, SERVICE_QNAME_2));
		verify();
	}

	@Test
	public void failureWhenGettingServices() throws Exception {
		getChildren(ServiceLocatorImpl.LOCATOR_ROOT_PATH.toString(), 
				new KeeperException.RuntimeInconsistencyException());
		replay();
		
		ServiceLocatorImpl slc = createServiceLocatorAndConnect();

		try {
			slc.getServices();
			fail("A ServiceLocatorException should have been thrown.");
		} catch (ServiceLocatorException e) {
		}
		verify();
	}

	@Test
	public void getEndpoints() throws Exception {
		pathExists(SERVICE_PATH_1);
		getChildren(SERVICE_PATH_1, ENDPOINT_NODE_1, ENDPOINT_NODE_2);

		replay();

		ServiceLocatorImpl slc = createServiceLocatorAndConnect();

		List<String> endpoints = slc.getEndpoints(SERVICE_QNAME_1);

		assertThat(endpoints, containsInAnyOrder(ENDPOINT_1, ENDPOINT_2));
		verify();
	}

	private ServiceLocatorImpl createServiceLocatorAndConnect() throws InterruptedException, ServiceLocatorException {
		ServiceLocatorImpl slc = createServiceLocator(true);
		slc.connect();

		return slc;
	}

	private ServiceLocatorImpl createServiceLocator(final boolean connectSuccessful) {
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

	private void pathExists(String path) throws KeeperException, InterruptedException {
		expect(zkMock.exists(path, false)).andReturn(new Stat());
	}

	private void pathExistsNot(String path) throws KeeperException, InterruptedException {
		expect(zkMock.exists(path, false)).andReturn(null);
	}
	
	private void pathExists(String path, KeeperException exc) throws KeeperException, InterruptedException {
		expect(zkMock.exists(SERVICE_PATH_1, false)).andThrow(exc);
	}


	private void createNode(String path, CreateMode mode) throws KeeperException, InterruptedException {
		createNode(path, mode, EMPTY_CONTENT);
	}

	private void createNode(String path, CreateMode mode, byte[] content) throws KeeperException, InterruptedException {
		expect(zkMock.create(eq(path), aryEq(content),
				eq(Ids.OPEN_ACL_UNSAFE), eq(mode)))
					.andReturn(path);
	}

	private void createNode(String path, CreateMode mode, KeeperException exc)
	throws KeeperException, InterruptedException {
		IExpectationSetters<String> expectation = expect(zkMock.create(eq(path), aryEq(new byte[0]),
				eq(Ids.OPEN_ACL_UNSAFE), eq(mode)));

		if( exc != null) {
			expectation.andThrow(exc);
		} else {
			expectation.andReturn(path);			
		}
	}

	private void getChildren(String node, String... children)
			throws KeeperException, InterruptedException {
		expect(zkMock.getChildren(node, false)).andReturn(
				Arrays.asList(children));
	}

	private void getChildren(String node, KeeperException exc)
			throws KeeperException, InterruptedException {
		expect(zkMock.getChildren(node, false)).andThrow(exc);
	}

	private void delete(String node)
			throws KeeperException, InterruptedException {
		zkMock.delete(node, -1);
	}

	private void delete(String node, KeeperException exc)
			throws KeeperException, InterruptedException {
		zkMock.delete(node, -1);
		expectLastCall().andThrow(exc);
	}
	
	private void replay() {
		EasyMock.replay(pcaMock, zkMock);
	}

	private void verify() {
		EasyMock.verify(pcaMock, zkMock);
	}
}
