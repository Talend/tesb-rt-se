package org.talend.esb.locator.proxy.service;

import static org.easymock.EasyMock.expect;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import javax.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.talend.schemas.esb._2011._09.locator.AssertionType;
import org.talend.schemas.esb._2011._09.locator.EndpointReferenceListType;
import org.talend.schemas.esb._2011._09.locator.EntryType;
import org.talend.schemas.esb._2011._09.locator.LookupRequestType;
import org.talend.schemas.esb._2011._09.locator.MatcherDataType;
import org.talend.schemas.esb._2011._09.locator.RegisterEndpointRequestType;
import org.talend.schemas.esb._2011._09.locator.SLPropertiesType;
import org.talend.schemas.esb._2011._09.locator.UnregisterEndpointRequestType;
import org.talend.webservices.esb.locator_v1.InterruptedExceptionFault;
import org.talend.webservices.esb.locator_v1.ServiceLocatorFault;
import org.talend.esb.servicelocator.client.ServiceLocator;
import org.talend.esb.servicelocator.client.ServiceLocatorException;

public class LocatorProxyServiceTest extends EasyMockSupport {

	private ServiceLocator sl;
	private QName SERVICE_NAME;
	private QName NOT_EXIST_SERVICE_NAME;
	private final String PROPERTY_KEY = "Key1";
	private final String PROPERTY_VALUE1 = "Value1";
	private final String PROPERTY_VALUE2 = "Value2";
	private final String ENDPOINTURL = "http://Service";;
	private final String QNAME_PREFIX1 = "http://services.talend.org/TestService";
	private final String QNAME_LOCALPART1 = "TestServiceProvider";
	private final String QNAME_PREFIX2 = "http://services.talend.org/NoNameService";
	private final String QNAME_LOCALPART2 = "NoNameServiceProvider";
	private List<String> names;
	private LocatorProxyServiceImpl lps;

	@Before
	public void setup() {

		sl = createMock(ServiceLocator.class);
		SERVICE_NAME = new QName(QNAME_PREFIX1, QNAME_LOCALPART1);
		NOT_EXIST_SERVICE_NAME = new QName(QNAME_PREFIX2, QNAME_LOCALPART2);
		names = new ArrayList<String>();
		lps = new LocatorProxyServiceImpl();
		lps.setLocatorClient(sl);

	}

	// @Test
	// public void initializeLocatorClient() throws InterruptedException,
	// ServiceLocatorException {
	// LocatorProxyServiceImpl lps = new LocatorProxyServiceImpl();
	// lps.setConnectionTimeout(5001);
	// lps.setSessionTimeout(5001);
	// lps.setLocatorEndpoints("test:8021");
	// lps.setLocatorClient(sl);
	// lps.initLocator();
	//
	// }

	@Test
	public void registeEndpoint() throws InterruptedExceptionFault,
			ServiceLocatorFault {
		LocatorProxyServiceImpl lps = new LocatorProxyServiceImpl();
		lps.setLocatorClient(sl);
		RegisterEndpointRequestType register_input = new RegisterEndpointRequestType();
		register_input.setEndpointURL(ENDPOINTURL);
		register_input.setServiceName(SERVICE_NAME);
		lps.registerEndpoint(register_input);

	}

	@Test
	public void registeEndpointWithOptionalParameter()
			throws InterruptedExceptionFault, ServiceLocatorFault {
		LocatorProxyServiceImpl lps = new LocatorProxyServiceImpl();
		lps.setLocatorClient(sl);
		RegisterEndpointRequestType register_input = new RegisterEndpointRequestType();
		register_input.setEndpointURL(ENDPOINTURL);
		register_input.setServiceName(SERVICE_NAME);

		SLPropertiesType value = new SLPropertiesType();
		EntryType e = new EntryType();

		e.setKey(PROPERTY_KEY);
		e.getValue().add(PROPERTY_VALUE1);
		e.getValue().add(PROPERTY_VALUE2);
		value.getEntry().add(e);

		register_input.setProperties(value);
		lps.registerEndpoint(register_input);

	}

	@Test
	public void unregisteEndpoint() throws InterruptedExceptionFault,
			ServiceLocatorFault {
		LocatorProxyServiceImpl lps = new LocatorProxyServiceImpl();
		lps.setLocatorClient(sl);
		UnregisterEndpointRequestType unregister_input = new UnregisterEndpointRequestType();
		unregister_input.setEndpointURL(ENDPOINTURL);
		unregister_input.setServiceName(SERVICE_NAME);
		lps.unregisterEnpoint(unregister_input);

	}

	@Test
	public void lookUpEndpoint() throws InterruptedExceptionFault,
			ServiceLocatorFault, ServiceLocatorException, InterruptedException {
		names.clear();
		names.add(ENDPOINTURL);
		expect(sl.lookup(SERVICE_NAME)).andStubReturn(names);
		replayAll();

		LookupRequestType lookup_input = new LookupRequestType();
		W3CEndpointReference endpointRef, expectedRef;
		W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
		builder.serviceName(SERVICE_NAME);
		builder.address(ENDPOINTURL);
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

		names.clear();
		names.add(ENDPOINTURL);
		expect(sl.lookup(SERVICE_NAME)).andStubReturn(names);
		replayAll();

		LookupRequestType lookup_input = new LookupRequestType();
		W3CEndpointReference endpointRef, expectedRef;
		W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
		builder.serviceName(SERVICE_NAME);
		builder.address(ENDPOINTURL);
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

		names.clear();
		names.add(ENDPOINTURL);
		expect(sl.lookup(NOT_EXIST_SERVICE_NAME)).andStubReturn(null);
		replayAll();

		LookupRequestType lookup_input = new LookupRequestType();

		lookup_input.setServiceName(NOT_EXIST_SERVICE_NAME);
		lps.lookupEndpoints(lookup_input);

	}

}
