package org.talend.esb.mep.requestcallback.impl;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.SoapActionInInterceptor;
import org.apache.cxf.binding.soap.interceptor.SoapActionInInterceptor.SoapActionInAttemptTwoInterceptor;
import org.apache.cxf.binding.soap.model.SoapOperationInfo;
import org.apache.cxf.common.util.StringUtils;
import org.apache.cxf.headers.Header;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.ws.addressing.AddressingProperties;
import org.apache.cxf.ws.addressing.AttributedURIType;
import org.apache.cxf.ws.addressing.JAXWSAConstants;
import org.apache.cxf.ws.addressing.MAPAggregator;
import org.apache.cxf.ws.addressing.Names;
import org.talend.esb.mep.requestcallback.feature.RequestCallbackFeature;

public class CallbackActionInterceptor extends AbstractPhaseInterceptor<SoapMessage> {

	public CallbackActionInterceptor() {
        super(Phase.PRE_LOGICAL);
        addBefore(SoapActionInAttemptTwoInterceptor.class.getName());
		addBefore(MAPAggregator.class.getName());
	}

	@Override
	public void handleMessage(SoapMessage message) throws Fault {
		final Header callHeader = message.getHeader(
				RequestCallbackFeature.CALL_ID_HEADER_NAME);
		if (callHeader == null) {
			return;
		}
		final Exchange e = message.getExchange();
		if (!e.isOneWay()) {
			AddressingProperties maps = (AddressingProperties) message.getContextualProperty(
					JAXWSAConstants.ADDRESSING_PROPERTIES_INBOUND);
			if (maps != null && maps.getReplyTo() != null) {
				AttributedURIType addr = maps.getReplyTo().getAddress();
				if (addr != null) {
					String replyTo = addr.getValue();
					if (Names.WSA_NONE_ADDRESS.equals(replyTo)) {
						addr.setValue(Names.WSA_ANONYMOUS_ADDRESS);
					}
				}
			}
		}
		final Header callbackHeader = message.getHeader(
				RequestCallbackFeature.CALLBACK_ID_HEADER_NAME);
		if (callbackHeader == null) {
			return;
		}
        final BindingOperationInfo boi = message.getExchange().getBindingOperationInfo();
        if (boi == null) {
            return;
        }
        final String action = SoapActionInInterceptor.getSoapAction(message);
        if (StringUtils.isEmpty(action)) {
            return;
        }
        final SoapOperationInfo soi = boi.getExtensor(SoapOperationInfo.class);
        if (soi == null) {
            return;
        }
        if (StringUtils.isEmpty(soi.getAction())) {
        	soi.setAction(action);
        }
	}

}
