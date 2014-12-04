package org.talend.esb.policy.transformation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.cxf.interceptor.StaxInInterceptor;
import org.apache.cxf.interceptor.transform.TransformOutInterceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.talend.esb.policy.transformation.interceptor.xslt.HttpAwareXSLTOutInterceptor;

public class TransformationPolicyOutInterceptor extends AbstractTransformationPolicyInterceptor {

    private ConcurrentHashMap<String, HttpAwareXSLTOutInterceptor> interceptorCache
        = new ConcurrentHashMap<String, HttpAwareXSLTOutInterceptor>();

    public TransformationPolicyOutInterceptor() {
        super(Phase.PRE_STREAM);
        addBefore(StaxInInterceptor.class.getName());
    }

    public TransformationPolicyOutInterceptor(TransformationAssertion assertion) {
        super(Phase.PRE_STREAM, assertion);
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

            HttpAwareXSLTOutInterceptor xsltOut;
            if (interceptorCache.containsKey(xsltPath)) {
                xsltOut = interceptorCache.get(xsltPath);
            } else {
                xsltOut = new HttpAwareXSLTOutInterceptor(xsltPath);
                interceptorCache.put(xsltPath, xsltOut);
            }
            xsltOut.handleMessage(message);
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
        Map<String, String> outTransformMap = (Map<String, String>) map;
           TransformOutInterceptor simpleOut = new TransformOutInterceptor();
           simpleOut.setOutTransformElements(outTransformMap);
           simpleOut.handleMessage(message);
    }
}
