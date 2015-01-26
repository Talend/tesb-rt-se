package org.talend.esb.policy.correlation.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.cxf.binding.soap.SoapBinding;
import org.apache.cxf.binding.soap.saaj.SAAJInInterceptor;
import org.apache.cxf.binding.soap.saaj.SAAJStreamWriter;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageUtils;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.ws.addressing.ContextUtils;
import org.apache.cxf.ws.policy.AbstractPolicyInterceptorProvider;
import org.apache.cxf.ws.policy.AssertionInfo;
import org.apache.cxf.ws.policy.AssertionInfoMap;
import org.talend.esb.policy.correlation.CorrelationIDCallbackHandler;
import org.talend.esb.policy.correlation.feature.CorrelationIDFeature;
import org.talend.esb.policy.correlation.impl.CorrelationIDAssertion.MethodType;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class CorrelationIDInterceptorProvider extends AbstractPolicyInterceptorProvider {

    private static final long serialVersionUID = 5698743589425687361L;

    public CorrelationIDInterceptorProvider() {
        super(Arrays.asList(CorrelationIDPolicyBuilder.CORRELATION_ID));

        this.getOutInterceptors().add(new CorrelationIDPolicyOutInterceptor());
        this.getOutFaultInterceptors().add(new CorrelationIDPolicyOutInterceptor());
        this.getInInterceptors().add(new CorrelationIDPolicyInInterceptor());
        this.getInFaultInterceptors().add(new CorrelationIDPolicyInInterceptor());

        // Selector registers SAAJ interceptor for Soap messages only
        CorrelationIDFeatureSelectorInterceptor selector = new CorrelationIDFeatureSelectorInterceptor();
        this.getInInterceptors().add(selector);
        this.getInFaultInterceptors().add(selector);

    }

    static class CorrelationIDPolicyOutInterceptor extends AbstractPhaseInterceptor<Message> {

        public CorrelationIDPolicyOutInterceptor() {
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

    static class CorrelationIDPolicyInInterceptor extends AbstractPhaseInterceptor<Message> {

        public CorrelationIDPolicyInInterceptor() {
            super(Phase.PRE_PROTOCOL);
            addAfter(SAAJInInterceptor.class.getName());
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
        AssertionInfoMap aim = message.get(AssertionInfoMap.class);
        if (aim != null) {
            Collection<AssertionInfo> ais = aim.get(CorrelationIDPolicyBuilder.CORRELATION_ID);

            if (ais == null) {
                return;
            }

            for (AssertionInfo ai : ais) {
                if (ai.getAssertion() instanceof CorrelationIDAssertion) {
                    CorrelationIDAssertion cAssertion = (CorrelationIDAssertion) ai.getAssertion();
                    MethodType mType = cAssertion.getMethodType();
                    // String value = cAssetrion.getValue();
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
                        if (MethodType.XPATH.equals(mType)) {

                        	XPathProcessor proc = new XPathProcessor(cAssertion, message);
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
                    // if (!MessageUtils.isRequestor(message) &&
                    // MessageUtils.isOutbound(message)) {// RESP_OUT
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
                    // }
                    ai.setAsserted(true);
                }
            }
        }
    }

    private static boolean isRestMessage(Message message) {
        return !(message.getExchange().getBinding() instanceof SoapBinding);
    }
}
