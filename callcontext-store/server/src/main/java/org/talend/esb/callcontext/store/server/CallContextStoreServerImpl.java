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

import org.talend.esb.callcontext.store.common.CallContextStoreServer;
import org.talend.esb.callcontext.store.common.exception.CallContextStoreException;
import org.talend.esb.callcontext.store.persistence.PersistencyManager;


/**
 * the main interface for Call Context Store Server
 *
 */
public class CallContextStoreServerImpl implements  CallContextStoreServer {
	
	private PersistencyManager persistencyManager;
	
	@Override
	public void deleteCallContext(String key) 
			throws CallContextStoreException {
		
		checkPersistencyManager(true);
		
		persistencyManager.removeCallContext(key);
	}

	@Override
	public String lookupCallContext(String key)
			throws CallContextStoreException {
		
		checkPersistencyManager(true);
		
		String ctx =  persistencyManager.restoreCallContext(key);
		
		return ctx;
	}

	@Override
	public void saveCallContext(String ctx, String ctxKey)
			throws CallContextStoreException {
		
		checkPersistencyManager(true);
		
		persistencyManager.storeCallContext(ctx, ctxKey);
	}
	
	
	public PersistencyManager getPersistencyManager() {
		return persistencyManager;
	}

	public void setPersistencyManager(PersistencyManager persistencyManager) {
		this.persistencyManager = persistencyManager;
	}
	
	private boolean checkPersistencyManager(boolean throwExceptionIfNotExists) 
		throws CallContextStoreException {
		if(persistencyManager==null) {
			if(throwExceptionIfNotExists){
				throw new CallContextStoreException("Persistency manager is not set");
			}
			return false;
		}
		return true;
			
	}
	
	
	
}
