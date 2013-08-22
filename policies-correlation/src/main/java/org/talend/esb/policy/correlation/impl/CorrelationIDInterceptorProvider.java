package org.talend.esb.policy.correlation.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.cxf.binding.soap.SoapBinding;
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
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.talend.esb.policy.correlation.CorrelationIDCallbackHandler;
import org.talend.esb.policy.correlation.impl.CorrelationIDAssertion.MethodType;
import org.xml.sax.SAXException;

public class CorrelationIDInterceptorProvider extends AbstractPolicyInterceptorProvider {

    private static final String CORRELATION_ID_CALLBACK_HANDLER = "correlation-id.callback-handler";
    
    private static final long serialVersionUID = 5698743589425687361L;

    public CorrelationIDInterceptorProvider() {
        super(Arrays.asList(CorrelationIDPolicyBuilder.CORRELATION_ID_SCHEMA));

        this.getOutInterceptors().add(new CorrelationIDPolicyOutInterceptor());
        this.getOutFaultInterceptors().add(new CorrelationIDPolicyOutInterceptor());
        this.getInInterceptors().add(new CorrelationIDPolicyInInterceptor());
        this.getInFaultInterceptors().add(new CorrelationIDPolicyInInterceptor());
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
            Collection<AssertionInfo> ais = aim.get(CorrelationIDPolicyBuilder.CORRELATION_ID_SCHEMA);

            if (ais == null) {
                return;
            }

            for (AssertionInfo ai : ais) {
                if (ai.getAssertion() instanceof CorrelationIDAssertion) {
                    CorrelationIDAssertion cAssetrion = (CorrelationIDAssertion) ai.getAssertion();
                    MethodType mType = cAssetrion.getMethodType();
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
                        correlationId = (String) message.get("CorrelationID");
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
                                correlationId = (String) reqMsg.get("CorrelationID");
                            }
                        }
                    }
                    // If correlationId is null we should add it to headers
                    if (null == correlationId) {
                        if (MethodType.XPATH.equals(mType)) {
// TODO: Try to implement receiving JAXB Context to apply XPath                            
//                            Document doc = getPayload(message);
//                            NodeList nodes = doc.getChildNodes();
//                            Node bodyElement = null;
//                            nodes = doc.getElementsByTagNameNS("http://schemas.xmlsoap.org/soap/envelope/",
//                                    "Body");
//                            if (nodes.getLength() > 0) {
//                                bodyElement = nodes.item(0);
//                            }
//                            if (bodyElement != null) {
//                                XPathFactory xPathfactory = XPathFactory.newInstance();
//                                XPath xpath = xPathfactory.newXPath();
//                                try {
//                                    XPathExpression expr = xpath.compile(cAssetrion.getValue());
//                                    String result = (String) expr.evaluate(bodyElement, XPathConstants.STRING);
//                                    if (null != result && !"".equals(result)) {
//                                        correlationId = result;
//                                    }
//                                } catch (XPathExpressionException e) {
//                                    e.printStackTrace();
//                                }
//                            } else {
//                                throw new RuntimeException("Body element in soap request not found");
//                            }
                        } else if (MethodType.CALLBACK.equals(mType)){
                            CorrelationIDCallbackHandler handler = (CorrelationIDCallbackHandler) message
                                    .get(CORRELATION_ID_CALLBACK_HANDLER);
                            if (handler != null)
                                correlationId = handler.getCorrelationId();
                        }
                        // Generate new ID if it was not set in callback or
                        // request
                        if (null == correlationId) {
                            correlationId = ContextUtils.generateUUID();
                        }
                    }
                    message.put("CorrelationID", correlationId);
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
