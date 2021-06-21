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
package org.talend.esb.auxiliary.storage.common;

import org.talend.esb.auxiliary.storage.common.exception.AuxiliaryStorageException;

/**
 * the main interface for Auxiliary Storage Server
 *
 */
public interface AuxiliaryStorageServer {

    /**
     * Upload object to repository.
     * @param object object to be uploaded
     * @param Context key of the stored object
     * @throws AuxiliaryStorageException
     */
    void saveObject(final String object, final String key) throws AuxiliaryStorageException;


    /**
     * Lookup stored object providing its context key
     * @param key The key of the stored object
     * @return Stored object
     * @throws AuxiliaryStorageException
     */
     String lookupObject(final String key) throws AuxiliaryStorageException;


    /**
     * Delete stored object by the given context key.
     * @param key The key of stored object
     * @throws RegistryException
     */
    void deleteObject(final String key) throws AuxiliaryStorageException;

}
