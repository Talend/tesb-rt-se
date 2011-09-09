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
import org.junit.Test;
import org.talend.esb.locator.proxy.service.types.AssertionType;
import org.talend.esb.locator.proxy.service.types.EndpointReferenceListType;
import org.talend.esb.locator.proxy.service.types.EntryType;
import org.talend.esb.locator.proxy.service.types.LookupRequestType;
import org.talend.esb.locator.proxy.service.types.MatcherDataType;
import org.talend.esb.locator.proxy.service.types.RegisterEndpointRequestType;
import org.talend.esb.locator.proxy.service.types.SLPropertiesType;
import org.talend.esb.locator.proxy.service.types.UnregisterEndpointRequestType;
import org.talend.esb.servicelocator.client.SLPropertiesMatcher;
import org.talend.esb.servicelocator.client.ServiceLocator;
import org.talend.esb.servicelocator.client.ServiceLocatorException;

public class LocatorProxyServiceTest extends EasyMockSupport {

	@Before
	public void setup() {
	}

	@Test
	public void registeEndpoint(){

	}

	@Test
	public void registeEndpointWithOptionalParameter() {

	}

	@Test
	public void unregisteEndpoint(){
	}

	@Test
	public void lookUpEndpoint(){
	}

	@Test
	public void lookUpEndpointFault(){

	}

	@Test
	public void lookUpEndpoints(){
	}
	
	public void lookUpEndpointsFault(){

	}

}
