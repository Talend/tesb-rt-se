/*
 * ============================================================================
 *
 * Copyright (c) 2006-2021 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 5/7 rue Salomon De Rothschild, 92150 Suresnes, France
 *
 * ============================================================================
 */
package org.talend.esb.auxiliary.storage.service.rest;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.talend.esb.auxiliary.storage.common.AuxiliaryStorageServer;
import org.talend.esb.auxiliary.storage.common.exception.ObjectNotFoundException;
import org.talend.esb.auxiliary.storage.service.rest.AuxiliaryStorageRestServiceImpl;

public class AuxiliaryStorageRestServiceImplTest extends EasyMockSupport {

	private final static String OBJECT_STRING = "objectString";
	private final static String KEY = "key";

	private AuxiliaryStorageServer server;
	private AuxiliaryStorageRestServiceImpl restService;



	@Before
	public void setUp() {
		Logger.getLogger(
				AuxiliaryStorageRestServiceImpl.class.getPackage().getName())
				.setLevel(Level.FINE);
		server = createMock(AuxiliaryStorageServer.class);
		restService = new AuxiliaryStorageRestServiceImpl();
		restService.setAuxiliaryStorageServer(server);
	}

	@Test
	public void lookupObjectTest() {
		server.lookupObject(KEY);
		EasyMock.expectLastCall().andStubReturn(OBJECT_STRING);
		replayAll();

		String ctxLookup = restService.lookup(KEY);

		Assert.assertNotNull(ctxLookup);
		Assert.assertTrue(ctxLookup.equalsIgnoreCase(
				OBJECT_STRING));
		verifyAll();
	}

	@Test
	public void lookupObjectNotFoundTest() {
		server.lookupObject(KEY);
		EasyMock.expectLastCall().andStubReturn(null);
		replayAll();

		String ctxLookup = null;
		try{
			ctxLookup = restService.lookup(KEY);
		}catch(Exception ex){
			 Assert.assertTrue(ex instanceof ObjectNotFoundException);
		}
		Assert.assertNull(ctxLookup);

		verifyAll();
	}

	@Test
	public void deleteObjectNotExistingTest() {
		server.deleteObject(KEY);
		EasyMock.expectLastCall().andStubThrow(new ObjectNotFoundException("Object is not found"));
		replayAll();

		try{
			restService.remove(KEY);
		}catch(Exception ex){
			 Assert.assertTrue(ex instanceof ObjectNotFoundException);
		}
		verifyAll();
	}
}
