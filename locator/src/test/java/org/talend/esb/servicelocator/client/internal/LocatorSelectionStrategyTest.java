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

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.collection.IsArrayContaining.hasItemInArray;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.talend.esb.servicelocator.TestValues.ENDPOINT_1;
import static org.talend.esb.servicelocator.TestValues.ENDPOINT_2;
import static org.talend.esb.servicelocator.TestValues.SERVICE_QNAME_1;

import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.service.Service;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.talend.esb.servicelocator.client.ServiceLocator;
import org.talend.esb.servicelocator.client.ServiceLocatorException;
import org.talend.esb.servicelocator.cxf.internal.DefaultSelectionStrategy;
import org.talend.esb.servicelocator.cxf.internal.EvenDistributionSelectionStrategy;

public class LocatorSelectionStrategyTest {
	
	EvenDistributionSelectionStrategy evenDistributionStrategy;

	DefaultSelectionStrategy defaultStrategy;

	ServiceLocator locatorMock;
	
	Exchange exchangeMock;
	
	Endpoint endpointMock;
	
	Service serviceMock;

	@Before
	public void setup() {
		evenDistributionStrategy = new EvenDistributionSelectionStrategy();
		defaultStrategy = new DefaultSelectionStrategy();
		locatorMock = createNiceMock(ServiceLocator.class);
		evenDistributionStrategy.setServiceLocator(locatorMock);
		defaultStrategy.setServiceLocator(locatorMock);
		serviceMock = createNiceMock(Service.class);
		expect(serviceMock.getName()).andStubReturn(SERVICE_QNAME_1);		
		endpointMock = createNiceMock(Endpoint.class);
		expect(endpointMock.getService()).andStubReturn(serviceMock);				
		exchangeMock = createNiceMock(Exchange.class);
		expect(exchangeMock.getEndpoint()).andStubReturn(endpointMock);		
	}
	
	@Test
	public void defaultGetAlternateAddresses() throws Exception {
		String[] adresses = new String[]{ENDPOINT_1,ENDPOINT_2};
		lookup(SERVICE_QNAME_1, adresses);
		replay();
		List<String> result = defaultStrategy.getAlternateAddresses(exchangeMock);
		assertThat(result, containsInAnyOrder(ENDPOINT_1, ENDPOINT_2));
		verify();
	}

	@Test
	public void defaultGetPrimaryAddress() throws Exception {
		String[] adresses = new String[]{ENDPOINT_1,ENDPOINT_2};
		lookup(SERVICE_QNAME_1, adresses);
		replay();
		String primary = defaultStrategy.getPrimaryAddress(exchangeMock);
		assertThat(adresses, hasItemInArray(primary));
		verify();
	}

	@Test
	public void defaultDistribution() throws Exception {
		String[] adresses = new String[]{ENDPOINT_1,ENDPOINT_2};
		lookup(SERVICE_QNAME_1, adresses);
		replay();
		String primary = null;
		String lastPrimary = null;
		boolean distributed = false;
		for (int i = 0; i < 100; i++) {	
			lastPrimary = primary;
			primary = defaultStrategy.getPrimaryAddress(exchangeMock);
			assertThat(adresses, hasItemInArray(primary));
			if (lastPrimary != null && !primary.equals(lastPrimary))
				distributed = true;
		}
		assertThat(distributed, is(false));
		verify();
	}

	@Test
	public void evenGetAlternateAddresses() throws Exception {
		String[] adresses = new String[]{ENDPOINT_1,ENDPOINT_2};
		lookup(SERVICE_QNAME_1, adresses);
		replay();
		List<String> result = evenDistributionStrategy.getAlternateAddresses(exchangeMock);
		assertThat(result, containsInAnyOrder(ENDPOINT_1, ENDPOINT_2));
		verify();
	}

	@Test
	public void evenGetPrimaryAddress() throws Exception {
		String[] adresses = new String[]{ENDPOINT_1,ENDPOINT_2};
		lookup(SERVICE_QNAME_1, adresses);
		replay();
		String primary = evenDistributionStrategy.getPrimaryAddress(exchangeMock);
		assertThat(adresses, hasItemInArray(primary));
		verify();
	}
	
	@Test
	public void evenDistribution() throws Exception {
		String[] adresses = new String[]{ENDPOINT_1,ENDPOINT_2};
		evenDistributionStrategy.setReloadAdressesCount(10);
		for (int i = 0; i < 10; i++)
			lookup(SERVICE_QNAME_1, adresses);
		replay();
		String primary = null;
		String lastPrimary = null;
		boolean distributed = false;
		for (int i = 0; i < 10*10; i++) {	
			lastPrimary = primary;
			primary = evenDistributionStrategy.getPrimaryAddress(exchangeMock);
			assertThat(adresses, hasItemInArray(primary));
			if (lastPrimary != null && !primary.equals(lastPrimary))
				distributed = true;
		}
		assertThat(distributed, is(true));
		verify();
	}

	private void lookup(QName serviceName, String[] adresses) throws ServiceLocatorException, InterruptedException {
		expect(locatorMock.lookup(eq(serviceName))).andReturn(Arrays.asList(adresses));
	}

	private void replay() {
		EasyMock.replay(locatorMock);
		EasyMock.replay(exchangeMock);
		EasyMock.replay(endpointMock);
		EasyMock.replay(serviceMock);
	}

	private void verify() {
		EasyMock.verify(locatorMock);
		EasyMock.verify(exchangeMock);
		EasyMock.verify(endpointMock);
		EasyMock.verify(serviceMock);
	}

}
