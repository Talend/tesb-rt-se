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
 * Consumer interface for handling calls
 * from ESB Job towards other ESB services
 */
public interface Consumer {

    /**
     * A blocking method to invoke a service ouside of the Job
     *
     * @param request Payload of request
     * @return Payload of response
     * @throws Exception In case something goes wrong
     */
    public Object invoke(Object payload) throws Exception;

}
