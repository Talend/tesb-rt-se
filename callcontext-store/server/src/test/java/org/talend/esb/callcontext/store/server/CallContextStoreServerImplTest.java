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
package org.talend.esb.callcontext.store.server;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.talend.esb.callcontext.store.common.exception.CallContextAlreadyExistsException;
import org.talend.esb.callcontext.store.common.exception.CallContextNotFoundException;
import org.talend.esb.callcontext.store.common.exception.PersistencyException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/test-callcontext-store-server.xml" })
public class CallContextStoreServerImplTest {

	@javax.annotation.Resource(name = "callContextStoreServerBean")
	private CallContextStoreServerImpl server;

	private String TMP_PATH = "./target/esbrepo/callcontextstore/fileStore";

	private final static String CONTEXT_STRING = "contextString";
	private final static String KEY = "key";
	
	@Test
	public void saveCallContext() {
		cleanupRepo();
		
		server.saveCallContext(CONTEXT_STRING, KEY);
	}

	@Test
	public void saveCallContextDublicate() {
		String key = null;
		boolean exceptionIsCatched = false;
		try {
			 server.saveCallContext(CONTEXT_STRING, KEY);
		} catch (Exception ex) {
			exceptionIsCatched = true;
			Assert.assertTrue(ex instanceof CallContextAlreadyExistsException);
		} finally {
			Assert.assertTrue(exceptionIsCatched);
			Assert.assertNull(key);
		}
	}

	@Test
	public void lookupCallContext() {
		String ctxLookup = server.lookupCallContext(KEY);
		Assert.assertNotNull(ctxLookup);
		Assert.assertTrue(ctxLookup.equalsIgnoreCase(
				CONTEXT_STRING));
	}

	@Test
	public void lookupCallContextNotExisting() {
		String ctxLookup = null;
		try {
			ctxLookup = server.lookupCallContext(KEY + "does not exist");
		} catch (Exception ex) {
			Assert.assertTrue(ex instanceof PersistencyException);
		} finally {
			Assert.assertNull(ctxLookup);
		}
	}

	@Test
	public void deleteCallContext() {
		server.deleteCallContext(KEY);
	}

	@Test
	public void deleteCallContextNotExisting() {
		boolean exceptionIsCatched = false;
		try {
			server.deleteCallContext(KEY);
		} catch (Exception ex) {
			exceptionIsCatched = true;
			Assert.assertTrue(ex instanceof CallContextNotFoundException);
		} finally {
			Assert.assertTrue(exceptionIsCatched);
		}
	}

	private void cleanupRepo() {
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