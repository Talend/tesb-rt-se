/*******************************************************************************
 *  Copyright (c) 2011 Talend Inc. - www.talend.com
 *  All rights reserved.
 *
 *  This program and the accompanying materials are made available
 *  under the terms of the Apache License v2.0
 *  which accompanies this distribution, and is available at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
package org.talend.esb.job;

/**
 * This interface is used by tESB Provider components
 * to get a request from the ESB and to write a response
 * back to ESB.
 */
public interface ProviderCallback {

    /**
     * Returns a request to the Job.
     * This method is <em>blocking</em> it will
     * block Job execution until request will arrive.
     *
     * @return
     */
    Object getRequest() throws JobInterruptedException;

    /**
     * This method will be used by Job to send
     * a response or fault.
     *
     * @param response
     */
    void sendResponse(Object response);

}
