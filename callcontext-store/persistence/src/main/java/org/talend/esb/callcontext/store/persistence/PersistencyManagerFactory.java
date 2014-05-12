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

import org.talend.esb.callcontext.store.common.exception.InitializationException;

/**
 * Factory retrieves an instance of PersistencyManager
 * Factory implementation is thread safe.
 * PersistencyManager implementation is not thread safe.
 *
 */
public interface PersistencyManagerFactory {

    /**
     * Creates a new PersistencyManager instance.
     * @param props Custom properties, reserved for future use
     * @return PersistencyManager Instance of PersistencyManager
     * @throws InitializationException if service registry core was not correctly initialized
     */
    PersistencyManager createPersistencyManager() throws InitializationException;

}
