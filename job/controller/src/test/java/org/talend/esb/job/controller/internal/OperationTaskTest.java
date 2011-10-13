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

import org.easymock.Capture;
import org.junit.Test;

import routines.system.api.ESBEndpointInfo;
import routines.system.api.ESBEndpointRegistry;
import routines.system.api.ESBJobInterruptedException;
import routines.system.api.ESBProviderCallback;
import routines.system.api.TalendESBJob;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertFalse;

public class OperationTaskTest {

    private static final String[] ARGS = new String[] {"arg1", "arg2"};

    private TalendESBJob job;
    
    @org.junit.Before
    public void startup() {
        job = createMock(TalendESBJob.class);
    }

    @Test
    public void talendESBJobInitializedCorrectly() {
        replay(job);
        new OperationTask(job, ARGS);
        verify(job);
    }

    @Test
    public void runRunsTalendJob() throws Exception {
        Capture<OperationTask> taskCapture = new Capture<OperationTask>();

        job.setProviderCallback(capture(taskCapture));
        expect(job.runJobInTOS(aryEq(ARGS))).andStubReturn(0);
        replay(job);
        
        OperationTask operationTask = new OperationTask(job, ARGS);

        Thread taskThread = new Thread(operationTask);
        taskThread.start();
        taskThread.join(1000);

        assertSame(operationTask, taskCapture.getValue());
        verify(job);
    }

    @Test
    public void runHandlesSeveralRequests() throws Exception {
        OneTimeTalendESBJob oneTimeJob = new OneTimeTalendESBJob(2);
        
        OperationTask operationTask = new OperationTask(oneTimeJob, ARGS);
        Thread taskThread = new Thread(operationTask);
        Thread clientThread1 = createClientThread(operationTask);
        Thread clientThread2 = createClientThread(operationTask);
        
        taskThread.start();
        clientThread1.start();
        clientThread2.start();

        clientThread1.join(1000);
        clientThread2.join(1000);

        operationTask.stop();
        taskThread.join(1000);

        oneTimeJob.validateRuns();
        assertFalse("Thread", taskThread.isAlive());
    }

    @Test
    public void interruptStopsOperationTask() throws Exception {
        OneTimeTalendESBJob oneTimeJob = new OneTimeTalendESBJob(0);
        
        OperationTask operationTask = new OperationTask(oneTimeJob, ARGS);
        Thread taskThread = new Thread(operationTask);

        taskThread.start();
        taskThread.interrupt();

        taskThread.join(1000);

        oneTimeJob.validateRuns();
        assertFalse("Thread", taskThread.isAlive());
    }

    Thread createClientThread(final OperationTask task) {
        return new Thread(
            new Runnable() {
                public void run() {
                    try {
                        task.invoke(new Object(), true);
                    } catch (Exception e) { }
                }
            }
        );
    }
    
    private abstract static  class AbstractTalendESBJob implements TalendESBJob {

        private ESBProviderCallback callback;
        
        private int expectedRuns;
        
        private int actualRuns;
        
        public AbstractTalendESBJob(int runs) {
            expectedRuns = runs;
        }
        
        @Override
        public String[][] runJob(String[] args) {
            return null;
        }

        public int runOnce() {
            try {
                callback.getRequest();
            } catch (ESBJobInterruptedException e) {
                return -1;                
            }
            callback.sendResponse(new Object());
            actualRuns++;
            return 0;
        }

        @Override
        public ESBEndpointInfo getEndpoint() {
            return null;
        }

        @Override
        public void setEndpointRegistry(ESBEndpointRegistry registry) {
        }

        @Override
        public void setProviderCallback(ESBProviderCallback callback) {
            this.callback = callback;
        }
        
        public void validateRuns() {
            if (actualRuns != expectedRuns) {
                throw new IllegalStateException(
                        "Expected " + expectedRuns + " runs, actual " + actualRuns + " runs.");
            }
        }
    }

    public static class OneTimeTalendESBJob extends AbstractTalendESBJob {
        
        OneTimeTalendESBJob(int runs) {
            super(runs);
        }

        @Override
        public int runJobInTOS(String[] args) {
            return runOnce();
        }
    }

    public static class ContinualTalendESBJob extends AbstractTalendESBJob {
        
        ContinualTalendESBJob(int runs) {
            super(runs);
        }

        @Override
        public int runJobInTOS(String[] args) {
            int ret = 0;
            while (ret == 0) {
                ret = runOnce();
            }
            return ret;
        }
    }
}
