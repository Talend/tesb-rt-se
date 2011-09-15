package org.talend.esb.locator.rest.proxy.service;

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
import org.junit.Ignore;
import org.junit.Test;
import org.talend.esb.locator.rest.proxy.service.types.EndpointReferenceListType;
import org.talend.esb.locator.rest.proxy.service.types.EntryType;
import org.talend.esb.locator.rest.proxy.service.types.RegisterEndpointRequestType;
//import org.talend.esb.locator.proxy.service.types.LookupRequestType;
import org.talend.esb.servicelocator.client.ServiceLocator;
import org.talend.esb.servicelocator.client.ServiceLocatorException;

public class LocatorProxyServiceTest extends EasyMockSupport {

	private ServiceLocator sl;
	private QName SERVICE_NAME;
	private QName NOT_EXIST_SERVICE_NAME;
	private final String PROPERTY_KEY = "Key1";
	private final String PROPERTY_VALUE1 = "Value1";
	private final String PROPERTY_VALUE2 = "Value2";
	private final String ENDPOINTURL = "http://Service";
	private final String QNAME_PREFIX1 = "http://services.talend.org/TestService";
	private final String QNAME_LOCALPART1 = "TestServiceProvider";
	private final String QNAME_PREFIX2 = "http://services.talend.org/NoNameService";
	private final String QNAME_LOCALPART2 = "NoNameServiceProvider";
	private List<String> names;
	private LocatorProxyServiceImpl lps;
	
	@Before
	public void setup() {
		sl = createMock(ServiceLocator.class);
		names = new ArrayList<String>();
		SERVICE_NAME = new QName(QNAME_PREFIX1, QNAME_LOCALPART1);
		NOT_EXIST_SERVICE_NAME = new QName(QNAME_PREFIX2, QNAME_LOCALPART2);
		names = new ArrayList<String>();
		lps = new LocatorProxyServiceImpl();
		lps.setLocatorClient(sl);
	}

	@Test
	public void lookUpEndpointTest() throws ServiceLocatorException, InterruptedException{
		names.clear();
		names.add(ENDPOINTURL);
		expect(sl.lookup(SERVICE_NAME)).andStubReturn(names);
		replayAll();

		W3CEndpointReference endpointRef, expectedRef;
		W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
		builder.serviceName(SERVICE_NAME);
		builder.address(ENDPOINTURL);
		expectedRef = builder.build();
		
		endpointRef = (W3CEndpointReference)lps.lookupEndpoint(SERVICE_NAME.toString(), new ArrayList<String>()).getEntity();
		
		Assert.assertTrue(endpointRef.toString().equals(expectedRef.toString()));
	}
	
	@Test
	public void lookUpEndpointTestNotFound() throws ServiceLocatorException, InterruptedException{
		names.clear();
		names.add(ENDPOINTURL);
		expect(sl.lookup(SERVICE_NAME)).andStubReturn(null);
		replayAll();
		
		int status = lps.lookupEndpoint(SERVICE_NAME.toString(), new ArrayList<String>()).getStatus();
		
		Assert.assertTrue(status == 404);
	}
	
	
	@Test
	public void lookUpEndpointTextStatus() throws ServiceLocatorException, InterruptedException{
		names.clear();
		names.add(ENDPOINTURL);
		expect(sl.lookup(SERVICE_NAME)).andStubReturn(names);
		replayAll();

		int status = lps.lookupEndpoint(SERVICE_NAME.toString(), new ArrayList<String>()).getStatus();
		
		Assert.assertTrue(status == 200);
	}

	@Test
	public void lookUpEndpoints() throws ServiceLocatorException, InterruptedException{
		names.clear();
		names.add(ENDPOINTURL);
		expect(sl.lookup(SERVICE_NAME)).andStubReturn(names);
		replayAll();

		W3CEndpointReference endpointRef, expectedRef;
		W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
		builder.serviceName(SERVICE_NAME);
		builder.address(ENDPOINTURL);
		expectedRef = builder.build();
		EndpointReferenceListType refs;

		refs = (EndpointReferenceListType)lps.lookupEndpoints(SERVICE_NAME.toString(), new ArrayList<String>()).getEntity();
		endpointRef = refs.getReturn().get(0);

		Assert.assertTrue(endpointRef.toString().equals(expectedRef.toString()));
	}
	
	@Test
	public void lookUpEndpointsNotFound() throws ServiceLocatorException, InterruptedException{
		names.clear();
		names.add(ENDPOINTURL);
		expect(sl.lookup(SERVICE_NAME)).andStubReturn(null);
		replayAll();

		W3CEndpointReference endpointRef, expectedRef;
		W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
		builder.serviceName(SERVICE_NAME);
		builder.address(ENDPOINTURL);
		expectedRef = builder.build();
		EndpointReferenceListType refs;

		int status = lps.lookupEndpoints(SERVICE_NAME.toString(), new ArrayList<String>()).getStatus();
		
		Assert.assertTrue(status == 404);
	}
	
	@Test
	public void unregisterEndpoint() throws ServiceLocatorException, InterruptedException{
		sl.unregister(SERVICE_NAME, ENDPOINTURL);
		EasyMock.expectLastCall();
		replayAll();
		int status = lps.unregisterEndpoint(SERVICE_NAME.toString(), ENDPOINTURL).getStatus();
		Assert.assertTrue(status == 200);
	}
}
