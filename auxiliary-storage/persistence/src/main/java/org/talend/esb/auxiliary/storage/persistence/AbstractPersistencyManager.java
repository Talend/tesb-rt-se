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

import java.util.logging.Logger;

import org.talend.esb.auxiliary.storage.persistence.PersistencyManager;

public abstract class AbstractPersistencyManager implements PersistencyManager {

    protected static final Logger LOG = Logger.getLogger(AbstractPersistencyManager.class.getName());

}
