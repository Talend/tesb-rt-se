package org.talend.esb.policy.transformation.feature;

import org.apache.cxf.Bus;
import org.apache.cxf.common.injection.NoJSR250Annotations;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.interceptor.InterceptorProvider;
import org.talend.esb.policy.transformation.TransformationType;
import org.talend.esb.policy.transformation.interceptor.xslt.HttpAwareXSLTInInterceptor;
import org.talend.esb.policy.transformation.interceptor.xslt.HttpAwareXSLTOutInterceptor;
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
        if (transformationType == TransformationType.xslt) {
            initializeXslt(provider);
        }
    }

    private void initializeXslt(InterceptorProvider provider) {
        if (path != null) {
            HttpAwareXSLTInInterceptor in = new HttpAwareXSLTInInterceptor(path);
            in.setAppliesToType(appliesTo);
            in.setMsgType(messageType);
            provider.getInInterceptors().add(in);

            HttpAwareXSLTOutInterceptor out = new HttpAwareXSLTOutInterceptor(path);
            out.setAppliesToType(appliesTo);
            out.setMsgType(messageType);
            provider.getOutInterceptors().add(out);
            provider.getOutFaultInterceptors().add(out);
        }
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
