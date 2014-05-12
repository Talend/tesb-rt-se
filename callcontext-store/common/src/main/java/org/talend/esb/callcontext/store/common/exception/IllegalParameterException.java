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
package org.talend.esb.callcontext.store.common.exception;

public class IllegalParameterException extends CallContextStoreException {

    /**
     *
     */
    private static final long serialVersionUID = -5679973253162838190L;

    public IllegalParameterException(String message) {
        super(message);
    }

    public IllegalParameterException(String message, Throwable e) {
        super(message, e);
    }

}
