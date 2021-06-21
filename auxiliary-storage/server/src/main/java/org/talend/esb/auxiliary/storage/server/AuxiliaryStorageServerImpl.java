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
package org.talend.esb.auxiliary.storage.server;

import org.talend.esb.auxiliary.storage.common.AuxiliaryStorageServer;
import org.talend.esb.auxiliary.storage.common.exception.AuxiliaryStorageException;
import org.talend.esb.auxiliary.storage.persistence.PersistencyManager;


/**
 * the main interface for Auxiliary Storage Server
 *
 */
public class AuxiliaryStorageServerImpl implements  AuxiliaryStorageServer {

	private PersistencyManager persistencyManager;

	@Override
	public void deleteObject(String key)
			throws AuxiliaryStorageException {

		checkPersistencyManager(true);

		persistencyManager.removeObject(key);
	}

	@Override
	public String lookupObject(String key)
			throws AuxiliaryStorageException {

		checkPersistencyManager(true);

		String ctx =  persistencyManager.restoreObject(key);

		return ctx;
	}

	@Override
	public void saveObject(String ctx, String ctxKey)
			throws AuxiliaryStorageException {

		checkPersistencyManager(true);

		persistencyManager.storeObject(ctx, ctxKey);
	}


	public PersistencyManager getPersistencyManager() {
		return persistencyManager;
	}

	public void setPersistencyManager(PersistencyManager persistencyManager) {
		this.persistencyManager = persistencyManager;
	}

	private boolean checkPersistencyManager(boolean throwExceptionIfNotExists)
		throws AuxiliaryStorageException {
		if(persistencyManager==null) {
			if(throwExceptionIfNotExists){
				throw new AuxiliaryStorageException("Persistency manager is not set");
			}
			return false;
		}
		return true;

	}



}
