package org.talend.esb.policy.transformation.feature;

import org.apache.cxf.Bus;
import org.apache.cxf.common.injection.NoJSR250Annotations;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.interceptor.InterceptorProvider;
import org.talend.esb.policy.transformation.interceptor.XslPathProtocolAwareXSLTInInterceptor;
import org.talend.esb.policy.transformation.interceptor.XslPathProtocolAwareXSLTInterceptor;
import org.talend.esb.policy.transformation.interceptor.XslPathProtocolAwareXSLTOutInterceptor;


@NoJSR250Annotations
public class XSLTFeature extends AbstractFeature {
    private String inXSLTPath;
    private String outXSLTPath;
    
    @Override
    protected void initializeProvider(InterceptorProvider provider, Bus bus) {
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

}
