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

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.talend.esb.job.controller.GenericOperation;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import routines.system.api.ESBEndpointInfo;
import routines.system.api.TalendESBJob;

public class JobLauncherRetrieveOperationTest {

    public static final String JOB_NAME_1 = "jobName1";

    public static final String JOB_NAME_2 = "jobName2";

    public static final String[] EMPTY_STRING_ARR = new String[0];

    private TalendESBJob job;

    private JobLauncherImpl jobLauncher;

    @Before
    public void setUp() {
        ExecutorService execService = createNiceMock(ExecutorService.class);
        ESBEndpointInfo endpointInfo = createNiceMock(ESBEndpointInfo.class);
        job = createNiceMock(TalendESBJob.class);
        expect(job.getEndpoint()).andStubReturn(endpointInfo);
        replay(job, execService);

        jobLauncher = new JobLauncherImpl();
        jobLauncher.setExecutorService(execService);
        jobLauncher.esbJobAdded(job, JOB_NAME_1);
    }

    @Test
    public void retrieveNewOperation() throws Exception {
        GenericOperation operation = jobLauncher.retrieveOperation(JOB_NAME_1, new String[0]);

        assertNotNull(operation);
    }

    @Test
    @Ignore
    public void retrieveSecondTimeOperationReturnsSame() throws Exception {
        GenericOperation operation1 = jobLauncher.retrieveOperation(JOB_NAME_1, EMPTY_STRING_ARR);
        GenericOperation operation2 = jobLauncher.retrieveOperation(JOB_NAME_1, EMPTY_STRING_ARR);

        assertSame(operation1, operation2);
    }

    @Test
    public void jobNotAvailable() throws Exception {
        try {
            jobLauncher.retrieveOperation(JOB_NAME_2, EMPTY_STRING_ARR);
            fail("An IllegalArgumentException should have been thrown");
        } catch (IllegalArgumentException e) {
        }
    }
}
