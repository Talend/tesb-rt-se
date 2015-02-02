package org.talend.esb.policy.transformation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.cxf.interceptor.transform.TransformInInterceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.ws.security.wss4j.PolicyBasedWSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.talend.esb.policy.transformation.util.xslt.InputXSLTUtil;

public class TransformationPolicyInInterceptor extends AbstractTransformationPolicyInterceptor {

    private ConcurrentHashMap<String, InputXSLTUtil> utilsCache
        = new ConcurrentHashMap<String, InputXSLTUtil>();


    public TransformationPolicyInInterceptor() {
        super(Phase.PRE_PROTOCOL);
        addAfter(PolicyBasedWSS4JInInterceptor.class.getName());
        addAfter(WSS4JInInterceptor.class.getName());
    }

    public TransformationPolicyInInterceptor(TransformationAssertion assertion) {
        super(Phase.PRE_PROTOCOL, assertion);
        addAfter(PolicyBasedWSS4JInInterceptor.class.getName());
        addAfter(WSS4JInInterceptor.class.getName());
    }


    protected void proceedXSLT(Message message, TransformationAssertion tas) {
        String xsltPath = (String)message.getContextualProperty(XSLT_PATH);
        if (xsltPath == null) {
            xsltPath = tas.getPath();
        }
        if (xsltPath != null) {

            if (!shouldTransform(message, tas.getMessageType(), tas.getAppliesTo())) {
                return;
            }

            InputXSLTUtil xsltIn;
            if (utilsCache.containsKey(xsltPath)) {
                xsltIn = utilsCache.get(xsltPath);
            } else {
                xsltIn = new InputXSLTUtil(xsltPath);
                utilsCache.put(xsltPath, xsltIn);
            }
            xsltIn.handleMessage(message);
        }
    }

    protected void proceedSimple(Message message, TransformationAssertion tas) {

        if (!shouldTransform(message, tas.getMessageType(), tas.getAppliesTo())) {
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
