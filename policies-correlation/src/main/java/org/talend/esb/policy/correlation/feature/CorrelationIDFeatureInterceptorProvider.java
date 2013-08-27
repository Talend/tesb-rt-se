package org.talend.esb.policy.correlation.feature;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.cxf.binding.soap.SoapBinding;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.AbstractAttributedInterceptorProvider;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageUtils;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.ws.addressing.ContextUtils;
import org.talend.esb.policy.correlation.CorrelationIDCallbackHandler;
import org.talend.esb.policy.correlation.impl.CorrelationIdProtocolHeaderCodec;
import org.talend.esb.policy.correlation.impl.CorrelationIdSoapCodec;
import org.xml.sax.SAXException;

public class CorrelationIDFeatureInterceptorProvider extends AbstractAttributedInterceptorProvider {

    private static final String CORRELATION_ID_CALLBACK_HANDLER = "correlation-id.callback-handler";

    private static final long serialVersionUID = 5698743589425687361L;

    public CorrelationIDFeatureInterceptorProvider() {

        this.getOutInterceptors().add(new CorrelationIDFeaturePolicyOutInterceptor());
        this.getOutFaultInterceptors().add(new CorrelationIDFeaturePolicyOutInterceptor());
        this.getInInterceptors().add(new CorrelationIDFeaturePolicyInInterceptor());
        this.getInFaultInterceptors().add(new CorrelationIDFeaturePolicyInInterceptor());
    }

    static class CorrelationIDFeaturePolicyOutInterceptor extends AbstractPhaseInterceptor<Message> {

        public CorrelationIDFeaturePolicyOutInterceptor() {
            super(Phase.PRE_STREAM);
        }

        @Override
        public void handleMessage(Message message) throws Fault {
            try {
                process(message);
            } catch (SAXException e) {
                throw new Fault(e);
            } catch (IOException e) {
                throw new Fault(e);
            } catch (ParserConfigurationException e) {
                throw new Fault(e);
            }
        }

    }

    static class CorrelationIDFeaturePolicyInInterceptor extends AbstractPhaseInterceptor<Message> {

        public CorrelationIDFeaturePolicyInInterceptor() {
            super(Phase.PRE_PROTOCOL);
        }

        @Override
        public void handleMessage(Message message) throws Fault {
            try {
                process(message);
            } catch (SAXException e) {
                throw new Fault(e);
            } catch (IOException e) {
                throw new Fault(e);
            } catch (ParserConfigurationException e) {
                throw new Fault(e);
            }
        }

    }

    static void process(Message message) throws SAXException, IOException, ParserConfigurationException {

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
