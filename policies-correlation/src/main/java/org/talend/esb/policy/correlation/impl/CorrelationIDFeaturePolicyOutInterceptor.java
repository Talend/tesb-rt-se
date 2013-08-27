package org.talend.esb.policy.correlation.impl;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.xml.sax.SAXException;
import static org.talend.esb.policy.correlation.impl.CorrelationIDProcessor.process;

public class CorrelationIDFeaturePolicyOutInterceptor extends AbstractPhaseInterceptor<Message> {
    public CorrelationIDFeaturePolicyOutInterceptor() {
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
