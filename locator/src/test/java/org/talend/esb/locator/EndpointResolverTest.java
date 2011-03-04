/*******************************************************************************
*
* Copyright (c) 2011 Talend Inc. - www.talend.com
* All rights reserved.
*
* This program and the accompanying materials are made available
* under the terms of the Apache License v2.0
* which accompanies this distribution, and is available at
* http://www.apache.org/licenses/LICENSE-2.0
*
*******************************************************************************/
package org.talend.esb.locator;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.xml.namespace.QName;

import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;

public class EndpointResolverTest {

	public static final QName SERVICE_NAME = new QName(
			"http://example.com/services", "service1");
	public static final String LOCATOR_ENDPOINTS = "localhost:2181";

	public static final String ENDPOINT = "http://example.com/service1";
	public static final String ENDPOINT_1 = ENDPOINT;
	public static final String ENDPOINT_2 = "http://example.com/service2";
	public static final String ENDPOINT_NODE_1 = "http:%2F%2Fexample.com%2Fservice1";
	public static final String ENDPOINT_NODE_2 = "http:%2F%2Fexample.com%2Fservice2";
	public static final String SERVICE_PATH = ServiceLocator.LOCATOR_ROOT_PATH
			+ "/{http:%2F%2Fexample.com%2Fservices}service1";
	public static final String ENDPOINT_PATH = SERVICE_PATH + "/"
			+ ENDPOINT_NODE_1;

	final ZooKeeper zkMock = createMock(ZooKeeper.class);

	@Test
	public void testDummy() throws Exception {
	}
	
/*
	@Test
	public void testEndpointResolver() throws Exception {
		EndpointResolver er = createEndpointResolver();
		assertNotNull(er);
	}

	@Test
	public void testEndpointResolverNullParameters() {
		try {
			createEndpointResolver(null, null);
			fail("NullPointerException should be thrown");
		} catch (NullPointerException e) {
		}

		try {
			createEndpointResolver(SERVICE_NAME, null);
			fail("NullPointerException should be thrown");
		} catch (NullPointerException e) {
		}

		try {
			createEndpointResolver(null, LOCATOR_ENDPOINTS);
			fail("NullPointerException should be thrown");
		} catch (NullPointerException e) {
		}
	}

	@Test
	public void testSelectEndpoint() {
		EndpointResolver er = createEndpointResolver();
		String endpoint = er.selectEndpoint();
		assertTrue(er.getEndpointsList().contains(endpoint));
	}

	@Test
	public void testGetEndpointsList() {
		EndpointResolver er = createEndpointResolver();
		assertEquals(er.getEndpointsList().get(0), ENDPOINT_1);
		assertEquals(er.getEndpointsList().get(1), ENDPOINT_2);
	}

	@Test
	public void testGetServiceName() {
		EndpointResolver er = createEndpointResolver();
		assertEquals(er.getServiceName(), SERVICE_NAME);
	}

	private EndpointResolver createEndpointResolver() {
		return createEndpointResolver(SERVICE_NAME, LOCATOR_ENDPOINTS);
	}

	private EndpointResolver createEndpointResolver(QName serviceName,
			String locatorEndpoints) {
		try {
			return new EndpointResolver(serviceName, locatorEndpoints) {

				@Override
				protected ServiceLocator createServiceLocator() {
					return new ServiceLocator() {

						@Override
						protected ZooKeeper createZooKeeper(
								CountDownLatch connectionLatch)
								throws IOException {
							connectionLatch.countDown();
							return zkMock;
						}

						public List<String> lookup(QName serviceName) {
							List<String> listOfEndpoints = new ArrayList<String>();

							listOfEndpoints.add(ENDPOINT_1);
							listOfEndpoints.add(ENDPOINT_2);

							return listOfEndpoints;
						}
					};
				}
			};
		} catch (ServiceLocatorException e) {
		} catch (InterruptedException e) {
		} catch (IOException e) {
		}
		return null;
	}
*/
}