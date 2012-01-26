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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import routines.system.api.ESBJobInterruptedException;
import routines.system.api.ESBProviderCallback;

public class RuntimeESBProviderCallback implements ESBProviderCallback {

    private static final Logger LOG =
        Logger.getLogger(RuntimeESBProviderCallback.class.getName());

    private static final MessageExchange POISON = new MessageExchange(null);

    private final BlockingQueue<MessageExchange> requests = new LinkedBlockingQueue<MessageExchange>();

    private MessageExchange currentExchange;

    private volatile boolean stopped;

    public Object getRequest() throws ESBJobInterruptedException {
        try {
            currentExchange = requests.take();
            if (POISON == currentExchange) {
                stopped = true;
                throw new ESBJobInterruptedException("Job was cancelled.");
            }
        } catch (InterruptedException e) {
            prepareStop();
        }
        return currentExchange.request;
    }

    public void sendResponse(Object response) {
        currentExchange.response = response;
        synchronized (currentExchange) {
            currentExchange.ready = true;
            currentExchange.notify();
        }
    }

    public Object invoke(Object payload, boolean isRequestResponse) throws Exception {
        MessageExchange myExchange = new MessageExchange(payload);
        requests.put(myExchange);
        if (!isRequestResponse) {
            return null;
        }
        synchronized (myExchange) {
            while (!myExchange.ready) {
                myExchange.wait();
            }
        }
        return myExchange.response;
    }

    public void stop() {
        prepareStop();
    }

    public boolean isStopped() {
        return stopped;
    }

    protected void prepareStop() {
        boolean success = false;
        while (!success) {
            try {
                requests.put(POISON);
                success = true;
            } catch (InterruptedException e) {
                LOG.throwing(this.getClass().getName(), "prepareStop", e);
            }
        }
    }

    private static final class MessageExchange {
        public Object request;

        public Object response;

        // http://docs.oracle.com/javase/tutorial/essential/concurrency/guardmeth.html
        public boolean ready = false;

        public MessageExchange(Object request) {
            this.request = request;
        }
    }

}
