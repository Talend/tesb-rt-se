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
import org.talend.esb.job.controller.internal.MessageExchangeBuffer.WorkloadListener;
import org.talend.esb.job.controller.internal.RuntimeESBProviderCallback.MessageExchange;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

public class AbstractOperationTest {

    private MessageExchangeBuffer buffer = createMock(MessageExchangeBuffer.class);

    @Test
    public void startPassesListenerToMessageExchangeBuffer() {
        WorkloadListener listener = createMock(WorkloadListener.class);
        buffer.setWorkloadListener(listener);
        replay(buffer);

        AbstractOperation op = createOperationUnderTest(buffer);

        op.start(listener);

        verify(buffer);
    }

    @Test
    public void stopStopsMessageExchangeBuffer() {
        buffer.stop();
        replay(buffer);

        AbstractOperation op = createOperationUnderTest(buffer);

        op.stop();

        verify(buffer);
    }

    @Test
    public void invokeAsOneWay() throws Exception {
        buffer.put((MessageExchange)anyObject());
        replay(buffer);

        AbstractOperation op = createOperationUnderTest(buffer);

        op.invoke(new Object(), false);

        verify(buffer);
    }

    private AbstractOperation createOperationUnderTest(MessageExchangeBuffer buf) {
        return new AbstractOperation(buf) {
            public void start(String[] arguments) { }

        };
    }
}
