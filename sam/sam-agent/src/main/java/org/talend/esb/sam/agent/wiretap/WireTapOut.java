/*
 * #%L
 * Service Activity Monitoring :: Agent
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
package org.talend.esb.sam.agent.wiretap;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.io.CachedOutputStreamCallback;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

/**
 * Create a CachedOutputStream on the message that can be used to Wiretap the
 * content. Additionally it registers an optional interceptor that is called as
 * soon as the output stream is closed
 */
public class WireTapOut extends AbstractPhaseInterceptor<Message> {

    private Interceptor<Message> wireTap;
    private boolean logMessageContent;

    private boolean logMessageContentOverride;
    /**
     * Instantiates a new wire tap out.
     *
     * @param wireTap the Interceptor
     * @param logMessageContent the log message content
     */
    public WireTapOut(Interceptor<Message> wireTap, boolean logMessageContent, boolean logMessageContentOverride) {
        super(Phase.PRE_STREAM);
        this.wireTap = wireTap;
        this.logMessageContent = logMessageContent;
        this.logMessageContentOverride = logMessageContentOverride;
    }

    /* (non-Javadoc)
     * @see org.apache.cxf.interceptor.Interceptor#handleMessage(org.apache.cxf.message.Message)
     */
    @Override
    public void handleMessage(final Message message) throws Fault {
        OutputStream os = message.getContent(OutputStream.class);

        if (null == os) {
            String encoding = (String) message.get(Message.ENCODING);
            if (encoding == null) {
                encoding = "UTF-8";
            }

            final Writer writer = message.getContent(Writer.class);
            if (null != writer) {
                os = new WriterOutputStream(writer, encoding);
                message.setContent(Writer.class,null);
            }
        }

        if (null != os) {
            final CacheAndWriteTapOutputStream newOut = new CacheAndWriteTapOutputStream(os);

            message.setContent(OutputStream.class, newOut);

            if (WireTapHelper
                    .isMessageContentToBeLogged(message, logMessageContent, logMessageContentOverride)) {
                message.setContent(CachedOutputStream.class, newOut);
            }else {
                try {
                    final CachedOutputStream cos = new CachedOutputStream();
                    cos.write(WireTapHelper.CONTENT_LOGGING_IS_DISABLED.getBytes(Charset.forName("UTF-8")));
                    message.setContent(CachedOutputStream.class, cos);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if (wireTap != null) {
                newOut.registerCallback(new CallBack(message));
            }
        }
    }

    /**
     * The CallBack class.
     */
    private final class CallBack implements CachedOutputStreamCallback {
        private final Message message;

        /**
         * Instantiates a new call back.
         *
         * @param message the message
         */
        private CallBack(Message message) {
            this.message = message;
        }

        /* (non-Javadoc)
         * @see org.apache.cxf.io.CachedOutputStreamCallback#onFlush(org.apache.cxf.io.CachedOutputStream)
         */
        @Override
        public void onFlush(CachedOutputStream os) {
        }

        /* (non-Javadoc)
         * @see org.apache.cxf.io.CachedOutputStreamCallback#onClose(org.apache.cxf.io.CachedOutputStream)
         */
        @Override
        public void onClose(CachedOutputStream os) {
            wireTap.handleMessage(message);
        }
    }
}
