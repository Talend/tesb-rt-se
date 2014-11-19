package org.talend.esb.policy.correlation.impl;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.cxf.binding.soap.saaj.SAAJInInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.neethi.Assertion;
import org.xml.sax.SAXException;

import static org.talend.esb.policy.correlation.impl.CorrelationIDProcessor.process;

public class CorrelationIDFeatureInInterceptor extends AbstractPhaseInterceptor<Message> {

	Assertion policy = null;

	public CorrelationIDFeatureInInterceptor() {
        super(Phase.PRE_PROTOCOL);
        addAfter(SAAJInInterceptor.class.getName());
    }

	public CorrelationIDFeatureInInterceptor(Assertion policy) {
        super(Phase.PRE_PROTOCOL);
        addAfter(SAAJInInterceptor.class.getName());
        this.policy = policy;
    }



    @Override
    public void handleMessage(Message message) throws Fault {
        try {
            process(message, policy);
        } catch (SAXException e) {
            throw new Fault(e);
        } catch (IOException e) {
            throw new Fault(e);
        } catch (ParserConfigurationException e) {
            throw new Fault(e);
        }
    }

}
