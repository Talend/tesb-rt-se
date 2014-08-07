package org.talend.esb.policy.transformation;

import org.apache.cxf.feature.transform.XSLTInInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.StaxInInterceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.ws.policy.AssertionInfo;

public class XSLTInPolicyInterceptor extends AbstractPhaseInterceptor<Message> {

	public XSLTInPolicyInterceptor() {
		super(Phase.POST_STREAM);
		addBefore(StaxInInterceptor.class.getName());
	}

    @Override
    public void handleMessage(Message message) {
    	AssertionInfo ai = null;
    	try {
    		ai = TransformationPolicyBuilder.getAssertion(message);		
    	} catch (Exception e) {
			throw new Fault(e);
    	}

    	if ((ai == null || !(ai.getAssertion() instanceof TransformationAssertion))) {
    		return;
    	}
    	
    	TransformationAssertion tas = (TransformationAssertion) ai.getAssertion();
    	
    	if (tas.getInXSLTPath() != null) {
    		XSLTInInterceptor xsltIn = new XSLTInInterceptor(tas.getInXSLTPath());
    		xsltIn.handleMessage(message);
    	}
    	
		if (ai != null) {
			ai.setAsserted(true);
		}				
    }

}
