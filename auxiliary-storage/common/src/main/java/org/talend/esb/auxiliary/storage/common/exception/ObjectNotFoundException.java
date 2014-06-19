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
package org.talend.esb.auxiliary.storage.common.exception;

public class ObjectNotFoundException extends AuxiliaryStorageException {

    /**
     *
     */
    private static final long serialVersionUID = -5067314390133284219L;

    public ObjectNotFoundException(String message) {
        super(message);
    }

    public ObjectNotFoundException(String message, Throwable e) {
        super(message, e);
    }

}
