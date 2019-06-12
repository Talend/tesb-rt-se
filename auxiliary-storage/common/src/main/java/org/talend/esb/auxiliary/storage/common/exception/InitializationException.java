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
package org.talend.esb.auxiliary.storage.common.exception;

public class InitializationException extends AuxiliaryStorageException {

    /**
     *
     */
    private static final long serialVersionUID = 721142793414458796L;

    public InitializationException(String message) {
        super(message);
    }

    public InitializationException(String message, Throwable e) {
        super(message, e);
    }

}
