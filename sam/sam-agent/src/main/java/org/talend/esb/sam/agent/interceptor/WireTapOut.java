/*
 * #%L
 * Service Activity Monitoring :: Agent
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
package org.talend.esb.sam.agent.interceptor;

import java.io.OutputStream;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.io.CacheAndWriteOutputStream;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.io.CachedOutputStreamCallback;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

/**
 * Create a wiretap InputStream in the message with key ContentKey.CONTENT_KEY
 */
public class WireTapOut extends AbstractPhaseInterceptor<Message> {
    private Interceptor<Message> wireTap;
    private boolean logMessage;

    public WireTapOut(Interceptor<Message> wireTap, boolean logMessage) {
        super(Phase.PRE_STREAM);
        this.wireTap = wireTap;
        this.logMessage = logMessage;
    }

    @Override
    public void handleMessage(final Message message) throws Fault {
        final OutputStream os = message.getContent(OutputStream.class);
        if (os != null && logMessage) {
            final CacheAndWriteOutputStream newOut = new CacheAndWriteOutputStream(os);
            message.setContent(OutputStream.class, newOut);
            message.setContent(CachedOutputStream.class, newOut);
            newOut.registerCallback(new CachedOutputStreamCallback() {
                
                @Override
                public void onFlush(CachedOutputStream os) {
                }
                
                @Override
                public void onClose(CachedOutputStream os) {
                    wireTap.handleMessage(message);
                }
            });
        }
    }

}
