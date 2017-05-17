/*******************************************************************************
 *  Copyright (c) 2011-2017 Talend Inc. - www.talend.com
 *  All rights reserved.
 *
 *  This program and the accompanying materials are made available
 *  under the terms of the Apache License v2.0
 *  which accompanies this distribution, and is available at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 ******************************************************************************/
package routines.system.api;

import org.apache.camel.Exchange;

/**
 * Talend ESB Job with lifecycle support for multiple
 * invocation from a route while keeping external
 * resources likd DB connections.
 */
public interface TalendESBJobBean {

    /**
     * Bean Initializer
     *
     * @param contextArgs
     */
    void prepareJob(String[] contextArgs);

    /**
     * Bean Destructor
     */
    void discardJob();

    /**
     * Run as a single-use job
     * 
     * @param exchange
     * @param contextArgs
     */
    void runSingleUseJob(Exchange exchange, String[] contextArgs);

    /**
     * Run as a prepared multi-use job
     * 
     * @param exchange
     * @param contextArgs
     */
    void runPreparedJob(Exchange exchange, String[] contextArgs);

    /**
     * Get the class of the underlying job
     *
     * @return the job class
     */
    Class<?> getJobClass();
}
