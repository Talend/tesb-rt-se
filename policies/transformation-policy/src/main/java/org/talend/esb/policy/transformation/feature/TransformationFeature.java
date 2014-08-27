package org.talend.esb.policy.transformation.feature;

import org.apache.cxf.Bus;
import org.apache.cxf.common.injection.NoJSR250Annotations;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.interceptor.InterceptorProvider;
import org.talend.esb.policy.transformation.TransformationType;
import org.talend.esb.policy.transformation.interceptor.xslt.XslPathProtocolAwareXSLTInInterceptor;
import org.talend.esb.policy.transformation.interceptor.xslt.XslPathProtocolAwareXSLTOutInterceptor;


@NoJSR250Annotations
public class TransformationFeature extends AbstractFeature {
    private String inXSLTPath;
    private String outXSLTPath;

    private TransformationType transformationType = TransformationType.xslt;

    @Override
    protected void initializeProvider(InterceptorProvider provider, Bus bus) {
        if (transformationType == TransformationType.xslt) {
            initializeXslt(provider);
        }
    }

    private void initializeXslt(InterceptorProvider provider) {
        if (inXSLTPath != null) {
            XslPathProtocolAwareXSLTInInterceptor in = new XslPathProtocolAwareXSLTInInterceptor(inXSLTPath);
            provider.getInInterceptors().add(in);
        }

        if (outXSLTPath != null) {
            XslPathProtocolAwareXSLTOutInterceptor out = new XslPathProtocolAwareXSLTOutInterceptor(outXSLTPath);
            provider.getOutInterceptors().add(out);
            provider.getOutFaultInterceptors().add(out);
        }
    }

    public void setInXSLTPath(String inXSLTPath) {
        this.inXSLTPath = inXSLTPath;
    }

    public void setOutXSLTPath(String outXSLTPath) {
        this.outXSLTPath = outXSLTPath;
    }

    public String getType() {
        return transformationType.toString();
    }

    public void setType(String type) {
        transformationType = TransformationType.valueOf(type);
    }

}
