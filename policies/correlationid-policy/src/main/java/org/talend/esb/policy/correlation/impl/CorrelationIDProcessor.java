package org.talend.esb.policy.correlation.impl;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.cxf.binding.soap.SoapBinding;
import org.apache.cxf.binding.soap.saaj.SAAJStreamWriter;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageUtils;
import org.apache.cxf.ws.addressing.ContextUtils;
import org.apache.neethi.Assertion;
import org.talend.esb.policy.correlation.CorrelationIDCallbackHandler;
import org.talend.esb.policy.correlation.feature.CorrelationIDFeature;
import org.talend.esb.policy.correlation.impl.CorrelationIDAssertion.MethodType;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class CorrelationIDProcessor {
    private static final Logger LOG = Logger.getLogger(CorrelationIDProcessor.class.getName());

    static void process(Message message, Assertion policy) throws SAXException, IOException, ParserConfigurationException {

        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Message process for correlation ID started");
        }

        String correlationId = null;
        // get ID from SOAP header
        correlationId = CorrelationIdSoapCodec.readCorrelationId(message);
        // get ID from Http header
        if (null == correlationId) {
            correlationId = CorrelationIdProtocolHeaderCodec.readCorrelationId(message);
            
        }
        // get from message
        if (null == correlationId) {
            // Get ID from Message
            correlationId = (String) message.get(CorrelationIDFeature.MESSAGE_CORRELATION_ID);
        }

        if ((message.getContent(javax.xml.stream.XMLStreamWriter.class) != null)
                && (message.getContent(javax.xml.stream.XMLStreamWriter.class) instanceof SAAJStreamWriter)) {
            NodeList nodeList = ((SAAJStreamWriter) message
                    .getContent(javax.xml.stream.XMLStreamWriter.class))
                    .getDocument()
                    .getElementsByTagNameNS("http://www.talend.com/esb/sam/correlationId/v1", "correlationId");
            if(nodeList.getLength()>0) {
                correlationId = nodeList.item(0).getTextContent();
            }
        }

        // get from message exchange
        if (null == correlationId) {
            // Get ID from Message exchange
            Exchange ex = message.getExchange();
            if (null != ex) {
                Message reqMsg = null;
                if (MessageUtils.isOutbound(message)) {
                    reqMsg = ex.getInMessage();
                } else {
                    reqMsg = ex.getOutMessage();
                }
                if (null != reqMsg) {
                    correlationId = (String) reqMsg.get(CorrelationIDFeature.MESSAGE_CORRELATION_ID);
                }
            }
        }
        // If correlationId is null we should add it to headers
        if (null == correlationId) {

        	MethodType mType = CorrelationIDAssertion.MethodType.CALLBACK;
        	if(policy instanceof CorrelationIDAssertion){
        		mType = ((CorrelationIDAssertion) policy).getMethodType();
        	}

            if (MethodType.XPATH.equals(mType) && !MessageUtils.isFault(message)) {
               XPathProcessor proc = new XPathProcessor(policy, message);
               correlationId = proc.getCorrelationID();
            } else if (MethodType.CALLBACK.equals(mType)){
                CorrelationIDCallbackHandler handler = (CorrelationIDCallbackHandler) message
                        .get(CorrelationIDFeature.CORRELATION_ID_CALLBACK_HANDLER);
                if (null == handler) {
                    handler = (CorrelationIDCallbackHandler) message
                            .getContextualProperty(CorrelationIDFeature.CORRELATION_ID_CALLBACK_HANDLER);
                }
                if (handler != null)
                    correlationId = handler.getCorrelationId();
            }
            // Generate new ID if it was not set in callback or
            // request
            if (null == correlationId) {
                correlationId = ContextUtils.generateUUID();
            }
        }

        message.put(CorrelationIDFeature.MESSAGE_CORRELATION_ID, correlationId);

        if (isRestMessage(message)) {
            // Add correlationId to http header
            if (null == CorrelationIdProtocolHeaderCodec.readCorrelationId(message)) {
                CorrelationIdProtocolHeaderCodec.writeCorrelationId(message, correlationId);
            }
        } else {
            // Add correlationId to soap header
            if (null == CorrelationIdSoapCodec.readCorrelationId(message)) {
                CorrelationIdSoapCodec.writeCorrelationId(message, correlationId);
            }
        }
    }

    private static boolean isRestMessage(Message message) {
        return !(message.getExchange().getBinding() instanceof SoapBinding);
    }

}
