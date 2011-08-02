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

import routines.system.api.ESBConsumer;
import routines.system.api.ESBJobInterruptedException;
import routines.system.api.ESBProviderCallback;

public class RuntimeESBProviderCallback implements ESBProviderCallback, ESBConsumer {

    private final boolean isRequestResponse;
    private final BlockingQueue<Object> requests = new LinkedBlockingQueue<Object>();

    private Object request = null;
    private Object response = null;

    public RuntimeESBProviderCallback(boolean isRequestResponse) {
        this.isRequestResponse = isRequestResponse;
    }

    @Override
    public Object getRequest() throws ESBJobInterruptedException {
        try {
            request = requests.take();
            return request;
        } catch (InterruptedException e) {
            throw new ESBJobInterruptedException(e.getMessage(), e);
        }
    }

    @Override
    public void sendResponse(Object response) {
        this.response = response;
        synchronized (request) {
            request.notify();
        }
    }

    @Override
    public Object invoke(Object payload) throws Exception {
        requests.put(payload);
        if(!isRequestResponse) {
            return null;
        }
        synchronized (payload) {
            payload.wait();
        }
        return response;
    }
}
