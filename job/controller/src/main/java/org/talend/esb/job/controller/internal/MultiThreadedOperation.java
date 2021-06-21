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
import java.util.logging.Logger;

import org.talend.esb.job.controller.GenericOperation;

import routines.system.api.ESBEndpointRegistry;
import routines.system.api.TalendESBJobFactory;

/**
 * Operation backed by potentially several concurrently running {@link TalendESBJob job} instances.
 *
 */
public class MultiThreadedOperation extends AbstractOperation implements GenericOperation {

    public static final Logger LOG = Logger.getLogger(MultiThreadedOperation.class.getName());

    private final ExecutorService execService;

    private final TalendESBJobFactory factory;

    private final String name;

    private final ESBEndpointRegistry endpointRegistry;

    private AtomicBoolean started = new AtomicBoolean(false);

    /**
     * Create a <code>MultiThreadedOperation</code>.
     *
     * @param jobFactory job instance  backing this operation, must not be <code>null</code>
     * @param jobName
     * @param esbEndpointRegistry
     * @param executorService executor service to start job in separate Thread, must not be <code>null</code>
     */
    public MultiThreadedOperation(TalendESBJobFactory jobFactory, String jobName,
            ESBEndpointRegistry esbEndpointRegistry, ExecutorService executorService) {
        factory = jobFactory;
        name = jobName;
        endpointRegistry = esbEndpointRegistry;
        execService = executorService;
    }

    /**
     * Start the operation by instantiating the first job instance in a separate Thread.
     *
     * @param arguments {@inheritDoc}
     */
    @Override
    public void start(String[] arguments) {
        boolean notStarted = !started.getAndSet(true);
        if (notStarted) {
            start(new MultiInstanceWorkloadStrategy(factory, name, arguments, endpointRegistry, execService));
        }
    }
}
