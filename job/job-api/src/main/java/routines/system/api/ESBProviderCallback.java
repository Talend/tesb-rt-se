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
 * This interface is used by provider component
 * to get a request from the ESB
 * and to write a response back to ESB.
 */
public interface ESBProviderCallback {

    /**
     * Returns a request to the Job.
     * This method is <em>blocking</em> it will
     * block Job execution until request will arrive.
     *
     * @return
     */
    Object getRequest() throws ESBJobInterruptedException;

    /**
     * This method will be used by Job to send
     * a response or fault.
     *
     * @param response
     */
    void sendResponse(Object response);

}
