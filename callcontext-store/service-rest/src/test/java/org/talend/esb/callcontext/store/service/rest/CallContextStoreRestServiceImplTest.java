/*
 * ============================================================================
 *
 * Copyright (C) 2011 - 2013 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 *
 * ============================================================================
 */
package org.talend.esb.callcontext.store.service.rest;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.talend.esb.callcontext.store.common.CallContextStoreServer;
import org.talend.esb.callcontext.store.common.exception.CallContextNotFoundException;

public class CallContextStoreRestServiceImplTest extends EasyMockSupport {

	private final static String CONTEXT_STRING = "contextString";
	private final static String KEY = "key";

	private CallContextStoreServer server;
	private CallContextStoreRestServiceImpl restService;


	
	@Before
	public void setUp() {
		Logger.getLogger(
				CallContextStoreRestServiceImpl.class.getPackage().getName())
				.setLevel(Level.FINE);
		server = createMock(CallContextStoreServer.class);
		restService = new CallContextStoreRestServiceImpl();
		restService.setCallContextStoreServer(server);
	}

	@Test
	public void lookupCallContextTest() {
		server.lookupCallContext(KEY);
		EasyMock.expectLastCall().andStubReturn(CONTEXT_STRING);
		replayAll();

		String ctxLookup = restService.lookup(KEY);

		Assert.assertNotNull(ctxLookup);
		Assert.assertTrue(ctxLookup.equalsIgnoreCase(
				CONTEXT_STRING));
		verifyAll();
	}

	@Test
	public void lookupContextNotFoundTest() {
		server.lookupCallContext(KEY);
		EasyMock.expectLastCall().andStubReturn(null);
		replayAll();

		String ctxLookup = null;
		try{
			ctxLookup = restService.lookup(KEY);
		}catch(Exception ex){
			 Assert.assertTrue(ex instanceof CallContextNotFoundException);
		}
		Assert.assertNull(ctxLookup);

		verifyAll();
	}
	
	@Test
	public void deleteContextNotExistingTest() {
		server.deleteCallContext(KEY);
		EasyMock.expectLastCall().andStubThrow(new CallContextNotFoundException("Call contest is not found"));
		replayAll();

		try{
			restService.remove(KEY);
		}catch(Exception ex){
			 Assert.assertTrue(ex instanceof CallContextNotFoundException);
		}
		verifyAll();
	}	
}
