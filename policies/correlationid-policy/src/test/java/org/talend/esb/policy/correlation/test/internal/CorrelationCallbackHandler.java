package org.talend.esb.policy.correlation.test.internal;


import org.talend.esb.policy.correlation.CorrelationIDCallbackHandler;

public class CorrelationCallbackHandler implements CorrelationIDCallbackHandler {

    @Override
    public String getCorrelationId() {
        return "customCorrelationHandler#" + Long.toString(System.currentTimeMillis());
    }
}
