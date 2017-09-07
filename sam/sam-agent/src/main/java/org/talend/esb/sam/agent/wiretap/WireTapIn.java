/*
 * #%L
 * Service Activity Monitoring :: Agent
 * %%
 * Copyright (C) 2011 - 2012 Talend Inc.
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
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

/**
 * Creates a CachedOutPutStream in the message that can be used to
 * wiretap the content
 *
 * The interceptor does not yet work streaming so it first copies all
 * the content to the CachedOutputStream and only then lets CXF
 * continue on the message.
 */
public class WireTapIn extends AbstractPhaseInterceptor<Message> {
    private boolean logMessageContent;

    /**
     * Instantiates a new WireTapIn
     *
     * @param logMessageContent the log message content
     */
    public WireTapIn(boolean logMessageContent) {
        super(Phase.RECEIVE);
        this.logMessageContent = logMessageContent;
    }

    /* (non-Javadoc)
     * @see org.apache.cxf.interceptor.Interceptor#handleMessage(org.apache.cxf.message.Message)
     */
    @Override
    public void handleMessage(final Message message) throws Fault {
        final InputStream is = message.getContent(InputStream.class);
        if (logMessageContent) {
            if (null == is) {
                Reader reader = message.getContent(Reader.class);
                if (null != reader) {
                    String encoding = (String) message.get(Message.ENCODING);
                    if (encoding == null) {
                        encoding = "UTF-8";
                    }
                    try {
                        final CachedOutputStream cos = new CachedOutputStream();
                        final Writer writer = new OutputStreamWriter(cos, encoding);
                        IOUtils.copy(reader, writer, 1024);
                        reader.reset();
                        writer.flush();
                        message.setContent(InputStream.class, cos.getInputStream());
                        message.setContent(Reader.class, null);
                        message.setContent(CachedOutputStream.class, cos);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } finally {
                        try {
                            reader.reset();
                        } catch (IOException e) {
                        }
                    }
                }
            } else {
                try {
                    final CachedOutputStream cos = new CachedOutputStream();
                    // TODO: We should try to make this streaming
                    //WireTapInputStream wtis = new WireTapInputStream(is, cos);
                    //message.setContent(InputStream.class, wtis);
                    IOUtils.copyAndCloseInput(is, cos);
                    message.setContent(InputStream.class, cos.getInputStream());
                    message.setContent(CachedOutputStream.class, cos);
                    message.getInterceptorChain().add(new AbstractPhaseInterceptor<Message>(Phase.POST_INVOKE) {
						@Override
						public void handleMessage(Message message) throws Fault {
							if (cos != null) {
								try {
									cos.close();
								} catch (IOException e) {
								}
							}
  					    }
					});
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
