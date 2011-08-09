package org.talend.esb.locator.proxy.service;

import static org.easymock.EasyMock.expect;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import javax.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;

import junit.framework.Assert;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.talend.esb.locator.proxy.service.types.EndpointReferenceListType;
import org.talend.esb.locator.proxy.service.types.LookupRequestType;
import org.talend.esb.locator.proxy.service.types.RegisterEndpointRequestType;
import org.talend.esb.locator.proxy.service.types.UnregisterEndpointRequestType;
import org.talend.esb.servicelocator.client.ServiceLocator;
import org.talend.esb.servicelocator.client.ServiceLocatorException;

public class LocatorProxyServiceTest extends EasyMockSupport {

	private ServiceLocator sl;
	private QName SERVICE_NAME;
	private QName NOT_EXIST_SERVICE_NAME;
	private String endpointURL;
	private List<String> names;
	private	LocatorProxyServiceImpl lps;

	@Before
	public void setup() {

		sl = createMock(ServiceLocator.class);
		SERVICE_NAME = new QName("http://services.talend.org/TestService",
				"TestServiceProvider");
		NOT_EXIST_SERVICE_NAME = new QName(
				"http://services.talend.org/NoNameService",
				"NoNameServiceProvider");
		endpointURL = "http://Service";
		names = new ArrayList<String>();
		lps = new LocatorProxyServiceImpl();
		lps.setLocatorClient(sl);

	}

//	@Test
//	public void initializeLocatorClient() throws InterruptedException,
//			ServiceLocatorException {
//		LocatorProxyServiceImpl lps = new LocatorProxyServiceImpl();
//		lps.setConnectionTimeout(5001);
//		lps.setSessionTimeout(5001);
//		lps.setLocatorEndpoints("test:8021");
//		lps.setLocatorClient(sl);
//		lps.initLocator();
//
//	}

	@Test
	public void registeEndpoint() throws InterruptedExceptionFault, ServiceLocatorFault {
			LocatorProxyServiceImpl lps = new LocatorProxyServiceImpl();
			lps.setLocatorClient(sl);
			RegisterEndpointRequestType register_input = new RegisterEndpointRequestType();
			register_input.setEndpointURL(endpointURL);
			register_input.setServiceName(SERVICE_NAME);
			lps.registerEndpoint(register_input);

	}


	@Test
	public void unregisteEndpoint() throws InterruptedExceptionFault,
			ServiceLocatorFault {
		LocatorProxyServiceImpl lps = new LocatorProxyServiceImpl();
		lps.setLocatorClient(sl);
		UnregisterEndpointRequestType unregister_input = new UnregisterEndpointRequestType();
		unregister_input.setEndpointURL(endpointURL);
		unregister_input.setServiceName(SERVICE_NAME);
		lps.unregisterEnpoint(unregister_input);

	}

	@Test
	public void lookUpEndpoint() throws InterruptedExceptionFault,
			ServiceLocatorFault, ServiceLocatorException, InterruptedException {
		names.clear();
		names.add(endpointURL);
		expect(sl.lookup(SERVICE_NAME)).andStubReturn(names);
		replayAll();

		LookupRequestType lookup_input = new LookupRequestType();
		W3CEndpointReference endpointRef, expectedRef;
		W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
		builder.serviceName(SERVICE_NAME);
		builder.address(endpointURL);
		expectedRef = builder.build();

		lookup_input.setServiceName(SERVICE_NAME);
		endpointRef = lps.lookupEndpoint(lookup_input);

		Assert.assertTrue(endpointRef.toString().equals(expectedRef.toString()));

	}

	@Test(expected = ServiceLocatorFault.class)
	public void lookUpEndpointFault() throws InterruptedExceptionFault,
			ServiceLocatorFault, ServiceLocatorException, InterruptedException {

		expect(sl.lookup(NOT_EXIST_SERVICE_NAME)).andStubReturn(null);
		replayAll();

		LookupRequestType lookup_input = new LookupRequestType();

		lookup_input.setServiceName(NOT_EXIST_SERVICE_NAME);
		lps.lookupEndpoint(lookup_input);

	}

	@Test
	public void lookUpEndpoints() throws InterruptedExceptionFault,
			ServiceLocatorFault, ServiceLocatorException, InterruptedException {

		names.add(endpointURL);
		expect(sl.lookup(SERVICE_NAME)).andStubReturn(names);
		replayAll();

		LookupRequestType lookup_input = new LookupRequestType();
		W3CEndpointReference endpointRef, expectedRef;
		W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
		builder.serviceName(SERVICE_NAME);
		builder.address(endpointURL);
		expectedRef = builder.build();
		EndpointReferenceListType refs;

		lookup_input.setServiceName(SERVICE_NAME);
		refs = lps.lookupEndpoints(lookup_input);
		endpointRef = refs.getReturn().get(0);

		Assert.assertTrue(endpointRef.toString().equals(expectedRef.toString()));

	}

	@Test(expected = ServiceLocatorFault.class)
	public void lookUpEndpointsFault() throws InterruptedExceptionFault,
			ServiceLocatorFault, ServiceLocatorException, InterruptedException {

		names.add(endpointURL);
		expect(sl.lookup(NOT_EXIST_SERVICE_NAME)).andStubReturn(null);
		replayAll();

		LookupRequestType lookup_input = new LookupRequestType();

		lookup_input.setServiceName(NOT_EXIST_SERVICE_NAME);
		lps.lookupEndpoints(lookup_input);

	}

}
