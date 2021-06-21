/*
 * #%L
 * Talend :: ESB :: Job :: Controller
 * %%
 * Copyright (c) 2006-2021 Talend Inc. - www.talend.com
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.talend.esb.job.controller.internal;

import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

import org.talend.esb.job.controller.internal.MessageExchangeBuffer.WorkloadListener;

import routines.system.api.ESBEndpointRegistry;
import routines.system.api.TalendESBJob;

public class SingleInstanceWorkloadStrategy implements WorkloadListener {

    public static final Logger LOG = Logger.getLogger(SingleInstanceWorkloadStrategy.class.getName());

    private final TalendESBJob job;

    private final String name;

    private String[] args;

    private final ESBEndpointRegistry registry;

    private final ExecutorService execService;

    private int instances;

    public SingleInstanceWorkloadStrategy(
            TalendESBJob esbJob,
            String jobName,
            String[] arguments,
            ESBEndpointRegistry endpointRegistry,
            ExecutorService executorService) {

        job = esbJob;
        name = jobName;
        args = arguments;
        registry = endpointRegistry;
        execService = executorService;
    }

    @Override
    public void initialValues(MessageExchangeBuffer buffer, int idleConsumers, int waitingRequests) {
        registerNewJobinstance(buffer);
    }

    @Override
    public void valuesChanged(MessageExchangeBuffer buffer, int idleConsumers, int waitingRequests) {

    }

    private void registerNewJobinstance(MessageExchangeBuffer buffer) {
        job.setEndpointRegistry(registry);
        RuntimeESBProviderCallback callback = new RuntimeESBProviderCallback(buffer, job, name, args);
        job.setProviderCallback(callback);
        execService.execute(callback);
        instances++;

        LOG.info("Created instance " + instances + " of job " + name + ".");
    }
}
