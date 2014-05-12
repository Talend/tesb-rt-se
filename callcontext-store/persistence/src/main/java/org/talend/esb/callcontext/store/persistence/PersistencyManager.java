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
package org.talend.esb.callcontext.store.persistence;

import org.talend.esb.callcontext.store.common.exception.PersistencyException;



public interface PersistencyManager {

    /**
     * Saves call context.
     *
     * @param context Context to be stored.
     *
     * @param Stored context key.
     *
     * @throws CallbackCallContextStoreException Thrown exception indicates that there was an error
     *         during context store operation and context probably wasn't saved.
     */
    void storeCallContext(String context, String key) throws PersistencyException;



    /**
     * Restores call context with the given key. If context is not
     * in multi-callback mode, it will be removed automatically after restore.
     *
     * @param contextKey Key for context to be restored
     * @return Restored call context or <code>null</code> in case when
     *         no context with the given key is stored.
     * @throws CallbackCallContextStoreException In case of restore failure.
     */
    String restoreCallContext(String contextKey) throws PersistencyException;


    /**
     * Removes the call context with the given store key.
     *
     * @param key Storage key of call context to be removed.
     */
    void removeCallContext(String contextKey) throws PersistencyException;
}
