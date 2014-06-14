package org.talend.esb.mep.requestcallback.sample.internal;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.common.util.StringUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.ws.addressing.AddressingProperties;
import org.apache.cxf.ws.addressing.AttributedURIType;
import org.apache.cxf.ws.addressing.JAXWSAConstants;
import org.apache.cxf.ws.addressing.MAPAggregator;

public class ActionVerifierInterceptor extends AbstractPhaseInterceptor<SoapMessage> {

	public static final String ACTION_VERIFICATION_PROPERTY_NAME =
			"org.talend.esb.mep.requestcallback.ActionVerification";

	public ActionVerifierInterceptor() {
		super(Phase.PRE_LOGICAL);
		addAfter(MAPAggregator.class.getName());
	}

	@Override
	public void handleMessage(SoapMessage message) throws Fault {
		if (message.remove(ACTION_VERIFICATION_PROPERTY_NAME) != Boolean.TRUE) {
			return;
		}
		final AddressingProperties maps = (AddressingProperties) message.getContextualProperty(
				JAXWSAConstants.ADDRESSING_PROPERTIES_OUTBOUND);
		if (maps == null) {
			throw new Fault(new IllegalStateException(
					"Required WS-Addressing has not been set. "));
		}
		final AttributedURIType action = maps.getAction();
		if (action == null) {
			throw new Fault(new IllegalStateException(
					"Required WS-Addressing Action parameter has not been set. "));
		}
		final String actionValue = action.getValue();
		if (StringUtils.isEmpty(actionValue)) {
			throw new Fault(new IllegalStateException(
					"Required WS-Addressing Action parameter is not valid. "));
		}
	}

}
