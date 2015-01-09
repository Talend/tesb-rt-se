package org.talend.esb.mep.requestcallback.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import javax.wsdl.Definition;
import javax.xml.namespace.QName;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.headers.Header;
import org.apache.cxf.helpers.DOMUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.service.model.BindingInfo;
import org.apache.cxf.ws.addressing.AddressingProperties;
import org.apache.cxf.ws.addressing.JAXWSAConstants;
import org.apache.cxf.ws.addressing.MAPAggregator;
import org.apache.cxf.wsdl.WSDLManager;
import org.talend.esb.mep.requestcallback.feature.CallContext;
import org.talend.esb.mep.requestcallback.feature.CallbackInfo;
import org.talend.esb.mep.requestcallback.feature.RequestCallbackFeature;
import org.talend.esb.sam.agent.flowidprocessor.FlowIdProtocolHeaderCodec;
import org.talend.esb.sam.agent.flowidprocessor.FlowIdSoapCodec;
import org.talend.esb.sam.agent.message.FlowIdHelper;
import org.w3c.dom.Element;

/**
 * The Class CompressionOutInterceptor.
 */
public class RequestCallbackInInterceptor extends AbstractPhaseInterceptor<SoapMessage> {

	private static final String SR_QUERY_PATH = "/services/registry/lookup/wsdl/";
	private static final int SR_QUERY_PATH_LEN = SR_QUERY_PATH.length();

