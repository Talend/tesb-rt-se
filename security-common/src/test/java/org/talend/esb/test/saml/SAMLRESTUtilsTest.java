package org.talend.esb.test.saml;

import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxrs.AbstractJAXRSFactoryBean;
import org.apache.cxf.message.Message;
import org.apache.cxf.ws.security.SecurityConstants;
import org.apache.cxf.ws.security.trust.STSClient;
import org.easymock.EasyMock;
import org.junit.Test;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import org.talend.esb.security.saml.SAMLRESTUtils;
import org.talend.esb.security.saml.STSRESTOutInterceptor;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SAMLRESTUtilsTest {

	@Test
	public void configureClientTest() throws Exception {

		AbstractJAXRSFactoryBean clientFactory = createMock(AbstractJAXRSFactoryBean.class);
		STSClient stsClient = createMock(STSClient.class);

		List<Interceptor<? extends Message>> interceptors = new ArrayList<Interceptor<? extends Message>>();

		expect(clientFactory.getOutInterceptors()).andReturn(interceptors).anyTimes();

		replay(clientFactory);

		SAMLRESTUtils.configureClient(clientFactory, stsClient);

		assertFalse(interceptors.isEmpty());

		for (Interceptor<? extends Message> i : interceptors) {
			if (i instanceof STSRESTOutInterceptor) {
				assertSame(((STSRESTOutInterceptor) i).getStsClient(), stsClient);
			}
		}

		EasyMock.verify(clientFactory);

	}

	@Test
	public void configureServerTest() throws Exception {

		AbstractJAXRSFactoryBean serverFactory = createMock(AbstractJAXRSFactoryBean.class);
		Map<String, Object> properties = new HashMap<String, Object>();
		expect(serverFactory.getProperties(true)).andReturn(properties).anyTimes();

		Map<String, Object> securityProps = new HashMap<String, Object>();

		serverFactory.setProvider(EasyMock.<Object>anyObject());
		EasyMock.expectLastCall().once();

		replay(serverFactory);

		SAMLRESTUtils.configureServer(serverFactory, securityProps);

		assertTrue(properties.containsKey(SecurityConstants.SIGNATURE_PROPERTIES));
		assertTrue(properties.containsKey(SecurityConstants.CALLBACK_HANDLER));
		assertTrue(properties.containsKey(SecurityConstants.SIGNATURE_USERNAME));

		EasyMock.verify(serverFactory);

	}

}