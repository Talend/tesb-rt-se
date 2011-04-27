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
 * Consumer callback to allow consumer components
 * to send requests and receive responses
 */
public interface ESBEndpointRegistry {

    /**
     * This method will create consumer based on the
     *
     * @param endpoint
     * @return
     */
    public ESBConsumer createConsumer(ESBEndpointInfo endpoint);

}
