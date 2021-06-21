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
package org.talend.esb.sam.agent.flowidprocessor;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.headers.Header;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.message.Message;
import org.w3c.dom.Node;

/**
 * Read and write the FlowId using the SOAP headers.
 */
public final class FlowIdSoapCodec {

    private static final Logger LOG = Logger.getLogger(FlowIdSoapCodec.class.getName());

    public static final QName FLOW_ID_QNAME = new QName(
            "http://www.talend.com/esb/sam/flowId/v1", "flowId");
    /**
     * Instantiates a new flow id soap codec.
     */
    private FlowIdSoapCodec() {
    }

    /**
     * Read flow id.
     *
     * @param message the message
     * @return flow id from the message
     */
    public static String readFlowId(Message message) {
        if (!(message instanceof SoapMessage)) {
            return null;
        }
        String flowId = null;
        Header hdFlowId = ((SoapMessage)message).getHeader(FLOW_ID_QNAME);
        if (hdFlowId != null) {
            if (hdFlowId.getObject() instanceof String) {
                flowId = (String)hdFlowId.getObject();
            } else if (hdFlowId.getObject() instanceof Node) {
                Node headerNode = (Node)hdFlowId.getObject();
                flowId = headerNode.getTextContent();
            } else {
                LOG.warning("Found FlowId soap header but value is not a String or a Node! Value: "
                               + hdFlowId.getObject().toString());
            }
        }
        return flowId;
    }

    /**
     * Write flow id to message.
     *
     * @param message the message
     * @param flowId the flow id
     */
    public static void writeFlowId(Message message, String flowId) {
        if (!(message instanceof SoapMessage)) {
            return;
        }
        SoapMessage soapMessage = (SoapMessage)message;
        Header hdFlowId = soapMessage.getHeader(FLOW_ID_QNAME);
        if (hdFlowId != null) {
            LOG.warning("FlowId already existing in soap header, need not to write FlowId header.");
            return;
        }

        try {
            soapMessage.getHeaders().add(
                    new Header(FLOW_ID_QNAME, flowId, new JAXBDataBinding(String.class)));
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Stored flowId '" + flowId + "' in soap header: " + FLOW_ID_QNAME);
            }
        } catch (JAXBException e) {
            LOG.log(Level.SEVERE, "Couldn't create flowId header.", e);
        }

    }

}
