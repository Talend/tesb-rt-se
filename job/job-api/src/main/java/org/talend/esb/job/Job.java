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

import routines.system.TalendJob;

/**
 * A JOB interface for Jobs that are using tESB Components
 */
public interface Job extends TalendJob {

    /**
     * Returns {@link Endpoint} instance
     * that describes the endpoint implemented by given Job.
     *
     * This method should return <code>null</code> if given Job
     * does not have any tESB provider component.
     *
     * @return {@link Endpoint} or null if no provider is configured for the Job
     */
    public Endpoint getEndpoint();

    /**
     * Injecting a {@link EndpointRegistry} to allow
     * tESB Consumer components to lookup and call
     * ESB providers.
     *
     * @param callback
     */
    public void setEndpointRegistry(EndpointRegistry registry);

    /**
     * Injecting a {@link ProviderCallback} to allow
     * tESB Provider components read requests sent to the
     * {@link Job} and write responses from the {@link Job}
     *
     * @param callback
     */
    public void setProviderCallback(ProviderCallback callback);

}
