package org.talend.esb.policy.transformation;

import java.util.Map;

import org.apache.cxf.interceptor.StaxInInterceptor;
import org.apache.cxf.interceptor.transform.TransformOutInterceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.talend.esb.policy.transformation.TransformationAssertion.AppliesToType;
import org.talend.esb.policy.transformation.TransformationAssertion.MessageType;
import org.talend.esb.policy.transformation.interceptor.xslt.HttpAwareXSLTOutInterceptor;

public class TransformationPolicyOutInterceptor extends AbstractTransformationPolicyInterceptor {


    public TransformationPolicyOutInterceptor() {
        super(Phase.PRE_STREAM);
        addBefore(StaxInInterceptor.class.getName());
    }


    protected void proceedXSLT(Message message, TransformationAssertion tas) {
        String xsltPath = (String)message.getContextualProperty(XSLT_PATH);
        if (xsltPath == null) {
            xsltPath = tas.getPath();
        }
        if (xsltPath != null) {

            MessageType msgType = MessageType.valueOf(tas.getMessageType());
            AppliesToType appliesToType  = AppliesToType.valueOf(tas.getAppliesTo());

            //XSLTOutInterceptor xsltOut = new XSLTOutInterceptor(outXSLTPath);
            HttpAwareXSLTOutInterceptor xsltOut
                = new HttpAwareXSLTOutInterceptor(xsltPath);
            xsltOut.setMsgType(msgType);
            xsltOut.setAppliesToType(appliesToType);
            xsltOut.handleMessage(message);
        }
    }


    protected void proceedSimple(Message message, TransformationAssertion tas) {

        MessageType msgType = MessageType.valueOf(tas.getMessageType());
        AppliesToType appliesToType  = AppliesToType.valueOf(tas.getAppliesTo());

        if (!shouldSchemaValidate(message, msgType, appliesToType)) {
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
