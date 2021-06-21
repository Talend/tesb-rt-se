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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.talend.esb.job.controller.internal.MessageExchangeBuffer.BufferStoppedException;

import routines.system.api.ESBJobInterruptedException;
import routines.system.api.ESBProviderCallback;
import routines.system.api.TalendESBJob;

/**
 * Callback implementation responsible for one Job. It provides a bridge between the
 * {@link MessageExchangeBuffer buffer} holding the requests and the job retrieving a request. I also
 * responsible to assign the response to the request as soon as available.
 * <code>RuntimeESBProviderCallback</code> is an active object and meant to be run in its own {@link Thread}.
 *
 */
public class RuntimeESBProviderCallback implements ESBProviderCallback, Runnable {

    private static final Logger LOG =
        Logger.getLogger(RuntimeESBProviderCallback.class.getName());


    private MessageExchangeBuffer messageExchanges;

    private TalendESBJob job;

    private final String name;

    private MessageExchange currentExchange;

    private String[] arguments;

    /**
     * Creates a new callback.
     *
     * @param messageExchanges
     *            the buffer from which to retrieve the requests for the job.
     * @param esbJob
     *            the job for which this callback is responsible , must not be <code>null</code>
     * @param arguments
     *            arguments to be passed to the job when starting it, must not be <code>null</code>
     */
    public RuntimeESBProviderCallback(
            MessageExchangeBuffer messageExchanges,
            TalendESBJob esbJob,
            String jobName,
            String[] arguments) {
        this.messageExchanges = messageExchanges;
        job = esbJob;
        name = jobName;
        this.arguments = arguments;
    }

    /**
     * Retrieves a request from the buffer and forwards it to the {@link TalendESBJob job} requesting it.
     *
     * @return the request retrieved from the buffer
     */
    @Override
    public Object getRequest() throws ESBJobInterruptedException {
        try {
            currentExchange = messageExchanges.take();
        } catch (BufferStoppedException e) {
            Thread.currentThread().interrupt();
            throw new ESBJobInterruptedException("Job canceled.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ESBJobInterruptedException("Job canceled.");
        }
        return currentExchange.getRequest();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendResponse(Object response) {
        currentExchange.setResponse(response);
    }

    /**
     * Starts the @link TalendESBJob job} for which this callback is responsible. The job is restarted if it
     * returns back, except the {@link Thread} was interrupted or the buffer was closed.
     */
    @Override
    public void run() {
        if (LOG.isLoggable(Level.INFO)) {
            LOG.info("Calling run to start ESB job instance " + job + " for job with name " + name);
        }
        ClassLoader oldContextCL = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(job.getClass().getClassLoader());

            while (true) {
                if (Thread.interrupted()) {
                    return;
                }
                LOG.info("Starting ESB job instance " + job + " for job with name " + name);
                int ret = job.runJobInTOS(arguments);
                LOG.info("ESB job instance " + job + " with name " + name + " finished, return code is "
                        + ret);
                if (ret != 0) {
                    break;
                }
            }
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            LOG.log(Level.SEVERE, "RuntimeException when invoking runJobInTOS()", ex);
        } finally {
            Thread.currentThread().setContextClassLoader(oldContextCL);
        }
    }



    @Override
    public String toString() {
        return "RuntimeESBProviderCallback[" + System.identityHashCode(this) + "] for job " + job;
    }

    public static class MessageExchange {
        private Object request;

        private Object response;

        private boolean ready;

        public MessageExchange(Object request) {
            this.request = request;
        }

        public Object getRequest() {
            return request;
        }

        public void setResponse(Object response) {
            synchronized (this) {
                this.response = response;
                ready = true;
                notifyAll();
            }
        }

        public Object waitForResponse() throws InterruptedException {
            synchronized (this) {
                while (!ready) {
                    wait();
                }
            }
            return response;
        }
    }
}
