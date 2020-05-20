/*
 * ============================================================================
 *
 * Copyright (C) 2011-2020 Talend Inc. - www.talend.com
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

import java.util.logging.Logger;

import org.talend.esb.auxiliary.storage.persistence.PersistencyManager;

public abstract class AbstractPersistencyManager implements PersistencyManager {

    protected static final Logger LOG = Logger.getLogger(AbstractPersistencyManager.class.getName());

}
