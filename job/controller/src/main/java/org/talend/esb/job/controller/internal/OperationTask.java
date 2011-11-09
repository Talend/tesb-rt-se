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

import org.talend.esb.job.controller.GenericOperation;

import routines.system.api.TalendESBJob;

public class OperationTask extends RuntimeESBProviderCallback implements JobTask, GenericOperation {

    private TalendESBJob job;
    
    private String[] arguments;
    
    public OperationTask(TalendESBJob job, String[] arguments) {
        this.job = job;
        this.arguments = arguments;
    }

    public void run() {
        ClassLoader oldContextCL = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(job.getClass().getClassLoader());
            job.setProviderCallback(this);

            while (true) {
                if (Thread.interrupted()) {
                    prepareStop();
                }
                job.runJobInTOS(arguments);
                if (isStopped()) {
                    return;
                }
            }
        } finally {
            Thread.currentThread().setContextClassLoader(oldContextCL);            
        }
    }
}
