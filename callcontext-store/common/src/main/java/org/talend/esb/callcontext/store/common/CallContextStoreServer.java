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
package org.talend.esb.callcontext.store.common;

import org.talend.esb.callcontext.store.common.exception.CallContextStoreException;

/**
 * the main interface for Call Context Store Server
 *
 */
public interface CallContextStoreServer {
	
    /**
     * Upload the call context to repository.
     * @param callContext call context to be uploaded
     * @param The key of call context
     * @throws CallContextStoreException
     */
    void saveCallContext(final String callContext, final String key) throws CallContextStoreException;
    

    /**
     * Lookup call context the specified call context key
     * @param key The key of call context
     * call context from persistence store
     * @return The call context object
     * @throws CallContextStoreException
     */
     String lookupCallContext(final String key) throws CallContextStoreException;
    
    
    /**
     * Delete call context by the given call context key.
     * @param key The key of call context
     * @throws RegistryException
     */
    void deleteCallContext(final String key) throws CallContextStoreException;    

}
