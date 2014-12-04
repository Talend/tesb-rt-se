package org.talend.esb.policy.transformation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.cxf.interceptor.StaxInInterceptor;
import org.apache.cxf.interceptor.transform.TransformInInterceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.talend.esb.policy.transformation.interceptor.xslt.HttpAwareXSLTInInterceptor;

public class TransformationPolicyInInterceptor extends AbstractTransformationPolicyInterceptor {

    private ConcurrentHashMap<String, HttpAwareXSLTInInterceptor> interceptorCache
        = new ConcurrentHashMap<String, HttpAwareXSLTInInterceptor>();


    public TransformationPolicyInInterceptor() {
        super(Phase.POST_STREAM);
        addBefore(StaxInInterceptor.class.getName());
    }

    public TransformationPolicyInInterceptor(TransformationAssertion assertion) {
        super(Phase.POST_STREAM, assertion);
        addBefore(StaxInInterceptor.class.getName());
    }


    protected void proceedXSLT(Message message, TransformationAssertion tas) {
        String xsltPath = (String)message.getContextualProperty(XSLT_PATH);
        if (xsltPath == null) {
            xsltPath = tas.getPath();
        }
        if (xsltPath != null) {

            if (!shouldSchemaValidate(message, tas.getMessageType(), tas.getAppliesTo())) {
                return;
            }

            HttpAwareXSLTInInterceptor xsltIn;
            if (interceptorCache.containsKey(xsltPath)) {
                xsltIn = interceptorCache.get(xsltPath);
            } else {
                xsltIn = new HttpAwareXSLTInInterceptor(xsltPath);
                interceptorCache.put(xsltPath, xsltIn);
            }
            xsltIn.handleMessage(message);
        }
    }

    protected void proceedSimple(Message message, TransformationAssertion tas) {

        if (!shouldSchemaValidate(message, tas.getMessageType(), tas.getAppliesTo())) {
            return;
        }

        Object map = message.getContextualProperty(TRANSFORM_MAP);
        if (!(map instanceof Map)) {
            return;
        }
        @SuppressWarnings("unchecked")
        Map<String, String> inTransformMap = (Map<String, String>) map;
           TransformInInterceptor simpleIn = new TransformInInterceptor();
           simpleIn.setInTransformElements(inTransformMap);
           simpleIn.handleMessage(message);
    }
}
