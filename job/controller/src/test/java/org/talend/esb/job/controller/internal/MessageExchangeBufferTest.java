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

import org.junit.Test;
import org.talend.esb.job.controller.internal.MessageExchangeBuffer.BufferStoppedException;
import org.talend.esb.job.controller.internal.MessageExchangeBuffer.WorkloadListener;
import org.talend.esb.job.controller.internal.RuntimeESBProviderCallback.MessageExchange;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MessageExchangeBufferTest {


    public static final MessageExchange ME_1 = new MessageExchange(null);
    public static final MessageExchange ME_2 = new MessageExchange(null);

    final MessageExchangeBuffer buffer = new MessageExchangeBuffer();

    @org.junit.Before
    public void startup() {
    }

    @Test
    public void itemsPutInFIFOrder() throws Exception {

        buffer.put(ME_1);
        buffer.put(ME_2);

        assertSame(ME_1, buffer.take());
        assertSame(ME_2, buffer.take());
    }

    @Test
    public void afterStopFurtherItemsRejected() throws Exception {
        buffer.stop();

        try {
            buffer.put(ME_1);
            fail("A BufferStoppedException should have been thrown.");
        } catch (BufferStoppedException e) {
        }
    }

    @Test
    public void notIsStoppedIfStillItemsInBuffer() throws Exception {
        buffer.put(ME_1);
        buffer.stop();

        assertFalse(buffer.isStopped());
    }

    @Test
    public void isStoppedIfBufferEmpty() throws Exception {
        buffer.put(ME_1);
        buffer.stop();
        buffer.take();

        assertTrue(buffer.isStopped());
    }

    @Test
    public void workloadListenerNotifiedWhenRegistered() {
        WorkloadListener listener = createMock(WorkloadListener.class);
        listener.initialValues(buffer, 0, 0);
        replay(listener);

        buffer.setWorkloadListener(listener);
        verify(listener);
    }

    @Test
    public void workloadListenerNotifiedWhenRegisteredOneRequestAlreadyPut() throws Exception {
        WorkloadListener listener = createMock(WorkloadListener.class);
        listener.initialValues(buffer, 0, 1);
        replay(listener);

        buffer.put(ME_1);
        buffer.setWorkloadListener(listener);
        verify(listener);
    }

    @Test
    public void workloadListenerNotifiedWhenRequestPut() throws Exception {
        WorkloadListener listener = createMock(WorkloadListener.class);
        listener.initialValues(buffer, 0, 0);
        listener.valuesChanged(buffer, 0, 1);
        replay(listener);

        buffer.setWorkloadListener(listener);
        buffer.put(ME_1);

        verify(listener);
    }

    @Test
    public void workloadListenerNotifiedBeforeLeavingTake()  throws Exception {
        WorkloadListener listener = createNiceMock(WorkloadListener.class);
        listener.initialValues(buffer, 0, 0);
        listener.valuesChanged(buffer, 0, 1);
        listener.valuesChanged(buffer, 0, 0);
        replay(listener);

        buffer.setWorkloadListener(listener);
        buffer.put(ME_1);
        buffer.take();

        verify(listener);
    }
}
