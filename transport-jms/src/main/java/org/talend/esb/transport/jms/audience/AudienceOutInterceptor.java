package org.talend.esb.transport.jms.audience;


import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;


public class AudienceOutInterceptor extends AbstractPhaseInterceptor<Message> {

    public AudienceOutInterceptor() {
        super(Phase.SETUP);
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        if (message.getExchange() != null
            && message.getExchange().getEndpoint() != null
            && message.getExchange().getEndpoint().getService() != null
            && message.getExchange().getEndpoint().getService().getName() != null) {
            message.put("security.sts.applies-to",
                        message.getExchange().getEndpoint().getService().getName().toString());
        }
    }
}
