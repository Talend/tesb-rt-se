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

import org.easymock.EasyMockSupport;
import org.junit.Test;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.isA;

import routines.system.api.ESBEndpointRegistry;
import routines.system.api.TalendESBJob;

public class SingleInstanceWorkloadStrategyTest extends EasyMockSupport {

    private static final String JOB_NAME = "job1";

    private static final String[] ARGUMENTS = new String[0];

    private ESBEndpointRegistry endpointRegistry = createNiceMock(ESBEndpointRegistry.class);

    private  TalendESBJob job = createMock(TalendESBJob.class);

    private MessageExchangeBuffer buffer = createNiceMock(MessageExchangeBuffer.class);

    private ExecutorService execService = createMock(ExecutorService.class);

    @Test
    public void strategyCreatedNothinHappens() {
        replayAll();

        new SingleInstanceWorkloadStrategy(job, JOB_NAME, ARGUMENTS, endpointRegistry, execService);

        verifyAll();
    }

    @Test
    public void whenInitializedOneInstanceCreated() {
        mockJobInstanceCreation();
        replayAll();

        SingleInstanceWorkloadStrategy strategy =
            new SingleInstanceWorkloadStrategy(job, JOB_NAME, ARGUMENTS, endpointRegistry, execService);
        strategy.initialValues(buffer, 0, 0);

        verifyAll();
    }

    @Test
    public void valueChangedCallDoesNothing() {
        replayAll();

        SingleInstanceWorkloadStrategy strategy =
                new SingleInstanceWorkloadStrategy(job, JOB_NAME, ARGUMENTS, endpointRegistry, execService);
        strategy.valuesChanged(buffer, 1, 0);

        verifyAll();

    }

    private void mockJobInstanceCreation() {
        job.setEndpointRegistry(endpointRegistry);
        job.setProviderCallback((RuntimeESBProviderCallback) anyObject());
        execService.execute(isA(RuntimeESBProviderCallback.class));
    }

}
