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

import java.util.Map;

/**
 * Describes the generic Endpoint
 */
public interface Endpoint {

    /**
     * Returns a component identifier that should be used
     * to be configured with given {@link Endpoint}
     *
     * @return a non-null {@link String} unique for type of endpoint
     */
    public String getEndpointKey();

    /**
     * Returns a URI String for the endpoint.
     * This URI should be understood by the consumer
     * with given {@link #getEndpointKey()}
     *
     * @return a non-null {@link String}, ideally a URI
     */
    public String getEndpointUri();

    /**
     * Additional endpoint properties that
     * would be required to configure endpoint
     *
     * @return
     */
    public Map<String, Object> getEndpointProperties();

}
