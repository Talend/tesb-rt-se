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
 * Consumer interface for handling calls
 * from ESB Job towards other ESB services
 */
public interface ESBConsumer {

    /**
     * A blocking method to invoke a service inside of the Job
     *
     * @param request Payload of request
     * @return Payload of response
     * @throws Exception In case something goes wrong
     */
    public Object invoke(Object payload) throws Exception;

}
