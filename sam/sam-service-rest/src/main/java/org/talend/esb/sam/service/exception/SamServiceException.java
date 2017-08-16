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
package org.talend.esb.sam.service.exception;

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
