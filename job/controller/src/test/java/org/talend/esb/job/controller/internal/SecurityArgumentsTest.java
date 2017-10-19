package org.talend.esb.job.controller.internal;

import org.apache.wss4j.common.crypto.Crypto;
import org.junit.After;
import static org.junit.Assert.assertSame;
import org.junit.Before;
import org.junit.Test;
import org.talend.esb.job.controller.ESBEndpointConstants.EsbSecurity;
import org.talend.esb.security.saml.STSClientUtils;
import org.apache.cxf.Bus;
import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.ws.security.SecurityConstants;
import org.apache.neethi.Policy;

import static org.easymock.EasyMock.createNiceMock;
import java.util.HashMap;
import java.util.Map;

public class SecurityArgumentsTest {

	@Before
	public void startCustomerService() {
	}

	@Test
	public void constructorTest() throws Exception {

		EsbSecurity esbSecurity = EsbSecurity.fromString(null);
		Policy policy = createNiceMock(Policy.class);
		String username = "username";
		String password = "password";
		String alias = "alias";
		Map<String, String> clientProperties = new HashMap<String, String>();
		String roleName = "roleName";
		Object securityToken = new Object();
		Crypto cryptoProvider = createNiceMock(Crypto.class);

		SecurityArguments sa = new SecurityArguments(esbSecurity, policy, username, password, alias, clientProperties,
				roleName, securityToken, cryptoProvider);

		assertSame(sa.getEsbSecurity(), esbSecurity);
		assertSame(sa.getPolicy(), policy);
		assertSame(sa.getUsername(), username);
		assertSame(sa.getPassword(), password);
		assertSame(sa.getAlias(), alias);
		assertSame(sa.getClientProperties(), clientProperties);
		assertSame(sa.getRoleName(), roleName);
		assertSame(sa.getSecurityToken(), securityToken);
		assertSame(sa.getCryptoProvider(), cryptoProvider);
	}

	@Test
	public void buildBasicAuthorizationPolicy() throws Exception {

		EsbSecurity esbSecurity = EsbSecurity.fromString("BASIC");
		String username = "username";
		String password = "password";
		String alias = "alias";

		SecurityArguments sa = new SecurityArguments(esbSecurity, null, username, password, alias, null, null, null,
				null);

		AuthorizationPolicy p = sa.buildAuthorizationPolicy();
		assertSame(p.getPassword(), password);
		assertSame(p.getUserName(), username);
		assertSame(p.getAuthorizationType(), "Basic");

	}

	@Test
	public void buildDigestAuthorizationPolicy() throws Exception {

		EsbSecurity esbSecurity = EsbSecurity.fromString("DIGEST");
		String username = "username";
		String password = "password";
		String alias = "alias";

		SecurityArguments sa = new SecurityArguments(esbSecurity, null, username, password, alias, null, null, null,
				null);

		AuthorizationPolicy p = sa.buildAuthorizationPolicy();
		assertSame(p.getPassword(), password);
		assertSame(p.getUserName(), username);
		assertSame(p.getAuthorizationType(), "Digest");

	}

	@Test
	public void buildClientConfig() throws Exception {

		initSTSClientUtils();

		EsbSecurity esbSecurity = EsbSecurity.fromString("DIGEST");
		Policy policy = createNiceMock(Policy.class);
		String username = "username";
		String password = "password";
		String alias = "alias";
		Map<String, String> clientProperties = new HashMap<String, String>();
		String actor = "actor";
		String cacheConfiFile = "file:123";
		clientProperties.put(SecurityConstants.ACTOR, actor);
		clientProperties.put(SecurityConstants.CACHE_CONFIG_FILE, cacheConfiFile);

		String roleName = "roleName";
		Object securityToken = new Object();
		Crypto cryptoProvider = createNiceMock(Crypto.class);

		SecurityArguments sa = new SecurityArguments(esbSecurity, policy, username, password, alias, clientProperties,
				roleName, securityToken, cryptoProvider);

		Bus bus = createNiceMock(Bus.class);
		boolean useServiceRegistry = true;
		String encryptionUsername = "encryptionUsername";
		Map<String, Object> config = sa.buildClientConfig(bus, useServiceRegistry, encryptionUsername);

		assertSame(config.get("security.username"), username);
		assertSame(config.get("security.encryption.username"), encryptionUsername);
		assertSame(config.get("security.password"), password);
		assertSame(config.get("security.signature.username"), alias);
		assertSame(config.get("security.encryption.crypto"), cryptoProvider);
	}

	@Test
	public void buildClientConfigWithoutAlias() throws Exception {

		initSTSClientUtils();

		EsbSecurity esbSecurity = EsbSecurity.fromString("DIGEST");
		Policy policy = createNiceMock(Policy.class);
		String username = "username";
		String password = "password";
		String alias = null;
		Map<String, String> clientProperties = new HashMap<String, String>();
		String actor = "ws-security.actor";
		clientProperties.put("ws-security.actor", actor);

		String roleName = "roleName";
		Object securityToken = new Object();
		Crypto cryptoProvider = createNiceMock(Crypto.class);

		SecurityArguments sa = new SecurityArguments(esbSecurity, policy, username, password, alias, clientProperties,
				roleName, securityToken, cryptoProvider);

		Bus bus = createNiceMock(Bus.class);
		boolean useServiceRegistry = true;
		String encryptionUsername = "encryptionUsername";
		Map<String, Object> config = sa.buildClientConfig(bus, useServiceRegistry, encryptionUsername);

		assertSame(config.get("security.username"), username);
		assertSame(config.get("security.encryption.username"), encryptionUsername);
		assertSame(config.get("security.password"), password);
		assertSame(config.get("security.signature.username"), alias);
		assertSame(config.get("security.encryption.crypto"), cryptoProvider);
	}

	private void initSTSClientUtils() {
		Map<String, String> m = new HashMap<String, String>();
		m.put("sts.namespace", "test");
		m.put("sts.service.name", "test");
		m.put("sts.endpoint.name", "test");
		m.put("sts.x509.endpoint.name", "test");

		new STSClientUtils(m);
	}

	@After
	public void closeContextAfterEachTest() {
	}

}