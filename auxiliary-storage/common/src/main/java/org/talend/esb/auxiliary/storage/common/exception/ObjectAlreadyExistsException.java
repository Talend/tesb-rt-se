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

public class ObjectAlreadyExistsException extends AuxiliaryStorageException {

    /**
     *
     */
    private static final long serialVersionUID = 2918875493282823512L;

    public ObjectAlreadyExistsException(String message) {
        super(message);
    }

    public ObjectAlreadyExistsException(String message, Throwable e) {
        super(message, e);
    }

}
