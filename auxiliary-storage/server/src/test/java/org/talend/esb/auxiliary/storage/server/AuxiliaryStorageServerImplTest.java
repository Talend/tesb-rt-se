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
package org.talend.esb.auxiliary.storage.server;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.talend.esb.auxiliary.storage.common.exception.ObjectAlreadyExistsException;
import org.talend.esb.auxiliary.storage.common.exception.ObjectNotFoundException;
import org.talend.esb.auxiliary.storage.common.exception.PersistencyException;
import org.talend.esb.auxiliary.storage.server.AuxiliaryStorageServerImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/test-auxiliary-storage-server.xml" })
public class AuxiliaryStorageServerImplTest {

	@javax.annotation.Resource(name = "auxiliaryStorageServerBean")
	private AuxiliaryStorageServerImpl server;

	private final static String TMP_PATH =  "./target/esbrepo/auxiliarystorage/fileStore";
	private final static String CONTEXT_STRING = "contextString";
	private final static String KEY = "key";

	@BeforeClass
	public static void init() {
		cleanupRepo();
	}


	@Test
	public void test() {
		saveObject();
		saveObjectDublicate();
		lookupObject();
		deleteObject();
		lookupObjectNotExisting();
		deleteObjectNotExisting();
	}


	@AfterClass
	public static void cleanup() {
		cleanupRepo();
	}

	private void saveObject() {
		server.saveObject(CONTEXT_STRING, KEY);
	}


	private void saveObjectDublicate() {
		String key = null;
		boolean exceptionCaught = false;
		try {
			 server.saveObject(CONTEXT_STRING, KEY);
		} catch (Exception ex) {
			exceptionCaught = true;
			Assert.assertTrue(ex instanceof ObjectAlreadyExistsException);
		} finally {
			Assert.assertTrue(exceptionCaught);
			Assert.assertNull(key);
		}
	}


	private void lookupObject() {
		String ctxLookup = server.lookupObject(KEY);
		Assert.assertNotNull(ctxLookup);
		Assert.assertTrue(ctxLookup.equalsIgnoreCase(
				CONTEXT_STRING));
	}


	private void lookupObjectNotExisting() {
		String ctxLookup = null;
		try {
			ctxLookup = server.lookupObject(KEY + "does not exist");
		} catch (Exception ex) {
			Assert.assertTrue(ex instanceof PersistencyException);
		} finally {
			Assert.assertNull(ctxLookup);
		}
	}


	private void deleteObject() {
		server.deleteObject(KEY);
	}


	private void deleteObjectNotExisting() {
		boolean exceptionCaught = false;
		try {
			server.deleteObject(KEY);
		} catch (Exception ex) {
			exceptionCaught = true;
			Assert.assertTrue(ex instanceof ObjectNotFoundException);
		} finally {
			Assert.assertTrue(exceptionCaught);
		}
	}


	private static void cleanupRepo() {
		File tempFolder = new File(TMP_PATH);
		if (tempFolder.exists()) {
			try {
				FileUtils.cleanDirectory(tempFolder);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}