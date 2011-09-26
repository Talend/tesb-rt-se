/*
 * #%L
 * Talend :: ESB :: Job :: Controller
 * %%
 * Copyright (C) 2011 Talend Inc.
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

import org.easymock.EasyMockSupport;
import org.junit.Test;
import org.talend.esb.job.controller.GenericOperation;

import routines.system.api.TalendESBJob;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

public class JobLauncherImplTest extends EasyMockSupport{

    public static final String JOB_NAME = "jobName";

    @Test
    public void retrieveNewOperation() throws Exception {
        TalendESBJob job = createNiceMock(TalendESBJob.class);
        replayAll();
        
        JobLauncherImpl jobLauncher = new JobLauncherImpl();
        jobLauncher.esbJobAdded(job, JOB_NAME);

        GenericOperation operation = jobLauncher.retrieveOperation(JOB_NAME, true, new String[0]);
        
        assertNotNull(operation);
    }

    @Test
    public void retrieveSecondTimeOperationReturnsSame() throws Exception {
        TalendESBJob job = createNiceMock(TalendESBJob.class);
        replayAll();
        
        JobLauncherImpl jobLauncher = new JobLauncherImpl();
        jobLauncher.esbJobAdded(job, JOB_NAME);

        GenericOperation operation1 = jobLauncher.retrieveOperation(JOB_NAME, true, new String[0]);
        GenericOperation operation2 = jobLauncher.retrieveOperation(JOB_NAME, true, new String[0]);
        
        assertSame(operation1, operation2);
    }

    @Test
    public void jobNotAvailable() throws Exception {        
        JobLauncherImpl jobLauncher = new JobLauncherImpl();

        try {
            jobLauncher.retrieveOperation(JOB_NAME, true, new String[0]);
            fail("An IllegalArgumentException should have been thrown");
        } catch(IllegalArgumentException e) {}
    }
}
