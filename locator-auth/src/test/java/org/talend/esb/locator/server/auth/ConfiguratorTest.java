package org.talend.esb.locator.server.auth;

import org.junit.Assert;
import org.junit.Test;

public class ConfiguratorTest {

	@Test
	public void testHandleAuthentication() throws Exception {

		Assert.assertNull(System.getProperty("zookeeper.authProvider.serviceLocator"));
		new Configurator();
		Assert.assertNotNull(System.getProperty("zookeeper.authProvider.serviceLocator"));

		Assert.assertSame("org.talend.esb.locator.server.auth.SLAuthenticationProvider",
				System.getProperty("zookeeper.authProvider.serviceLocator"));

	}

}