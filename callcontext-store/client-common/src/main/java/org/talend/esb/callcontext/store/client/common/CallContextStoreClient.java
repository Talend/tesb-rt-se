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
package org.talend.esb.callcontext.store.client.common;

import org.talend.esb.callcontext.store.common.CallContextFactory;

/**
 * Public interface for the Registry client.
 */
public interface CallContextStoreClient<E> {

    /**
     * gets CallContext factory
     * @return Call Context factory
     */
	public CallContextFactory<E> getCallContextFactory();
	
    /**
     * sets CallContext factory
     * @param CallContext factory
     */
    public void setCallContextFactory(CallContextFactory<E> factory);
	
    /**
     * lookup call context by context key and policy alias
     * @param context key
     * @return Call Context
     */
    public E getCallContext(String key);
    
    /**
     * save call context in persistence store
     * @param call context
     * @return context key
     */   
    public String saveCallContext(E ctx); 
    
    /**
     * remove call context from persistence store
     * @param call context
     */   
    public void removeCallContext(String key);     
}
