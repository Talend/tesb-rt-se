package org.talend.esb.policy.transformation.feature;

import org.apache.cxf.Bus;
import org.apache.cxf.common.injection.NoJSR250Annotations;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.interceptor.InterceptorProvider;
import org.talend.esb.policy.transformation.TransformationAssertion;
import org.talend.esb.policy.transformation.TransformationPolicyInInterceptor;
import org.talend.esb.policy.transformation.TransformationPolicyOutInterceptor;
import org.talend.esb.policy.transformation.TransformationType;
import org.talend.esb.policy.transformation.TransformationAssertion.AppliesToType;
import org.talend.esb.policy.transformation.TransformationAssertion.MessageType;;


@NoJSR250Annotations
public class TransformationFeature extends AbstractFeature {

    private String path;
    private MessageType messageType;
    private AppliesToType appliesTo;



    private TransformationType transformationType = TransformationType.xslt;

    @Override
    protected void initializeProvider(InterceptorProvider provider, Bus bus) {
        TransformationAssertion assertion =
                new TransformationAssertion(path, appliesTo, messageType, transformationType);

        TransformationPolicyInInterceptor in = new TransformationPolicyInInterceptor(assertion);
        provider.getInInterceptors().add(in);
        provider.getInInterceptors().add(in);

        TransformationPolicyOutInterceptor out = new TransformationPolicyOutInterceptor(assertion);
        provider.getOutInterceptors().add(out);
        provider.getOutFaultInterceptors().add(out);
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setType(String type) {
        transformationType = TransformationType.valueOf(type);
    }

    public void setAppliesTo(String appliesTo) {
        this.appliesTo = AppliesToType.valueOf(appliesTo);
    }

    public void setMessage(String messageType) {
        this.messageType = MessageType.valueOf(messageType);
    }
}
