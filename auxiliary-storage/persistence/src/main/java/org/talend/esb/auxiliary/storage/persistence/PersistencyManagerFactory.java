/*
 * ============================================================================
 *
 * Copyright (C) 2011-2019 Talend Inc. - www.talend.com
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
package org.talend.esb.auxiliary.storage.persistence;

import org.talend.esb.auxiliary.storage.common.exception.InitializationException;

/**
 * Factory retrieves an instance of PersistencyManager
 * Factory implementation is thread safe.
 * PersistencyManager implementation is not thread safe.
 *
 */
public interface PersistencyManagerFactory {

    /**
     * Creates a new PersistencyManager instance.
     * @return PersistencyManager Instance of PersistencyManager
     * @throws InitializationException if persistency manager was not correctly initialized
     */
    PersistencyManager createPersistencyManager() throws InitializationException;

}
