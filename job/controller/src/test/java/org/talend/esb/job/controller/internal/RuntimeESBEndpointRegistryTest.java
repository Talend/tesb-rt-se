package org.talend.esb.job.controller.internal;

import org.apache.wss4j.common.crypto.Crypto;
import org.junit.After;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import org.talend.esb.job.controller.ESBEndpointConstants;
import org.talend.esb.sam.agent.feature.EventFeature;
import org.talend.esb.security.policy.PolicyProvider;
import org.talend.esb.security.saml.STSClientUtils;
import org.talend.esb.servicelocator.cxf.LocatorFeature;

import routines.system.api.ESBEndpointInfo;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.util.HashMap;
import java.util.Map;

public class RuntimeESBEndpointRegistryTest {

	@Before
	public void startCustomerService() {
	}

	@Test
	public void createDefaultConsumer() throws Exception {

		RuntimeESBEndpointRegistry registry = new RuntimeESBEndpointRegistry();
		ESBEndpointInfo i = getMockEndpointInfo(getDefaultEndpointProperties());
		assertNotNull(registry.createConsumer(i));
	}
	
	@Test
	public void setters() throws Exception {

		RuntimeESBEndpointRegistry registry = new RuntimeESBEndpointRegistry();
		
		registry.setLocatorFeature(createNiceMock(LocatorFeature.class));
		registry.setPolicyProvider(createNiceMock(PolicyProvider.class));
		registry.setSamFeature(createNiceMock(EventFeature.class));
		registry.setCryptoProvider(createNiceMock(Crypto.class));
		
		ESBEndpointInfo i = getMockEndpointInfo(getDefaultEndpointProperties());
		assertNotNull(registry.createConsumer(i));
	}

	@Test
	public void serviceRegistryOnBus() throws Exception {
		
	    Map<String, String> m = new HashMap<String, String>();
		m.put("sts.namespace", "test");
		m.put("sts.service.name", "test");
		m.put("sts.endpoint.name", "test");
		
		STSClientUtils u = new STSClientUtils(m);
		
		RuntimeESBEndpointRegistry registry = new RuntimeESBEndpointRegistry();
		Map<String, Object> ep = getDefaultEndpointProperties();
		ep.put(ESBEndpointConstants.USE_SERVICE_REGISTRY, true);
		ESBEndpointInfo i = getMockEndpointInfo(ep);

		registry.setBus(createNiceMock(org.apache.cxf.Bus.class));
		registry.setClientProperties(new HashMap<String, String>());
		
		assertNotNull(registry.createConsumer(i));
	}

	@After
	public void closeContextAfterEachTest() {
	}

	private Map<String, Object> getDefaultEndpointProperties() {
		Map<String, Object> ep = new HashMap<String, Object>();
		ep.put(ESBEndpointConstants.SERVICE_NAME, "{http://services.talend.org/test/Library/1.0}/LibraryProvider");
		ep.put(ESBEndpointConstants.OPERATION_NAMESPACE, "http://services.talend.org/test/Library/1.0");
		ep.put(ESBEndpointConstants.DEFAULT_OPERATION_NAME, "seekBook");

		ep.put(ESBEndpointConstants.PORT_NAME, "{http://services.talend.org/test/Library/1.0}/LibraryHttpPort");
		ep.put(ESBEndpointConstants.PUBLISHED_ENDPOINT_URL, "local://LibraryHttpPort");
		ep.put(ESBEndpointConstants.WSDL_URL, "classpath:/conf/libraryService/Library.wsdl");
		ep.put(ESBEndpointConstants.USE_SERVICE_LOCATOR, false);

		return ep;
	}

	private ESBEndpointInfo getMockEndpointInfo(Map<String, Object> ep) {
		ESBEndpointInfo i = createNiceMock(ESBEndpointInfo.class);
		expect(i.getEndpointProperties()).andReturn(ep).anyTimes();
		replay(i);

		return i;
	}

}