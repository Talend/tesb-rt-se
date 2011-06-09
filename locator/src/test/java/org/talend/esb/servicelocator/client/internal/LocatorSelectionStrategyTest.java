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

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isOneOf;
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
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.talend.esb.servicelocator.client.SLPropertiesMatcher;
import org.talend.esb.servicelocator.client.ServiceLocator;
import org.talend.esb.servicelocator.client.ServiceLocatorException;
import org.talend.esb.servicelocator.cxf.internal.DefaultSelectionStrategy;
import org.talend.esb.servicelocator.cxf.internal.EvenDistributionSelectionStrategy;
import org.talend.esb.servicelocator.cxf.internal.RandomSelectionStrategy;

public class LocatorSelectionStrategyTest extends EasyMockSupport {
	
	EvenDistributionSelectionStrategy evenDistributionStrategy;

	RandomSelectionStrategy randomStrategy;

	DefaultSelectionStrategy defaultStrategy;

	ServiceLocator locatorMock;
	
	Exchange exchangeMock;
	
	Endpoint endpointMock;
	
	Service serviceMock;

	@Before
	public void setup() {
		randomStrategy = new RandomSelectionStrategy();
		evenDistributionStrategy = new EvenDistributionSelectionStrategy();
		defaultStrategy = new DefaultSelectionStrategy();
		locatorMock = createNiceMock(ServiceLocator.class);
		randomStrategy.setServiceLocator(locatorMock);
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
		lookup(SERVICE_QNAME_1, ENDPOINT_1,ENDPOINT_2);
		replayAll();
		List<String> result = defaultStrategy.getAlternateAddresses(exchangeMock);
		assertThat(result, containsInAnyOrder(ENDPOINT_1, ENDPOINT_2));
		verifyAll();
	}

	@Test
	public void defaultGetPrimaryAddress() throws Exception {
		lookup(SERVICE_QNAME_1, ENDPOINT_1,ENDPOINT_2);
		replayAll();
		String primary = defaultStrategy.getPrimaryAddress(exchangeMock);
        assertThat(primary, isOneOf(ENDPOINT_1,ENDPOINT_2));
		verifyAll();
	}

	@Test
	public void defaultDistribution() throws Exception {
		lookup(SERVICE_QNAME_1, ENDPOINT_1,ENDPOINT_2);
		replayAll();
		String primary = null;
		String lastPrimary = null;
		boolean distributed = false;
		for (int i = 0; i < 100; i++) {	
			lastPrimary = primary;
			primary = defaultStrategy.getPrimaryAddress(exchangeMock);
	        assertThat(primary, isOneOf(ENDPOINT_1,ENDPOINT_2));
			if (lastPrimary != null && !primary.equals(lastPrimary))
				distributed = true;
		}
		assertThat(distributed, is(false));
		verifyAll();
	}

	@Test
	public void evenGetAlternateAddresses() throws Exception {
		lookup(SERVICE_QNAME_1, ENDPOINT_1,ENDPOINT_2);
		replayAll();
		List<String> result = evenDistributionStrategy.getAlternateAddresses(exchangeMock);
		assertThat(result, containsInAnyOrder(ENDPOINT_1, ENDPOINT_2));
		verifyAll();
	}

	@Test
	public void evenGetPrimaryAddress() throws Exception {
		lookup(SERVICE_QNAME_1, ENDPOINT_1,ENDPOINT_2);
		replayAll();
		String primary = evenDistributionStrategy.getPrimaryAddress(exchangeMock);
        assertThat(primary, isOneOf(ENDPOINT_1,ENDPOINT_2));
		verifyAll();
	}
	
	@Test
	public void evenDistribution() throws Exception {
		evenDistributionStrategy.setReloadAdressesCount(10);
		lookup(SERVICE_QNAME_1, ENDPOINT_1,ENDPOINT_2);
		replayAll();
		String primary = null;
		String lastPrimary = null;
		boolean distributed = false;
		for (int i = 0; i < 2; i++) {	
			lastPrimary = primary;
			primary = evenDistributionStrategy.getPrimaryAddress(exchangeMock);
            assertThat(primary, isOneOf(ENDPOINT_1,ENDPOINT_2));
			if (lastPrimary != null && !primary.equals(lastPrimary))
				distributed = true;
		}
		assertThat(distributed, is(true));
		verifyAll();
	}

	@Test
	public void randomGetAlternateAddresses() throws Exception {
		lookup(SERVICE_QNAME_1, ENDPOINT_1,ENDPOINT_2);
		replayAll();
		List<String> result = randomStrategy.getAlternateAddresses(exchangeMock);
		assertThat(result, containsInAnyOrder(ENDPOINT_1, ENDPOINT_2));
		verifyAll();
	}

	@Test
	public void randomGetPrimaryAddress() throws Exception {
		lookup(SERVICE_QNAME_1, ENDPOINT_1,ENDPOINT_2);
		replayAll();
		String primary = randomStrategy.getPrimaryAddress(exchangeMock);
        assertThat(primary, isOneOf(ENDPOINT_1,ENDPOINT_2));
		verifyAll();
	}
	
	@Test
	public void randomDistribution() throws Exception {
		randomStrategy.setReloadAdressesCount(10);
		for (int i = 0; i < 10; i++)
			lookup(SERVICE_QNAME_1, ENDPOINT_1,ENDPOINT_2);
		replayAll();
		String primary = null;
		String lastPrimary = null;
		boolean distributed = false;
		for (int i = 0; i < 10*10; i++) {	
			lastPrimary = primary;
			primary = randomStrategy.getPrimaryAddress(exchangeMock);
            assertThat(primary, isOneOf(ENDPOINT_1,ENDPOINT_2));
			if (lastPrimary != null && !primary.equals(lastPrimary))
				distributed = true;
		}
		assertThat(distributed, is(true));
		verifyAll();
	}

	private void lookup(QName serviceName, String... adresses) throws ServiceLocatorException, InterruptedException {
		expect(locatorMock.lookup(eq(serviceName), (SLPropertiesMatcher) anyObject())).andReturn(Arrays.asList(adresses));
	}
}
