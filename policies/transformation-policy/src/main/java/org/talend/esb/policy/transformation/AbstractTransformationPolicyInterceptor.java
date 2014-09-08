package org.talend.esb.policy.transformation;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageUtils;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.ws.policy.AssertionInfo;
import org.talend.esb.policy.transformation.TransformationAssertion.AppliesToType;
import org.talend.esb.policy.transformation.TransformationAssertion.MessageType;

public abstract class AbstractTransformationPolicyInterceptor extends AbstractPhaseInterceptor<Message> {

    protected static final String XSLT_PATH     = "org.talend.esb.transformation.xslt-path";
    protected static final String TRANSFORM_MAP = "org.talend.esb.transformation.transform-map";

    public AbstractTransformationPolicyInterceptor(String phase) {
        super(phase);
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

        TransformationType transformationType = tas.getTransformationType();
        if (transformationType == TransformationType.xslt) {
            proceedXSLT(message, tas);
        } else if (transformationType == TransformationType.simple) {
            proceedSimple(message, tas);
        }

        if (ai != null) {
            ai.setAsserted(true);
        }
    }


    protected abstract void proceedXSLT(Message message, TransformationAssertion tas);
    protected abstract void proceedSimple(Message message, TransformationAssertion tas);


    protected boolean shouldSchemaValidate(Message message, MessageType msgType, AppliesToType appliesToType) {
        if (MessageUtils.isRequestor(message)) {
            if (MessageUtils.isOutbound(message)) { // REQ_OUT
                return ((appliesToType == AppliesToType.consumer || appliesToType == AppliesToType.always)
                    && (msgType == MessageType.request || msgType == MessageType.all));
            } else { // RESP_IN
                return ((appliesToType == AppliesToType.consumer || appliesToType == AppliesToType.always)
                    && (msgType == MessageType.response || msgType == MessageType.all));
            }
        } else {
            if (MessageUtils.isOutbound(message)) { // RESP_OUT
                return ((appliesToType == AppliesToType.provider || appliesToType == AppliesToType.always)
                    && (msgType == MessageType.response || msgType == MessageType.all));
            } else { // REQ_IN
                return ((appliesToType == AppliesToType.provider || appliesToType == AppliesToType.always)
                    && (msgType == MessageType.request || msgType == MessageType.all));
            }
        }
    }
}
