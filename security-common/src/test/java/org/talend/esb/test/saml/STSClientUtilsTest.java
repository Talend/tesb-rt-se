package org.talend.esb.test.saml;

import org.apache.cxf.Bus;
import org.apache.cxf.ws.security.SecurityConstants;
import org.apache.cxf.ws.security.trust.STSClient;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import org.talend.esb.security.saml.STSClientUtils;
import static org.easymock.EasyMock.createMock;
import java.util.HashMap;
import java.util.Map;

public class STSClientUtilsTest {

	private static final String STS_WSDL_LOCATION = "sts.wsdl.location";
	private static final String STS_NAMESPACE = "sts.namespace";
	private static final String STS_SERVICE_NAME = "sts.service.name";
	private static final String STS_ENDPOINT_NAME = "sts.endpoint.name";
	private static final String STS_X509_ENDPOINT_NAME = "sts.x509.endpoint.name";

	private static final String ENABLE_SAML_ONE_TIME_USE_CACHE = "ws-security.enable.saml.cache";

	private static Bus BUS = createMock(Bus.class);

	@SuppressWarnings("static-access")
	@Test
	public void createSTSClientTest() throws Exception {

		STSClientUtils u = new STSClientUtils(getSTSProperties());
		assertNotNull(u.createSTSClient(BUS, getSTSProperties()));

	}

	@Test
	public void createSTSX509Client() throws Exception {

		assertNotNull(STSClientUtils.createSTSX509Client(BUS, getSTSProperties()));

	}

	@SuppressWarnings("static-access")
	@Test
	public void createSTSX509ClientWithAlias() throws Exception {

		STSClientUtils u = new STSClientUtils(getSTSProperties());

		String alias = "alias";

		STSClient client = u.createSTSX509Client(BUS, alias);

		assertNotNull(client);

		assertEquals(client.getProperties().get(SecurityConstants.STS_TOKEN_USERNAME), alias);

	}

	@Test
	public void createSTSClientUserNamePasswordTest() throws Exception {

		String username = "username";
		String password = "password";

		STSClient client = STSClientUtils.createSTSClient(BUS, username, password);

		assertNotNull(client);
		assertEquals(client.getProperties().get(SecurityConstants.USERNAME), username);
		assertEquals(client.getProperties().get(SecurityConstants.PASSWORD), password);

	}

	@Test
	public void applyAuthorization() throws Exception {

		STSClient client = STSClientUtils.createSTSClient(BUS, getSTSProperties());

		STSClientUtils.applyAuthorization(client, "testRole");

	}

	@Test
	public void applyAuthorizationNullRole() throws Exception {

		STSClient client = STSClientUtils.createSTSClient(BUS, getSTSProperties());

		STSClientUtils.applyAuthorization(client, null);

	}

	private Map<String, String> getSTSProperties() {

		Map<String, String> stsProperties = new HashMap<String, String>();

		stsProperties.put(STS_NAMESPACE, "STS_NAMESPACE");
		stsProperties.put(STS_SERVICE_NAME, "STS_SERVICE_NAME");
		stsProperties.put(STS_ENDPOINT_NAME, "STS_ENDPOINT_NAME");
		stsProperties.put(STS_WSDL_LOCATION, "STS_WSDL_LOCATION");
		stsProperties.put(ENABLE_SAML_ONE_TIME_USE_CACHE, "file:false");
		stsProperties.put(STS_X509_ENDPOINT_NAME, "STS_X509_ENDPOINT_NAME");

		return stsProperties;
	}

}