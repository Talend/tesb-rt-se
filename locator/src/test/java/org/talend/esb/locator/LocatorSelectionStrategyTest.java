package org.talend.esb.locator;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIn.isIn;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.service.Service;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.Before;

public class LocatorSelectionStrategyTest {

	public static final QName SERVICE_NAME_1 = new QName("nsp1", "name1");

	public static final QName SERVICE_NAME_2 = new QName("nsp2", "name2");

	public static final String ENDPOINT_1 = "endpoint1";

	public static final String ENDPOINT_2 = "endpoint2";

	public static final String ENDPOINT_3 = "endpoint3";
	
	public static final String ENDPOINT_4 = "endpoint4";

	public static final String ENDPOINT_5 = "endpoint5";

	public static final List<String> ENDPOINTS_1 = 
		Arrays.asList(ENDPOINT_1, ENDPOINT_2, ENDPOINT_3);
	
	public static final List<String> ENDPOINTS_2 = 
		Arrays.asList(ENDPOINT_4, ENDPOINT_5);

	public static final List<String> ENDPOINTS_1A = 
		Arrays.asList(ENDPOINT_1, ENDPOINT_2, ENDPOINT_3);
	
	public static final List<String> ENDPOINTS_1B = 
		Arrays.asList(ENDPOINT_4, ENDPOINT_5);

	public static final Matcher<Collection<String>> EMPTY_ENDPOINT_COLLECTION = empty();

	private static final Exchange EXCHANGE_1 = createExchangeMock(SERVICE_NAME_1);

	private static final Exchange EXCHANGE_2 = createExchangeMock(SERVICE_NAME_2);

	private ServiceLocator sl = createMock(ServiceLocator.class);

	private LocatorSelectionStrategy strategy;
	
	@Before
	public void setup() {
		strategy = new LocatorSelectionStrategy();
		strategy.setServiceLocator(sl);
	}
	
	@Test
	public void getAlternateAddressesReturnsListFromLocator() throws Exception {
		expect(sl.lookup(SERVICE_NAME_1)).andReturn(ENDPOINTS_1);
		replay(sl);
		
		List<String> alternates = strategy.getAlternateAddresses(EXCHANGE_1);

		assertThat(alternates, containsInAnyOrder(ENDPOINT_1, ENDPOINT_2, ENDPOINT_3));
		verify(sl);
	}
	
	@Test
	public void getAlternateAddressesLocatorThrowsException() throws Exception {
		expect(sl.lookup(SERVICE_NAME_1)).andThrow(new ServiceLocatorException());
		replay(sl);
				
		List<String> alternates = strategy.getAlternateAddresses(EXCHANGE_1);

		assertThat(alternates, EMPTY_ENDPOINT_COLLECTION);
		verify(sl);
	}

	@Test
	public void getPrimaryAddressReturnsElementFromLocator() throws Exception {
		expect(sl.lookup(SERVICE_NAME_1)).andReturn(ENDPOINTS_1);
		replay(sl);
		
		String address = strategy.getPrimaryAddress(EXCHANGE_1);

		assertThat(address, isIn(ENDPOINTS_1));
		verify(sl);
	}

	@Test
	public void getPrimaryAddressLocatorThrowsException() throws Exception {
		expect(sl.lookup(SERVICE_NAME_1)).andThrow(new ServiceLocatorException());
		replay(sl);
		
		String address = strategy.getPrimaryAddress(EXCHANGE_1);

		assertThat(address, nullValue());
		verify(sl);
	}

	@Test
	public void secondGetPrimaryAddressCallWithSameExchangeReturnsSameAsFirstCall() throws Exception {
		expect(sl.lookup(SERVICE_NAME_1)).andReturn(ENDPOINTS_1);
		replay(sl);

		String firstAddress = strategy.getPrimaryAddress(EXCHANGE_1);
		String secondAddress = strategy.getPrimaryAddress(EXCHANGE_1);

		assertThat(secondAddress, equalTo(firstAddress));
		verify(sl);
	}

	@Test
	public void secondGetPrimaryAddressCallWithDifferentExchangeReturnsDifferentAsFirstCall() throws Exception {
		expect(sl.lookup(SERVICE_NAME_1)).andReturn(ENDPOINTS_1);
		expect(sl.lookup(SERVICE_NAME_2)).andReturn(ENDPOINTS_2);
		replay(sl);
		
		strategy.getPrimaryAddress(EXCHANGE_1);
		String secondAddress = strategy.getPrimaryAddress(EXCHANGE_2);

		assertThat(secondAddress, isIn(ENDPOINTS_2));
		verify(sl);
	}

	@Test
	public void secondGetPrimaryAddressReturnsNewAddressWhenGetAlternateAddressCalledInBetween()
	throws Exception{
		expect(sl.lookup(SERVICE_NAME_1)).andReturn(ENDPOINTS_1A);
		expect(sl.lookup(SERVICE_NAME_1)).andReturn(ENDPOINTS_1A);
		expect(sl.lookup(SERVICE_NAME_1)).andReturn(ENDPOINTS_1B);
		replay(sl);
	
		String firstAddress = strategy.getPrimaryAddress(EXCHANGE_1);
		strategy.getAlternateAddresses(EXCHANGE_1);
		String secondAddress = strategy.getPrimaryAddress(EXCHANGE_1);

		assertThat(secondAddress, not(equalTo(firstAddress)));
		assertThat(secondAddress, isIn(ENDPOINTS_1B));
		verify(sl);
	}

	@Test
	public void selectAlternateAddressReturnsOneOfTheAddressesPassedAsParamter() {
		List<String> endpoints = new ArrayList<String>(ENDPOINTS_1); 
		String address = strategy.selectAlternateAddress(endpoints);
		assertThat(address, isIn(ENDPOINTS_1));
	}

	private static Exchange createExchangeMock(QName serviceName) {
		Service service = createMock(Service.class); 
		Endpoint ep = createMock(Endpoint.class);
		Exchange exchange = createMock(Exchange.class);

		expect(service.getName()).andStubReturn(serviceName);
		expect(ep.getService()).andStubReturn(service);
		expect(exchange.getEndpoint()).andStubReturn(ep);
		
		replay(service, ep, exchange);
		return exchange;
	}
}