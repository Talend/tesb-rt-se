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
package org.talend.esb.policy.correlation.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.message.Message;

/**
 * Read and Write the correlationId using the PROTOCOL_HEADERS.
 */
public final class CorrelationIdProtocolHeaderCodec {

    private static final Logger LOG = Logger.getLogger(CorrelationIdProtocolHeaderCodec.class.getName());

    private static final String CORRELATIONID_HTTP_HEADER_NAME = "CorrelationID";

    /**
     * Instantiates a new correlation id protocol header codec.
     */
    private CorrelationIdProtocolHeaderCodec() {
    }

    /**
     * Read correlation id from message.
     *
     * @param message the message
     * @return the CorrelationId as string
     */
    public static String readCorrelationId(Message message) {
        String correlationId = null;
        Map<String, List<String>> headers = getOrCreateProtocolHeader(message);
        List<String> correlationIds = headers.get(CORRELATIONID_HTTP_HEADER_NAME);
        if (correlationIds != null && correlationIds.size() > 0) {
            correlationId = correlationIds.get(0);
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("HTTP header '" + CORRELATIONID_HTTP_HEADER_NAME + "' found: " + correlationId);
            }
        } else {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("No HTTP header '" + CORRELATIONID_HTTP_HEADER_NAME + "' found");
            }
        }

        return correlationId;
    }

    /**
     * Write correlation id.
     *
     * @param message the message
     * @param correlationId the correlation id
     */
    public static void writeCorrelationId(Message message, String correlationId) {
        Map<String, List<String>> headers = getOrCreateProtocolHeader(message);
        headers.put(CORRELATIONID_HTTP_HEADER_NAME, Collections.singletonList(correlationId));
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("HTTP header '" + CORRELATIONID_HTTP_HEADER_NAME + "' set to: " + correlationId);
        }
    }

    /**
     * Gets the or create protocol header.
     *
     * @param message the message
     * @return the message headers map
     */
    private static Map<String, List<String>> getOrCreateProtocolHeader(
            Message message) {
        Map<String, List<String>> headers = CastUtils.cast((Map<?, ?>) message
                .get(Message.PROTOCOL_HEADERS));
        if (headers == null) {
            headers = new HashMap<String, List<String>>();
            message.put(Message.PROTOCOL_HEADERS, headers);
        }
        return headers;
    }

}
