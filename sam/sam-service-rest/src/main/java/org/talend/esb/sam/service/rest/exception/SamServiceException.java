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
package org.talend.esb.sam.service.rest.exception;

public class SamServiceException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 8902450708514783397L;

    public SamServiceException(String message) {
        super(message);
    }

    public SamServiceException(String string, Throwable e) {
        super(string, e);
    }

}
