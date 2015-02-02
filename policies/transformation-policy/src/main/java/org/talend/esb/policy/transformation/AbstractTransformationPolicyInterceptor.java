package org.talend.esb.policy.transformation;

import java.util.Collection;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageUtils;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.ws.policy.AssertionInfo;
import org.apache.cxf.ws.policy.AssertionInfoMap;
import org.talend.esb.policy.transformation.TransformationAssertion.AppliesToType;
import org.talend.esb.policy.transformation.TransformationAssertion.MessageType;

public abstract class AbstractTransformationPolicyInterceptor extends AbstractPhaseInterceptor<Message> {

    protected static final String XSLT_PATH     = "org.talend.esb.transformation.xslt-path";
    protected static final String TRANSFORM_MAP = "org.talend.esb.transformation.transform-map";

    private TransformationAssertion featureAssertion;

    public AbstractTransformationPolicyInterceptor(String phase) {
        super(phase);
    }

    public AbstractTransformationPolicyInterceptor(String phase, TransformationAssertion assertion) {
        super(phase);
        if (assertion == null) {
            throw new IllegalArgumentException("Provided assertion is null");
        }
        featureAssertion = assertion;
    }

    @Override
    public void handleMessage(Message message) {
        AssertionInfo ai = null;
        try {
            ai = TransformationPolicyBuilder.getAssertion(message);
        } catch (Exception e) {
            throw new Fault(e);
        }


        TransformationAssertion tas;
        if ((ai == null || !(ai.getAssertion() instanceof TransformationAssertion))) {
            if (featureAssertion != null) {
                tas = featureAssertion;
            } else {
                confirmPolicyProcessing(message);
                return;
            }
        } else {
            tas = (TransformationAssertion) ai.getAssertion();
        }

        TransformationType transformationType = tas.getTransformationType();
        if (transformationType == TransformationType.xslt) {
            proceedXSLT(message, tas);
        } else if (transformationType == TransformationType.simple) {
            proceedSimple(message, tas);
        }

       confirmPolicyProcessing(message);
    }


    protected abstract void proceedXSLT(Message message, TransformationAssertion tas);
    protected abstract void proceedSimple(Message message, TransformationAssertion tas);


    protected boolean shouldTransform(Message message, MessageType msgType, AppliesToType appliesToType) {
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


    protected void confirmPolicyProcessing(Message message) {
         AssertionInfoMap aim = message.get(AssertionInfoMap.class);
         if (aim != null) {
             Collection<AssertionInfo> ais = aim
                       .get(TransformationPolicyBuilder.TRANSFORMATION);

             if (ais != null) {
                 for (AssertionInfo ai : ais) {
                     if (ai.getAssertion() instanceof TransformationAssertion) {
                         ai.setAsserted(true);
                     }
                 }
             }
         }
    }
}
