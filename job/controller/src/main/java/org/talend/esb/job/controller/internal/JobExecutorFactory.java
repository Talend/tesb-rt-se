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
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class JobExecutorFactory {
    public static final Logger LOG = Logger.getLogger(JobExecutorFactory.class.getName());

    private static Thread.UncaughtExceptionHandler loggingUEH = new Thread.UncaughtExceptionHandler() {

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            LOG.log(Level.SEVERE, "Uncaught exception in job executor thread: ", e);
        }
    };

    private JobExecutorFactory() {

    }

    public static ExecutorService newExecutor() {
        ThreadFactory jobThreadFactory = new ThreadFactory() {
            ThreadFactory defaultThreadFactory = Executors.defaultThreadFactory();

            @Override
            public Thread newThread(Runnable r) {
                Thread newThread = defaultThreadFactory.newThread(r);
                newThread.setUncaughtExceptionHandler(loggingUEH);
                newThread.setContextClassLoader(this.getClass().getClassLoader());
                return newThread;
            }
        };

        return Executors.newCachedThreadPool(jobThreadFactory);
    }

}
