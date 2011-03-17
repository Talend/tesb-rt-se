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

import static org.talend.esb.sam.agent.flowid.FlowIdHelper.FLOW_ID_QNAME;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.headers.Header;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageUtils;
import org.apache.cxf.phase.Phase;
import org.talend.esb.sam.agent.flowid.FlowIdHelper;
import org.w3c.dom.Node;

public class FlowIdSoapCodec extends AbstractSoapInterceptor implements MessageContextCodec {

    protected static Logger logger = Logger.getLogger(FlowIdSoapCodec.class.getName());

    public FlowIdSoapCodec() {
        super(Phase.PRE_PROTOCOL);
        addBefore("org.apache.cxf.jaxws.handler.soap.SOAPHandlerInterceptor");
    }

    public void handleMessage(SoapMessage soapMessage) throws Fault {
        logger.finest("FlowIdSoapCodec Interceptor called. isOutbound: "
                      + MessageUtils.isOutbound(soapMessage) + ", isRequestor: "
                      + MessageUtils.isRequestor(soapMessage));

        FlowIdProcessor processor = new FlowIdProcessor(this);
        processor.processMessage(soapMessage);
    }

    public String readFlowId(Message message) {
        String flowId = null;
        SoapMessage soapMessage = (SoapMessage)message;
        Header hdFlowId = soapMessage.getHeader(FlowIdHelper.FLOW_ID_QNAME);
        if (hdFlowId != null) {
            if (hdFlowId.getObject() instanceof String) {
                flowId = (String)hdFlowId.getObject();
            } else if (hdFlowId.getObject() instanceof Node) {
                Node headerNode = (Node)hdFlowId.getObject();
                flowId = headerNode.getTextContent();
            } else {
                logger.warning("Found FlowId soap header but value is not a String or a Node! Value: "
                               + hdFlowId.getObject().toString());
            }
        }
        return flowId;
    }

    public void writeFlowId(Message message, String flowId) {
        SoapMessage soapMessage = (SoapMessage)message;
        List<Header> headers = soapMessage.getHeaders();
        Header flowIdHeader;
        try {
            flowIdHeader = new Header(FLOW_ID_QNAME, flowId, new JAXBDataBinding(String.class));
            headers.add(flowIdHeader);
            logger.fine("Stored flowId '" + flowId + "' in soap header: " + FLOW_ID_QNAME.toString());
        } catch (JAXBException e) {
            logger.log(Level.SEVERE, "Couldn't create flowId header.", e);
        }

    }

}
