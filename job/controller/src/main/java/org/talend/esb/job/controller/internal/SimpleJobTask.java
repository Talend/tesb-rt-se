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

import java.util.Dictionary;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

import routines.system.api.TalendJob;

public class SimpleJobTask implements ManagedService, JobTask  {

    private static final Logger LOG =
        Logger.getLogger(SimpleJobTask
                .class.getName());

    private TalendJob job;
    
    private String name;
    
    private Configuration configuration;

    private  FutureTask<?> future;
    
    private CountDownLatch configAvailable = new CountDownLatch(1);
    
    public SimpleJobTask(TalendJob job, String name) {
        this.job = job;
        this.name = name;
        future = new FutureTask<Object>(new JobRunner(), null);
    }

    @Override
    public void updated(@SuppressWarnings("rawtypes") Dictionary properties) throws ConfigurationException {
        configuration = new Configuration(properties);
        configAvailable.countDown();
    }

    @Override
    public void stop() {
        future.cancel(true);
    }

    @Override
    public void run() {
        future.run();
    }

    public class JobRunner implements Runnable {
        @Override
        public void run()  {
            LOG.info("Starting job " + name);
            String[] args = null;
            try {
                if (configAvailable.await(2000, TimeUnit.MILLISECONDS)) {
                    args = configuration.getArguments();
                } else {
                    args = new String[0];
                }
                ClassLoader oldContextCL = Thread.currentThread().getContextClassLoader();
                try {
                    Thread.currentThread().setContextClassLoader(job.getClass().getClassLoader());
                    int ret = job.runJobInTOS(args);
                    LOG.info("Job " + name + " finished, return code is " + ret);
                } finally {
                    Thread.currentThread().setContextClassLoader(oldContextCL);            
                }

            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
