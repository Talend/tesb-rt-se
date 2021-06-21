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
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;


import routines.system.api.ESBEndpointRegistry;
import routines.system.api.TalendESBJob;
import routines.system.api.TalendESBJobFactory;

public class MultiInstanceWorkloadStrategyTest extends EasyMockSupport {

    private static final String JOB_NAME = "job1";

    private static final String[] ARGUMENTS = new String[0];

    private static final int ANY_VAL = 34234;

    private static final int NOT_ZERO = 15283;

    private static final int ZERO = 0;

    private ESBEndpointRegistry endpointRegistry = createNiceMock(ESBEndpointRegistry.class);

    private TalendESBJobFactory jobFactory = createMock(TalendESBJobFactory.class);

    private MessageExchangeBuffer buffer = createNiceMock(MessageExchangeBuffer.class);

    private ExecutorService execService = createMock(ExecutorService.class);

    @Test
    public void strategyCreatedNothinHappens() {
        replayAll();

        new MultiInstanceWorkloadStrategy(jobFactory, JOB_NAME, ARGUMENTS, endpointRegistry, execService);

        verifyAll();
    }

    @Test
    public void whenInitializedOneInstanceCreated() {
        mockJobInstanceCreation();
        replayAll();

        MultiInstanceWorkloadStrategy strategy =
            new MultiInstanceWorkloadStrategy(jobFactory, JOB_NAME, ARGUMENTS, endpointRegistry, execService);
        strategy.initialValues(buffer, ANY_VAL, ANY_VAL);

        verifyAll();
    }

    @Test
    public void noIdleJobWhenValueChangedOneInstanceCreated() {
        mockJobInstanceCreation();
        replayAll();

        MultiInstanceWorkloadStrategy strategy =
            new MultiInstanceWorkloadStrategy(jobFactory, JOB_NAME, ARGUMENTS, endpointRegistry, execService);
        strategy.valuesChanged(buffer, ZERO, ANY_VAL);

        verifyAll();

    }

    @Test
    public void idleJobExistWhenValueChangedNoInstanceCreated() {
        replayAll();

        MultiInstanceWorkloadStrategy strategy =
            new MultiInstanceWorkloadStrategy(jobFactory, JOB_NAME, ARGUMENTS, endpointRegistry, execService);
        strategy.valuesChanged(buffer, NOT_ZERO, ANY_VAL);

        verifyAll();

    }

    private void mockJobInstanceCreation() {
        TalendESBJob job = createMock(TalendESBJob.class);
        job.setEndpointRegistry(endpointRegistry);
        job.setProviderCallback((RuntimeESBProviderCallback) anyObject());
        expect(jobFactory.newTalendESBJob()).andReturn(job);
        execService.execute(isA(RuntimeESBProviderCallback.class));
    }

}
