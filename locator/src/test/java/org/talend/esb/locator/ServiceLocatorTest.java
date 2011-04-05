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
package org.talend.esb.locator;

import static org.easymock.EasyMock.*;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.collection.IsEmptyIterable.emptyIterable;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.talend.esb.locator.ServiceLocator.PostConnectAction;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.xml.namespace.QName;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.hamcrest.Matcher;
import org.junit.Test;

public class ServiceLocatorTest {

	public static final QName SERVICE_QNAME_1 = new QName(
			"http://example.com/services", "service1");

	public static final QName SERVICE_QNAME_2 = new QName(
			"http://example.com/services", "service2");

	public static final QName SERVICE_QNAME = SERVICE_QNAME_1;

	public static final String SERVICE_NAME_1 = "{http:%2F%2Fexample.com%2Fservices}service1";
	
	public static final String SERVICE_NAME_2 = "{http:%2F%2Fexample.com%2Fservices}service2";

	public static final String ENDPOINT = "http://example.com/service1";

	public static final String ENDPOINT_1 = ENDPOINT;

	public static final String ENDPOINT_2 = "http://example.com/service2";

	public static final String ENDPOINT_NODE_1 = "http:%2F%2Fexample.com%2Fservice1";

	public static final String ENDPOINT_NODE_2 = "http:%2F%2Fexample.com%2Fservice2";

	public static final String SERVICE_PATH_1 = ServiceLocator.LOCATOR_ROOT_PATH
	+ "/{http:%2F%2Fexample.com%2Fservices}service1";
	
	public static final String SERVICE_PATH_2 = ServiceLocator.LOCATOR_ROOT_PATH
	+ "/{http:%2F%2Fexample.com%2Fservices}service2";

	public static final String SERVICE_PATH = SERVICE_PATH_1;

	public static final String ENDPOINT_PATH = SERVICE_PATH + "/"
			+ ENDPOINT_NODE_1;

	final ZooKeeper zkMock = createMock(ZooKeeper.class);

	PostConnectAction pcaMock = createMock(PostConnectAction.class);

	@Test
	public void successfulConnect() throws Exception {
		ServiceLocator slc = createServiceLocator();

		pcaMock.process(slc);
		replay(pcaMock, zkMock);

		slc.setPostConnectAction(pcaMock);
		slc.connect();

		verify(pcaMock, zkMock);
	}

	@Test
	public void failingConnect() throws Exception {
		ServiceLocator slc = createServiceLocator(false);

		replay(pcaMock);

		slc.setConnectionTimeout(10);
		slc.setPostConnectAction(pcaMock);

		try {
			slc.connect();
			fail("A ServiceLocatorException should have been thrown.");
		} catch (ServiceLocatorException e) {
		}

		verify(pcaMock);
	}

	@Test
	public void sucessfulRegisterServiceExistsEndPointDoesNotExist()
			throws Exception {
		expect(zkMock.exists(SERVICE_PATH, false)).andReturn(new Stat());
		expect(zkMock.exists(ENDPOINT_PATH, false)).andReturn(null);
		expect(
				zkMock.create(eq(ENDPOINT_PATH), aryEq(new byte[0]),
						eq(Ids.OPEN_ACL_UNSAFE), eq(CreateMode.EPHEMERAL)))
				.andReturn(ENDPOINT_PATH);
		expect(zkMock.getState()).andReturn(ZooKeeper.States.CONNECTED);

		replay(zkMock);

		ServiceLocator slc = createServiceLocator();
		slc.connect();
		slc.register(SERVICE_QNAME, ENDPOINT);

		verify(zkMock);
	}

	@Test
	public void sucessfulRegisterAndServiceDoesNotExist() throws Exception {
		expect(zkMock.exists(SERVICE_PATH, false)).andReturn(null);
		expect(
				zkMock.create(eq(SERVICE_PATH), aryEq(new byte[0]),
						eq(Ids.OPEN_ACL_UNSAFE), eq(CreateMode.PERSISTENT)))
				.andReturn(ENDPOINT_PATH);

		expect(zkMock.exists(ENDPOINT_PATH, false)).andReturn(null);
		expect(
				zkMock.create(eq(ENDPOINT_PATH), aryEq(new byte[0]),
						eq(Ids.OPEN_ACL_UNSAFE), eq(CreateMode.EPHEMERAL)))
				.andReturn(ENDPOINT_PATH);
		expect(zkMock.getState()).andReturn(ZooKeeper.States.CONNECTED);

		replay(zkMock);

		ServiceLocator slc = createServiceLocator();
		slc.connect();
		slc.register(SERVICE_QNAME, ENDPOINT);

		verify(zkMock);
	}

