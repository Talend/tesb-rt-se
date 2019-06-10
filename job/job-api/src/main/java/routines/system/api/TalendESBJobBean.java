/*******************************************************************************
 *  Copyright (c) 2011-2019 Talend Inc. - www.talend.com
 *  All rights reserved.
 *
 *  This program and the accompanying materials are made available
 *  under the terms of the Apache License v2.0
 *  which accompanies this distribution, and is available at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 ******************************************************************************/
package routines.system.api;

import java.util.Map;

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
     * @param exchangeData
     * @param contextArgs
     */
    void runSingleUseJob(Map<String, Object> exchangeData, String[] contextArgs);

    /**
     * Run as a prepared multi-use job
     *
     * @param exchangeData
     * @param contextArgs
     */
    void runPreparedJob(Map<String, Object> exchangeData, String[] contextArgs);

    /**
     * Get the class of the underlying job
     *
     * @return the job class
     */
    Class<?> getJobClass();
}