	public RequestCallbackInInterceptor() {
		super(Phase.PRE_LOGICAL);
		addAfter(MAPAggregator.class.getName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleMessage(SoapMessage message) throws Fault {
		final Header callHeader = message.getHeader(
				RequestCallbackFeature.CALL_ID_HEADER_NAME);
		if (callHeader == null) {
			return;
		}
		final Exchange e = message.getExchange();
		if (!e.isOneWay()) {
			e.setOneWay(true);
		}
		final Header callbackHeader = message.getHeader(
				RequestCallbackFeature.CALLBACK_ID_HEADER_NAME);
		if (callbackHeader == null) {
			doHandleRequestSoapMessage(message, callHeader);
		} else {
			doHandleCallbackSoapMessage(message, callHeader, callbackHeader);
		}
	}

	private void doHandleRequestSoapMessage(
			SoapMessage message, Header callHeader) throws Fault {
		setupCallContext(message, callHeader, null);
	}

	private void doHandleCallbackSoapMessage(
			SoapMessage message, Header callHeader, Header callbackHeader) throws Fault {
		setupCallContext(message, callHeader, callbackHeader);
		setupFlowId(message);
	}

	private void setupCallContext(
			SoapMessage message, Header callHeader, Header callbackHeader) throws Fault {

		final AddressingProperties maps = getAddressingProperties(message);
		if (maps == null) {
			throw new IllegalStateException(
					"Request-Callback enabled but no WS-Addressing headers set. ");
		}
		CallContext ctx = new CallContext();
		message.put(RequestCallbackFeature.CALLCONTEXT_PROPERTY_NAME, ctx);
		final QName operationName = QName.valueOf(maps.getAction().getValue());
		if (!isGenericOperation(operationName)) {
			ctx.setOperationName(operationName);
		}
		ctx.setCallId(valueOf(callHeader));
		if (callbackHeader != null) {
			ctx.setCallbackId(valueOf(callbackHeader));
		}
		ctx.setRequestId(maps.getMessageID().getValue());
		ctx.setReplyToAddress(maps.getReplyTo().getAddress().getValue());
		ctx.setCorrelationId(getCorrelationId(message));

		// Try to get SAM flowId in request message
		// to store it in CallContext for subsequent use
		// in callback message
		if (callbackHeader == null) {
		    setupFlowId(message);
		}

		fillCallContext(ctx, message);
	}

	private static String getCorrelationId(SoapMessage message) {
		Header h = message.getHeader(RequestCallbackFeature.CORRELATION_ID_HEADER_NAME);
		if(h!=null){
			return valueOf(h);
		}
		return null;
	}

	private static AddressingProperties getAddressingProperties(SoapMessage message) {
		AddressingProperties maps = (AddressingProperties) message.get(
				JAXWSAConstants.CLIENT_ADDRESSING_PROPERTIES);
		if (maps == null) {
			maps = (AddressingProperties) message.get(
					JAXWSAConstants.ADDRESSING_PROPERTIES_INBOUND);
		}
		return maps;
	}

	private static String valueOf(Header header) {
		final Object headerObject = header.getObject();
    	if (headerObject == null) {
    		return null;
    	}
    	if (headerObject instanceof String) {
    		return (String) headerObject;
    	}
    	if (headerObject instanceof Element) {
    		return DOMUtils.getContent((Element) headerObject);
    	}
    	return null;
	}

	private static boolean isGenericOperation(QName operationName) {
		final String name = operationName.getLocalPart();
		return name.startsWith("http://cxf.apache.org/jaxws") && name.endsWith("InvokeOneWayRequest");
	}

	public static void fillCallContext(CallContext callContext, SoapMessage message) {
		if (callContext.getOperationName() == null) {
			callContext.setOperationName((QName) message.get(Message.WSDL_OPERATION));
		}
		callContext.setPortTypeName((QName) message.get(Message.WSDL_INTERFACE));
		callContext.setServiceName((QName) message.get(Message.WSDL_SERVICE));
		final BindingInfo bi = message.getExchange().getBinding().getBindingInfo();
		callContext.setBindingId(bi == null
				? "http://schemas.xmlsoap.org/wsdl/soap/" : bi.getBindingId());
		URL wsdlLocation = resolveCallbackWsdlLocation(callContext.getServiceName(), message);
		if (wsdlLocation != null) {
			callContext.setWsdlLocation(wsdlLocation);
		}
        String flowId = FlowIdHelper.getFlowId(message);
        if (flowId != null && !flowId.isEmpty()) {
            callContext.setFlowId(flowId);
        }
	}

	private static URL resolveCallbackWsdlLocation(QName callbackService, SoapMessage message) {
		final WSDLManager wsdlManager = message.getExchange().getBus().getExtension(WSDLManager.class);
		for (Map.Entry<Object, Definition> entry : wsdlManager.getDefinitions().entrySet()) {
			if (entry.getValue().getService(callbackService) != null) {
				final Object key = entry.getKey();
				if (key instanceof URL) {
					return asCallbackWsdlURL((URL) entry.getKey());
				}
				if (key instanceof String) {
					final String loc = (String) key;
					if (loc.startsWith("file:") || loc.indexOf("://") > 0) {
						try {
							return asCallbackWsdlURL(new URL(loc));
						} catch (MalformedURLException e) {
							throw new IllegalStateException("Corrupted WSDL location URL: ", e);
						}
					}
				}
			}
		}
		return null;
	}

	private static URL asCallbackWsdlURL(URL wsdlURL) {
		if (wsdlURL == null) {
			return null;
		}
		final CallbackInfo cbInfo = CallContext.createCallbackInfo(wsdlURL);
		if (cbInfo.getCallbackServiceName() == null) {
			// old-style callback definition without callback service.
			return null;
		}
		String protocol = wsdlURL.getProtocol();
		if (!("http".equals(protocol) || "https".equals(protocol))) {
			// not a service registry query, return as it is.
			return wsdlURL;
		}
		final String path = wsdlURL.getPath();
		if (!path.startsWith("/services/registry/lookup/wsdl/")) {
			// not a service registry query, return as it is.
			return wsdlURL;
		}
		try {
			final String urlString = wsdlURL.toExternalForm();
			final String resString =  urlString.substring(0,
					urlString.indexOf(SR_QUERY_PATH) + SR_QUERY_PATH_LEN)
					+ URLEncoder.encode(cbInfo.getCallbackServiceName().toString(), "UTF-8")
					+ "?mergeWithPolicies=true&participant=provider";
			return new URL(resString);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalStateException("Unexpected URL creation problem: ", e);
		}
	}

	/**
     * This functions reads SAM flowId and sets it
     * as message property for subsequent store in CallContext
     * @param message
     */
    private static void setupFlowId(SoapMessage message) {
        String flowId = FlowIdHelper.getFlowId(message);

        if (flowId == null) {
            flowId = FlowIdProtocolHeaderCodec.readFlowId(message);
        }

        if (flowId == null) {
            flowId = FlowIdSoapCodec.readFlowId(message);
        }

        if (flowId == null) {
            Exchange ex = message.getExchange();
            if (null!=ex){
                Message reqMsg = ex.getOutMessage();
                if ( null != reqMsg) {
                    flowId = FlowIdHelper.getFlowId(reqMsg);
                }
            }
        }

        if (flowId != null && !flowId.isEmpty()) {
            FlowIdHelper.setFlowId(message, flowId);
        }
    }
}
