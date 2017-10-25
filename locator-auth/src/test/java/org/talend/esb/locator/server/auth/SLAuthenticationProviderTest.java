package org.talend.esb.locator.server.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.server.ServerCnxn;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import org.junit.Assert;
import static org.junit.Assert.assertSame;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class SLAuthenticationProviderTest {

	@Test
	public void testHandleAuthentication() throws Exception {

		SLAuthenticationProvider p = new SLAuthenticationProvider();
		assertTrue(p.isAuthenticated());
		assertSame("sl", p.getScheme());

		assertTrue(p.isValid("id"));
		assertFalse(p.isValid(""));
		assertTrue(p.matches("test,test1,test2", "test1"));
		assertFalse(p.matches("test,test1,test2", "test3"));

	}

	@Test
	public void testHandleAuthenticationFailedLogin() throws Exception {

		SLAuthenticationProvider p = new SLAuthenticationProvider();
		ServerCnxn cnxn = createNiceMock(ServerCnxn.class);

		// no password
		byte[] authData = "tadmin".getBytes();

		p.handleAuthentication(cnxn, authData);

	}

}