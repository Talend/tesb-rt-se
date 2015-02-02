package org.talend.esb.policy.transformation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.cxf.binding.soap.interceptor.SoapOutInterceptor;
import org.apache.cxf.interceptor.transform.TransformOutInterceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.talend.esb.policy.transformation.util.xslt.OutputXSLTUtil;

public class TransformationPolicyOutInterceptor extends AbstractTransformationPolicyInterceptor {

    private ConcurrentHashMap<String, OutputXSLTUtil> utilsCache
        = new ConcurrentHashMap<String, OutputXSLTUtil>();

    public TransformationPolicyOutInterceptor() {
        super(Phase.WRITE);
        addAfter(SoapOutInterceptor.class.getName());
    }

    public TransformationPolicyOutInterceptor(TransformationAssertion assertion) {
        super(Phase.WRITE, assertion);
        addAfter(SoapOutInterceptor.class.getName());
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

            OutputXSLTUtil xsltOut;
            if (utilsCache.containsKey(xsltPath)) {
                xsltOut = utilsCache.get(xsltPath);
            } else {
                xsltOut = new OutputXSLTUtil(xsltPath);
                utilsCache.put(xsltPath, xsltOut);
            }
            xsltOut.handleMessage(message);
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
        Map<String, String> outTransformMap = (Map<String, String>) map;
           TransformOutInterceptor simpleOut = new TransformOutInterceptor();
           simpleOut.setOutTransformElements(outTransformMap);
           simpleOut.handleMessage(message);
    }
}
