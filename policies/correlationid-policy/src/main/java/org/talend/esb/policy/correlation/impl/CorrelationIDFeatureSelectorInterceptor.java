package org.talend.esb.policy.correlation.impl;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.saaj.SAAJInInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.neethi.Assertion;

public class CorrelationIDFeatureSelectorInterceptor extends AbstractPhaseInterceptor<Message> {
	
	Assertion policy = null;
	
	public CorrelationIDFeatureSelectorInterceptor() {
        super(Phase.READ);
    }
	
	public CorrelationIDFeatureSelectorInterceptor(Assertion policy) {
        super(Phase.READ);
        this.policy = policy;
    }	
	
	

    @Override
    public void handleMessage(Message message) throws Fault {
        if(message instanceof SoapMessage){
        	message.getInterceptorChain().add(new SAAJInInterceptor());
        }else{
        	//Skip for not SOAP messages
        }
    }
}
