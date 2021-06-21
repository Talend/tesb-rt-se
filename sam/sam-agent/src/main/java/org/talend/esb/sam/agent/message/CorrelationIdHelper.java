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
package org.talend.esb.sam.agent.message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.headers.Header;
import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.message.Message;
import javax.xml.namespace.QName;
import org.w3c.dom.Node;


/**
 * The Class CorrelationIdHelper used as helper for correlation id setting.
 */
public final class CorrelationIdHelper {

    private static final Logger LOG = Logger.getLogger(CorrelationIdHelper.class.getName());

    public static final String CORRELATION_ID_KEY = "CorrelationID";

    public static final QName CORRELATION_ID_QNAME = new QName(
            "http://www.talend.com/esb/sam/correlationId/v1", "correlationId");

    /**
     * Instantiates a new correlation id helper.
     */
    private CorrelationIdHelper() {
    }

    /**
     * Get CorrelationId from message.
     *
     * @param message the message
     * @return correlationId or null if not set
     */
    public static String getCorrelationId(Message message) {
        String correlationId = (String) message.get(CORRELATION_ID_KEY);
        if(null == correlationId) {
            correlationId = readCorrelationId(message);
        }
        if(null == correlationId) {
            correlationId = readCorrelationIdSoap(message);
        }
        return correlationId;
    }

    /**
     * Sets the correlation id.
     *
     * @param message the message
     * @param correlationId the correlation id
     */
    public static void setCorrelationId(Message message, String correlationId) {
        message.put(CORRELATION_ID_KEY, correlationId);
    }

    public static String readCorrelationIdSoap(Message message) {
        if (!(message instanceof SoapMessage)) {
            return null;
        }
        String correlationId = null;
        Header hdCorrelationId = ((SoapMessage)message).getHeader(CORRELATION_ID_QNAME);
        if (hdCorrelationId != null) {
            if (hdCorrelationId.getObject() instanceof String) {
                correlationId = (String)hdCorrelationId.getObject();
            } else if (hdCorrelationId.getObject() instanceof Node) {
                Node headerNode = (Node)hdCorrelationId.getObject();
                correlationId = headerNode.getTextContent();
            } else {
                LOG.warning("Found CorrelationId soap header but value is not a String or a Node! Value: "
                               + hdCorrelationId.getObject().toString());
            }
        }
        return correlationId;
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
        List<String> correlationIds = headers.get(CORRELATION_ID_KEY);
        if (correlationIds != null && correlationIds.size() > 0) {
            correlationId = correlationIds.get(0);
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("HTTP header '" + CORRELATION_ID_KEY + "' found: " + correlationId);
            }
        } else {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("No HTTP header '" + CORRELATION_ID_KEY + "' found");
            }
        }

        return correlationId;
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
