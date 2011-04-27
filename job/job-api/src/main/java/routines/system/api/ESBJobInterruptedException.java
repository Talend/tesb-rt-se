/*******************************************************************************
 *  Copyright (c) 2011 Talend Inc. - www.talend.com
 *  All rights reserved.
 *
 *  This program and the accompanying materials are made available
 *  under the terms of the Apache License v2.0
 *  which accompanies this distribution, and is available at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 ******************************************************************************/
package routines.system.api;

/**
 * A special type of exception
 * that will signal job waiting for request
 * that it should quit it's listening cycle (if any)
 */
public class ESBJobInterruptedException extends Exception {

    /**
     * Generated SV UID
     */
    private static final long serialVersionUID = -1570949226819610043L;

    /**
     * Constructor from parent class
     *
     * @param message
     */
    public ESBJobInterruptedException(String message) {
        super(message);
    }

    /**
     * Constructor from parent class
     *
     * @param message
     */
    public ESBJobInterruptedException(String message, Throwable e) {
        super(message, e);
    }

}
