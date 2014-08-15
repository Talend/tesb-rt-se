package org.talend.esb.policy.transformation;

import java.util.Map;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.StaxInInterceptor;
import org.apache.cxf.interceptor.transform.TransformOutInterceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.ws.policy.AssertionInfo;
import org.talend.esb.policy.transformation.interceptor.XslPathProtocolAwareXSLTOutInterceptor;

public class TransformationPolicyOutInterceptor extends AbstractPhaseInterceptor<Message> {

	private static final String OUT_XSLT_PATH = "org.talend.esb.transformation.out.xslt-path";
	private static final String OUT_TRANSFORM_MAP = "org.talend.esb.transformation.out.transform-map";

	public TransformationPolicyOutInterceptor() {
		super(Phase.PRE_STREAM);
		addBefore(StaxInInterceptor.class.getName());
	}

    @Override
    public void handleMessage(Message message) {
    	AssertionInfo ai = null;
    	try {
    		ai = TransformationPolicyBuilder.getAssertion(message);		
    	} catch (Exception e) {
			throw new Fault(e);
    	}

    	if ((ai == null || !(ai.getAssertion() instanceof TransformationAssertion))) {
    		return;
    	}
    	
    	TransformationAssertion tas = (TransformationAssertion) ai.getAssertion();
    	
    	TransformationType transformationType = tas.getTransformationType();
    	if (transformationType == TransformationType.xslt) {
    		proceedXSLT(message, tas);
    	} else if (transformationType == TransformationType.simple) {
    		proceedSimple(message, tas);
    	}
    	
		if (ai != null) {
			ai.setAsserted(true);
		}				
    }

    private void proceedXSLT(Message message, TransformationAssertion tas) {
    	String outXSLTPath = (String)message.getContextualProperty(OUT_XSLT_PATH);
    	if (outXSLTPath == null) {
    		outXSLTPath = tas.getOutXSLTPath();
    	}
    	if (outXSLTPath != null) {
            //XSLTOutInterceptor xsltOut = new XSLTOutInterceptor(outXSLTPath);
            XslPathProtocolAwareXSLTOutInterceptor xsltOut
                = new XslPathProtocolAwareXSLTOutInterceptor(outXSLTPath);
            xsltOut.handleMessage(message);
    	}
    }

    private void proceedSimple(Message message, TransformationAssertion tas) {
    	Object map = message.getContextualProperty(OUT_TRANSFORM_MAP);
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
