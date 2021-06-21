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

import org.talend.esb.job.controller.GenericOperation;
import org.talend.esb.job.controller.internal.MessageExchangeBuffer.WorkloadListener;
import org.talend.esb.job.controller.internal.RuntimeESBProviderCallback.MessageExchange;

/**
 * Skeleton implementation for a {@link GenericOperation}  which is backed by a {@link TalendESBJob job}.
 *
 */
public abstract class AbstractOperation implements GenericOperation {

    private final MessageExchangeBuffer buffer;

    /**
     * Create a <code>SingleThreadedOperation</code>.
     *
     * @param esbJob job instance  backing this operation, must not be <code>null</code>
     * @param executorService executor service to start job in separate Thread, must not be <code>null</code>
     */
    public AbstractOperation() {
        this(new MessageExchangeBuffer());
    }

    public AbstractOperation(MessageExchangeBuffer messageExchanges) {
        buffer = messageExchanges;
    }

    /**
     * Starts the one and only job instance in a separate Thread. Should be called exactly one time before
     * the operation is stopped.
     *
     * @param arguments {@inheritDoc}
     */
    protected void start(WorkloadListener listener) {
        buffer.setWorkloadListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        buffer.stop();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object invoke(Object payload, boolean isRequestResponse) throws Exception {
        MessageExchange myExchange = new MessageExchange(payload);
        buffer.put(myExchange);
        if (!isRequestResponse) {
            return null;
        }
        return myExchange.waitForResponse();
    }
}
