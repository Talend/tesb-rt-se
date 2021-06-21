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

import java.util.Dictionary;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

import routines.system.api.TalendJob;

public class SimpleJobTask implements ManagedService, JobTask  {

    private static final Logger LOG = Logger.getLogger(SimpleJobTask.class.getName());

    private TalendJob job;

    private String name;

    private Configuration configuration;

    private  FutureTask<?> future;

    public SimpleJobTask(TalendJob job, String name) {
        this.job = job;
        this.name = name;
        configuration = new Configuration();
        future = new FutureTask<Object>(new JobRunner(), null);
    }

    @Override
    public void updated(@SuppressWarnings("rawtypes") Dictionary properties) throws ConfigurationException {
        configuration.setProperties(properties);
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
                args = configuration.awaitArguments();
            } catch (InterruptedException e) {
                return;
            }

            ClassLoader oldContextCL = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(job.getClass().getClassLoader());
                int ret = job.runJobInTOS(args);
                LOG.info("Job " + name + " finished, return code is " + ret);
            } catch (RuntimeException ex) {
                ex.printStackTrace();
                LOG.log(Level.SEVERE, "RuntimeException when invoking runJobInTOS()", ex);
            } finally {
                Thread.currentThread().setContextClassLoader(oldContextCL);
            }
        }
    }
}