	@Test
	public void sucessfulRegisterServiceDoesNotExistButConcurrentlyCreatedByOtherClient()
			throws Exception {
		expect(zkMock.exists(SERVICE_PATH, false)).andReturn(null);
		expect(
				zkMock.create(eq(SERVICE_PATH), aryEq(new byte[0]),
						eq(Ids.OPEN_ACL_UNSAFE), eq(CreateMode.PERSISTENT)))
				.andThrow(new KeeperException.NodeExistsException());

		expect(zkMock.exists(ENDPOINT_PATH, false)).andReturn(null);
		expect(
				zkMock.create(eq(ENDPOINT_PATH), aryEq(new byte[0]),
						eq(Ids.OPEN_ACL_UNSAFE), eq(CreateMode.EPHEMERAL)))
				.andReturn(ENDPOINT_PATH);
		expect(zkMock.getState()).andReturn(ZooKeeper.States.CONNECTED);
		replay(zkMock);

		ServiceLocator slc = createServiceLocator();
		slc.connect();
		slc.register(SERVICE_QNAME, ENDPOINT);

		verify(zkMock);
	}

	@Test
	public void failureWhenRegisteringService() throws Exception {
		expect(zkMock.exists(SERVICE_PATH, false)).andThrow(
				new KeeperException.RuntimeInconsistencyException());
		expect(zkMock.getState()).andReturn(ZooKeeper.States.CONNECTED);

		replay(zkMock);

		ServiceLocator slc = createServiceLocator();
		slc.connect();

		try {
			slc.register(SERVICE_QNAME, ENDPOINT);
			fail("A ServiceLocatorException should have been thrown.");
		} catch (ServiceLocatorException e) {
		}

		verify(zkMock);
	}

	@Test
	public void lookupServiceKnownEndpointsAvailable() throws Exception {
		expect(zkMock.exists(SERVICE_PATH, false)).andReturn(new Stat());
		expect(zkMock.getChildren(SERVICE_PATH, false)).andReturn(
				Arrays.asList(ENDPOINT_NODE_1, ENDPOINT_NODE_2));
		expect(zkMock.getState()).andReturn(ZooKeeper.States.CONNECTED);

		replay(zkMock);

		ServiceLocator slc = createServiceLocator();
		slc.connect();

		List<String> endpoints = slc.lookup(SERVICE_QNAME);

		assertThat(endpoints, containsInAnyOrder(ENDPOINT_1, ENDPOINT_2));
		verify(zkMock);
	}

	@Test
	public void lookupServiceNotKnown() throws Exception {
		expect(zkMock.exists(SERVICE_PATH, false)).andReturn(null);
		expect(zkMock.getState()).andReturn(ZooKeeper.States.CONNECTED);
		replay(zkMock);

		ServiceLocator slc = createServiceLocator();
		slc.connect();

		List<String> endpoints = slc.lookup(SERVICE_QNAME);

		Matcher<Iterable<String>> emptyStringIterable = emptyIterable();
		assertThat(endpoints, emptyStringIterable);
		verify(zkMock);
	}

	@Test
	public void getServicesSuccessful() throws Exception {
		expect(zkMock.getState()).andReturn(ZooKeeper.States.CONNECTED);
		expect(zkMock.getChildren(ServiceLocator.LOCATOR_ROOT_PATH.toString(), false)).andReturn(
				Arrays.asList(SERVICE_NAME_1, SERVICE_NAME_2));
		replay(zkMock);

		ServiceLocator slc = createServiceLocator();
		slc.connect();

		List<QName> services = slc.getServices();

		assertThat(services, containsInAnyOrder(SERVICE_QNAME_1, SERVICE_QNAME_2));
		verify(zkMock);
	}

	@Test
	public void failureWhenGettingServices() throws Exception {
		expect(zkMock.getState()).andReturn(ZooKeeper.States.CONNECTED);
		expect(zkMock.getChildren(ServiceLocator.LOCATOR_ROOT_PATH.toString(), false)).andThrow(
				new KeeperException.RuntimeInconsistencyException());
		replay(zkMock);
		
		ServiceLocator slc = createServiceLocator();
		slc.connect();

		try {
			slc.getServices();
			fail("A ServiceLocatorException should have been thrown.");
		} catch (ServiceLocatorException e) {
		}
	}

	private ServiceLocator createServiceLocator() {
		return createServiceLocator(true);
	}

	private ServiceLocator createServiceLocator(final boolean connectSuccessful) {
		return new ServiceLocator() {
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
}
