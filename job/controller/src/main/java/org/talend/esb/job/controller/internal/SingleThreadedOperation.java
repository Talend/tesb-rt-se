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
import java.util.concurrent.atomic.AtomicBoolean;

import org.talend.esb.job.controller.GenericOperation;

import routines.system.api.ESBEndpointRegistry;
import routines.system.api.TalendESBJob;

/**
 * Operation backed by exactly one {@link TalendESBJob job} instance.
 *
 */
public class SingleThreadedOperation extends AbstractOperation implements GenericOperation {

    private final ExecutorService execService;

    private final  ESBEndpointRegistry endpointRegistry;

    private final TalendESBJob job;

    private final String name;

    private AtomicBoolean started = new AtomicBoolean(false);

    /**
     * Create a <code>SingleThreadedOperation</code>.
     *
     * @param esbJob job instance  backing this operation, must not be <code>null</code>
     * @param executorService executor service to start job in separate Thread, must not be <code>null</code>
     */
    public SingleThreadedOperation(
            TalendESBJob esbJob,
            String jobName,
            ESBEndpointRegistry esbEndpointRegistry,
            ExecutorService executorService) {
        job = esbJob;
        name = jobName;
        endpointRegistry = esbEndpointRegistry;
        execService = executorService;
    }

    /**
     * Starts the one and only job instance in a separate Thread. Should be called exactly one time before
     * the operation is stopped.
     *
     * @param arguments {@inheritDoc}
     */
    @Override
    public void start(String[] arguments) {
        boolean notStarted = !started.getAndSet(true);
        if (notStarted) {
            start(new SingleInstanceWorkloadStrategy(job, name, arguments, endpointRegistry, execService));
        }
    }
}
