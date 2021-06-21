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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.saaj.SAAJStreamWriter;
import org.apache.cxf.headers.Header;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.message.Message;
import org.w3c.dom.Node;

/**
 * Read and write the CorrelationId using the SOAP headers.
 */
public final class CorrelationIdSoapCodec {

    private static final Logger LOG = Logger.getLogger(CorrelationIdSoapCodec.class.getName());

    public static final QName CORRELATION_ID_QNAME = new QName(
            "http://www.talend.com/esb/sam/correlationId/v1", "correlationId");

    /**
     * Instantiates a new correlation id soap codec.
     */
    private CorrelationIdSoapCodec() {
    }

    /**
     * Read correlation id.
     *
     * @param message the message
     * @return correlation id from the message
     */
    public static String readCorrelationId(Message message) {
        if (!(message instanceof SoapMessage)) {
            return null;
        }
        String correlationId = null;
        Header hdCorrelationId = ((SoapMessage) message).getHeader(CORRELATION_ID_QNAME);
        if (hdCorrelationId != null) {
            if (hdCorrelationId.getObject() instanceof String) {
                correlationId = (String) hdCorrelationId.getObject();
            } else if (hdCorrelationId.getObject() instanceof Node) {
                Node headerNode = (Node) hdCorrelationId.getObject();
                correlationId = headerNode.getTextContent();
            } else {
                LOG.warning("Found CorrelationId soap header but value is not a String or a Node! Value: "
                        + hdCorrelationId.getObject().toString());
            }
        }
        return correlationId;
    }

    /**
     * Write correlation id to message.
     *
     * @param message the message
     * @param correlationId the correlation id
     */
    public static void writeCorrelationId(Message message, String correlationId) {
        if (!(message instanceof SoapMessage)) {
            return;
        }
        SoapMessage soapMessage = (SoapMessage) message;
        Header hdCorrelationId = soapMessage.getHeader(CORRELATION_ID_QNAME);
        if (hdCorrelationId != null) {
            LOG.warning("CorrelationId already existing in soap header, need not to write CorrelationId header.");
            return;
        }
        if ((soapMessage.getContent(javax.xml.stream.XMLStreamWriter.class) != null)
                && (soapMessage.getContent(javax.xml.stream.XMLStreamWriter.class) instanceof SAAJStreamWriter)
                && (((SAAJStreamWriter) soapMessage.getContent(javax.xml.stream.XMLStreamWriter.class))
                        .getDocument()
                        .getElementsByTagNameNS("http://www.talend.com/esb/sam/correlationId/v1",
                                "correlationId").getLength() > 0)) {
            LOG.warning("CorrelationId already existing in soap header, need not to write CorrelationId header.");
            return;
        }

        try {
            soapMessage.getHeaders().add(
                    new Header(CORRELATION_ID_QNAME, correlationId, new JAXBDataBinding(String.class)));
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Stored correlationId '" + correlationId + "' in soap header: "
                        + CORRELATION_ID_QNAME);
            }
        } catch (JAXBException e) {
            LOG.log(Level.SEVERE, "Couldn't create correlationId header.", e);
        }

    }

}
