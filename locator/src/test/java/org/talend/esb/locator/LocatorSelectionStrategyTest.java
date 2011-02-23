package org.talend.esb.locator;

import static org.easymock.EasyMock.*;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.service.Service;
import org.hamcrest.Matcher;
import org.junit.Test;

public class LocatorSelectionStrategyTest {
	
	public static final QName SERVICE_NAME = new QName("nsp", "name");

	public static final String ENDPOINT_1 = "endpoint1";

	public static final String ENDPOINT_2 = "endpoint2";

	public static final String ENDPOINT_3 = "endpoint3";
	
	public static final List<String> ENDPOINTS = 
		Arrays.asList(ENDPOINT_1, ENDPOINT_2, ENDPOINT_3);
	
	public static final Matcher<Collection<String>> EMPTY_ENDPOINT_COLLECTION = empty();

	@Test
	public void getAlternateAddressesReturnsListFromLocator() throws Exception {
		
		ServiceLocator sl = createMock(ServiceLocator.class);
		Exchange exchange = createExchangeMock(SERVICE_NAME);

		expect(sl.lookup(SERVICE_NAME)).andReturn(ENDPOINTS);
		replay(sl);
		
		LocatorSelectionStrategy strategy = new LocatorSelectionStrategy();
		strategy.setServiceLocator(sl);
		
		List<String> alternates = strategy.getAlternateAddresses(exchange);

		assertThat(alternates, containsInAnyOrder(ENDPOINT_1, ENDPOINT_2, ENDPOINT_3));
		verify(sl);
	}
	
	@Test
	public void getAlternateAddressesLocatorThrowsException() throws Exception {
		
		ServiceLocator sl = createMock(ServiceLocator.class);
		Exchange exchange = createExchangeMock(SERVICE_NAME);

		expect(sl.lookup(SERVICE_NAME)).andThrow(new ServiceLocatorException());
		replay(sl);
		
		LocatorSelectionStrategy strategy = new LocatorSelectionStrategy();
		strategy.setServiceLocator(sl);
		
		List<String> alternates = strategy.getAlternateAddresses(exchange);

		assertThat(alternates, EMPTY_ENDPOINT_COLLECTION);
		verify(sl);
	}

	private static Exchange createExchangeMock(QName serviceName) {
		Service service = createMock(Service.class); 
		Endpoint ep = createMock(Endpoint.class);
		Exchange exchange = createMock(Exchange.class);

		expect(service.getName()).andStubReturn(SERVICE_NAME);
		expect(ep.getService()).andStubReturn(service);
		expect(exchange.getEndpoint()).andStubReturn(ep);
		
		replay(service, ep, exchange);
		return exchange;
	}
}
