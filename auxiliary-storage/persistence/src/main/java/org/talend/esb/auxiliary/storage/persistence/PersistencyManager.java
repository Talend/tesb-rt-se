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
package org.talend.esb.auxiliary.storage.persistence;

import org.talend.esb.auxiliary.storage.common.exception.PersistencyException;



public interface PersistencyManager {

    /**
     * Saves object.
     *
     * @param object Object to be stored.
     *
     * @param Stored object's context key.
     *
     * @throws AuxiliaryStorageException Thrown exception indicates that there was an error
     *         during store operation and object probably wasn't saved.
     */
    void storeObject(String object, String key) throws PersistencyException;



    /**
     * Restores stored object with the given key.
     *
     * @param key Key for object to be restored
     * @return Restored object or <code>null</code> in case when
     *         no object with the given key is stored.
     * @throws AuxiliaryStorageException In case of restore failure.
     */
    String restoreObject(String key) throws PersistencyException;


    /**
     * Removes object with the given key from the storage.
     *
     * @param key Storage key of the object to be removed.
     */
    void removeObject(String key) throws PersistencyException;
}
