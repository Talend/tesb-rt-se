package org.talend.esb.policy.transformation;

import java.util.Map;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.StaxInInterceptor;
import org.apache.cxf.interceptor.transform.TransformInInterceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.ws.policy.AssertionInfo;
import org.talend.esb.policy.transformation.interceptor.xslt.HttpAwareXSLTInInterceptor;

public class TransformationPolicyInInterceptor extends AbstractPhaseInterceptor<Message> {

	private static final String IN_XSLT_PATH = "org.talend.esb.transformation.in.xslt-path";
	private static final String IN_TRANSFORM_MAP = "org.talend.esb.transformation.in.transform-map";

	public TransformationPolicyInInterceptor() {
		super(Phase.POST_STREAM);
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
    	String inXSLTPath = (String)message.getContextualProperty(IN_XSLT_PATH);
    	if (inXSLTPath == null) {
    		inXSLTPath = tas.getInXSLTPath();
    	}
    	if (inXSLTPath != null) {
            //XSLTInInterceptor xsltIn = new XSLTInInterceptor(inXSLTPath);
            HttpAwareXSLTInInterceptor xsltIn
                = new HttpAwareXSLTInInterceptor(inXSLTPath);
            xsltIn.handleMessage(message);
    	}
    }

    private void proceedSimple(Message message, TransformationAssertion tas) {
    	Object map = message.getContextualProperty(IN_TRANSFORM_MAP);
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
