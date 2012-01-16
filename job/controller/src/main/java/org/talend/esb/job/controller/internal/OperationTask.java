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

import java.util.concurrent.ExecutorService;

import org.talend.esb.job.controller.GenericOperation;
import org.talend.esb.job.controller.internal.RuntimeESBProviderCallback.MessageExchange;
import org.talend.esb.job.controller.internal.RuntimeESBProviderCallback.MessageExchangeBuffer;

import routines.system.api.TalendESBJob;

public class OperationTask implements Runnable, /*JobTask,*/ GenericOperation {

    private TalendESBJob job;


    private ExecutorService executorService;

    private String[] arguments;
    
    private MessageExchangeBuffer messageExchanges;
    
    private RuntimeESBProviderCallback callback;

    public OperationTask(TalendESBJob job, String[] arguments, ExecutorService executorService) {
        this.job = job;
        this.executorService = executorService;
        this.arguments = arguments;
        this.messageExchanges = new MessageExchangeBuffer();
        this.callback = new RuntimeESBProviderCallback(messageExchanges);
    }

    public void start() {
        executorService.execute(this);    
    }
    
    public void stop() {
        messageExchanges.stop();
    }

    public boolean isStopped() {
        return messageExchanges.isStopped();    
    }

    public void run() {
        ClassLoader oldContextCL = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(job.getClass().getClassLoader());
            job.setProviderCallback(callback);

            while (true) {
                if (Thread.interrupted()) {
                    stop();
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

    public Object invoke(Object payload, boolean isRequestResponse) throws Exception {
        MessageExchange myExchange = new MessageExchange(payload);
        messageExchanges.putMessageExchange(myExchange);
        if (!isRequestResponse) {
            return null;
        }
        synchronized (myExchange) {
            myExchange.wait();
        }
        return myExchange.response;
    }
   
}
