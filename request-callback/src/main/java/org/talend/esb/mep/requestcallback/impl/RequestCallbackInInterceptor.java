package org.talend.esb.mep.requestcallback.impl;

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
import org.talend.esb.mep.requestcallback.feature.CallContext;
import org.talend.esb.mep.requestcallback.feature.RequestCallbackFeature;
import org.talend.esb.sam.agent.flowidprocessor.FlowIdProtocolHeaderCodec;
import org.talend.esb.sam.agent.flowidprocessor.FlowIdSoapCodec;
import org.talend.esb.sam.agent.message.FlowIdHelper;
import org.w3c.dom.Element;

/**
 * The Class CompressionOutInterceptor.
 */
public class RequestCallbackInInterceptor extends AbstractPhaseInterceptor<SoapMessage> {

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

		// Try to get SAM flowId in request message
		// to store it in CallContext for subsequent use
		// in callback message
		if (callbackHeader == null) {
		    setupFlowId(message);
		}

		fillCallContext(ctx, message);
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

	private static void fillCallContext(CallContext callContext, SoapMessage message) {
		if (callContext.getOperationName() == null) {
			callContext.setOperationName((QName) message.get(Message.WSDL_OPERATION));
		}
		callContext.setPortTypeName((QName) message.get(Message.WSDL_INTERFACE));
		callContext.setServiceName((QName) message.get(Message.WSDL_SERVICE));
		BindingInfo bi = message.getExchange().getBinding().getBindingInfo();
		callContext.setBindingId(bi == null
				? "http://schemas.xmlsoap.org/wsdl/soap/" : bi.getBindingId());

        String flowId = FlowIdHelper.getFlowId(message);
        if (flowId != null && !flowId.isEmpty()) {
            callContext.setFlowId(flowId);
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
