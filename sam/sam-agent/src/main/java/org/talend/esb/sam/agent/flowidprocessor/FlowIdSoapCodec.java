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
package org.talend.esb.sam.agent.flowidprocessor;

import static org.talend.esb.sam.agent.message.FlowIdHelper.FLOW_ID_QNAME;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.headers.Header;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.message.Message;
import org.talend.esb.sam.agent.message.FlowIdHelper;
import org.w3c.dom.Node;

/**
 * Read and write the FlowId using the SOAP headers
 */
public class FlowIdSoapCodec {

    private static final Logger LOG = Logger.getLogger(FlowIdSoapCodec.class.getName());

    private FlowIdSoapCodec() {
    }

    public static String readFlowId(Message message) {
        if (!(message instanceof SoapMessage)) {
            return null;
        }
        String flowId = null;
        Header hdFlowId = ((SoapMessage)message).getHeader(FlowIdHelper.FLOW_ID_QNAME);
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

    public static void writeFlowId(Message message, String flowId) {
        if (!(message instanceof SoapMessage)) {
            return;
        }
        SoapMessage soapMessage = (SoapMessage)message;
        Header hdFlowId = soapMessage.getHeader(FlowIdHelper.FLOW_ID_QNAME);
        if (hdFlowId != null) {
            LOG.warning("FlowId already existing in soap header, need not to write FlowId header.");
            return;
        }

        try {
            Header flowIdHeader = new Header(FLOW_ID_QNAME, flowId, new JAXBDataBinding(String.class));
            soapMessage.getHeaders().add(flowIdHeader);
            if(LOG.isLoggable(Level.FINE)) {
                LOG.fine("Stored flowId '" + flowId + "' in soap header: " + FLOW_ID_QNAME);
            }
        } catch (JAXBException e) {
            LOG.log(Level.SEVERE, "Couldn't create flowId header.", e);
        }

    }

}
