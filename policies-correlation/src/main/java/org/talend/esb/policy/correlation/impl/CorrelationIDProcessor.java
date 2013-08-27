package org.talend.esb.policy.correlation.impl;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.cxf.binding.soap.SoapBinding;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageUtils;
import org.apache.cxf.ws.addressing.ContextUtils;
import org.talend.esb.policy.correlation.CorrelationIDCallbackHandler;
import org.talend.esb.policy.correlation.feature.CorrelationIDFeature;
import org.xml.sax.SAXException;

public class CorrelationIDProcessor {
    private static final Logger LOG = Logger.getLogger(CorrelationIDProcessor.class.getName());

    private static final String CORRELATION_ID_CALLBACK_HANDLER = "correlation-id.callback-handler";

    static void process(Message message) throws SAXException, IOException, ParserConfigurationException {

        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Message process for correlation ID started");
        }

        String correlationId = null;
        // get ID from Http header
        correlationId = CorrelationIdProtocolHeaderCodec.readCorrelationId(message);
        // get ID from SOAP header
        if (null == correlationId) {
            correlationId = CorrelationIdSoapCodec.readCorrelationId(message);
        }
        // get from message
        if (null == correlationId) {
            // Get ID from Message
            correlationId = (String) message.get(CorrelationIDFeature.MESSAGE_CORRELATION_ID);
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
            CorrelationIDCallbackHandler handler = (CorrelationIDCallbackHandler) message
                    .get(CORRELATION_ID_CALLBACK_HANDLER);
            if (handler != null)
                correlationId = handler.getCorrelationId();
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
