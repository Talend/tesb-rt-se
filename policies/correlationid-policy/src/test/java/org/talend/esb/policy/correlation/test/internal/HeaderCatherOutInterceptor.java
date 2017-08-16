package org.talend.esb.policy.correlation.test.internal;


import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.talend.esb.policy.correlation.feature.CorrelationIDFeature;

public class HeaderCatherOutInterceptor extends AbstractPhaseInterceptor<Message> {

    private Object latestCorrelationHeader;

    public HeaderCatherOutInterceptor() {
        super(Phase.PRE_PROTOCOL);
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        latestCorrelationHeader = message.get(CorrelationIDFeature.MESSAGE_CORRELATION_ID);
    }

    public Object getLatestCorrelationHeader() {
        return latestCorrelationHeader;
    }
}
