package org.talend.esb.mep.requestcallback.impl;

import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.headers.Header;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.ws.addressing.AddressingProperties;
import org.apache.cxf.ws.addressing.AttributedURIType;
import org.apache.cxf.ws.addressing.ContextUtils;
import org.apache.cxf.ws.addressing.EndpointReferenceType;
import org.apache.cxf.ws.addressing.JAXWSAConstants;
import org.apache.cxf.ws.addressing.MAPAggregator;
import org.apache.cxf.ws.addressing.RelatesToType;
//import org.apache.cxf.ws.addressing.impl.AddressingProperties;
import org.apache.cxf.ws.security.SecurityConstants;
import org.talend.esb.mep.requestcallback.feature.CallContext;
import org.talend.esb.mep.requestcallback.feature.RequestCallbackFeature;
import org.talend.esb.sam.agent.message.FlowIdHelper;

/**
 * The Class CompressionOutInterceptor.
 */
public class RequestCallbackOutInterceptor extends AbstractPhaseInterceptor<SoapMessage> {



	public RequestCallbackOutInterceptor() {
		super(Phase.PRE_LOGICAL);
		addBefore(MAPAggregator.class.getName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleMessage(SoapMessage message) throws Fault {
		final Exchange e = message.getExchange();
		if (!e.isOneWay()) {
			return;
		}
		doHandleSoapMessage(message);
	}

	private void doHandleSoapMessage(SoapMessage message) throws Fault {
		final Object callbackEndpoint = message.getContextualProperty(
				RequestCallbackFeature.CALLBACK_ENDPOINT_PROPERTY_NAME);
		if (callbackEndpoint != null) {
			final String callbackEndpointAddress;
			if (callbackEndpoint instanceof String) {
				callbackEndpointAddress = (String) callbackEndpoint;
			} else if (callbackEndpoint instanceof EndpointImpl) {
				callbackEndpointAddress = ((EndpointImpl) callbackEndpoint).getAddress();
			} else {
				throw new IllegalArgumentException("Unsupported type of endpoint. ");
			}
			doHandleRequestSoapMessage(message, callbackEndpointAddress);
			return;
		}
		final CallContext ctx = (CallContext) message.getContextualProperty(
				RequestCallbackFeature.CALLCONTEXT_PROPERTY_NAME);
		if (ctx != null) {
			doHandleCallbackSoapMessage(message, ctx);
			return;
		}
	}

	private void doHandleRequestSoapMessage(
			SoapMessage message, String callbackEndpoint) throws Fault {
		final String callId = ContextUtils.generateUUID();
		message.getHeaders().add(createHeader(
				RequestCallbackFeature.CALL_ID_HEADER_NAME, callId));
		Map<String, Object> requestInfo = getCallInfo(message);
		if (requestInfo != null) {
			requestInfo.put(RequestCallbackFeature.CALL_ID_NAME,
					callId);
		}
		aggregateAddressing(message, callbackEndpoint, null);
	}

	private void doHandleCallbackSoapMessage(
			SoapMessage message, CallContext callContext) throws Fault {
		final String callId = callContext.getCallId();
		final String correlationID = callContext.getCorrelationId();
		final String callbackId = ContextUtils.generateUUID();
		List<Header> headers = message.getHeaders();
		if(correlationID!=null){
			message.getHeaders().add(createHeader(
				RequestCallbackFeature.CORRELATION_ID_HEADER_NAME, correlationID));
		}
		message.getHeaders().add(createHeader(
				RequestCallbackFeature.CALL_ID_HEADER_NAME, callId));
		headers.add(createHeader(
				RequestCallbackFeature.CALLBACK_ID_HEADER_NAME, callbackId));
		Map<String, Object> requestInfo = getCallInfo(message);
		if (requestInfo != null) {
			requestInfo.put(RequestCallbackFeature.CALL_ID_NAME,
					callId);
			requestInfo.put(RequestCallbackFeature.CALLBACK_ID_NAME,
					callbackId);
		}

        String flowId = callContext.getFlowId();
        if (flowId != null && !flowId.isEmpty()) {
            FlowIdHelper.setFlowId(message, flowId);
        }

		aggregateAddressing(message, null, callContext.getRequestId());

		// In case of encryption propagate stored requestor certificate to callback response
		propagateRequestorCertificate(message, callContext);
	}

	private void aggregateAddressing(
			SoapMessage message, String callbackEndpoint, String relatesTo) {
		final AddressingProperties maps = initAddressingProperties(message);
		if (callbackEndpoint != null) {
			EndpointReferenceType replyTo= maps.getReplyTo();
	        if (replyTo == null || ContextUtils.isGenericAddress(replyTo)) {
	            EndpointReferenceType replyToRef = new EndpointReferenceType();
	            AttributedURIType address = new AttributedURIType();
	            address.setValue(callbackEndpoint);
	            replyToRef.setAddress(address);
	            maps.setReplyTo(replyToRef);
	        }
		}
/* Have to comment out "RelatesTo" setting after Upgrade CXF to 3.2.6
		if (maps.getRelatesTo() == null) {
	        RelatesToType relatesToAttr = new RelatesToType();
	        relatesToAttr.setRelationshipType("message");
	        relatesToAttr.setValue(relatesTo);
	        maps.setRelatesTo(relatesToAttr);
		}*/
	}

	private static Header createHeader(QName headerName, String value) throws Fault {
		try {
			return new Header(headerName, value, new JAXBDataBinding(String.class));
		} catch (JAXBException e) {
			throw new Fault(e);
		}
	}

	private static AddressingProperties initAddressingProperties(SoapMessage message) {
		AddressingProperties maps = (AddressingProperties) message.getContextualProperty(
				JAXWSAConstants.CLIENT_ADDRESSING_PROPERTIES);
		if (maps == null) {
			maps = new AddressingProperties();
			message.put(JAXWSAConstants.CLIENT_ADDRESSING_PROPERTIES, maps);
		}
		return maps;
	}

	@SuppressWarnings("unchecked")
	private static Map<String, Object> getCallInfo(SoapMessage message) {
		return (Map<String, Object>) message.getContextualProperty(
				RequestCallbackFeature.CALL_INFO_PROPERTY_NAME);
	}

    private static void propagateRequestorCertificate(Message message, CallContext callContext) {
    	if (callContext.getRequestorSignatureCertificate() != null) {
            message.put(SecurityConstants.ENCRYPT_CERT, callContext.getRequestorSignatureCertificate());
    	}
    }

}
