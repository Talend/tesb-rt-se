package org.talend.esb.policy.transformation;

import java.util.Map;

import org.apache.cxf.interceptor.StaxInInterceptor;
import org.apache.cxf.interceptor.transform.TransformInInterceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.talend.esb.policy.transformation.TransformationAssertion.AppliesToType;
import org.talend.esb.policy.transformation.TransformationAssertion.MessageType;
import org.talend.esb.policy.transformation.interceptor.xslt.HttpAwareXSLTInInterceptor;

public class TransformationPolicyInInterceptor extends AbstractTransformationPolicyInterceptor {


    public TransformationPolicyInInterceptor() {
        super(Phase.POST_STREAM);
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

            //XSLTInInterceptor xsltIn = new XSLTInInterceptor(inXSLTPath);
            HttpAwareXSLTInInterceptor xsltIn
                = new HttpAwareXSLTInInterceptor(xsltPath);
            xsltIn.setMsgType(msgType);
            xsltIn.setAppliesToType(appliesToType);
            xsltIn.handleMessage(message);
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
        Map<String, String> inTransformMap = (Map<String, String>) map;
           TransformInInterceptor simpleIn = new TransformInInterceptor();
           simpleIn.setInTransformElements(inTransformMap);
           simpleIn.handleMessage(message);
    }
}
