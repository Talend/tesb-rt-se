package org.talend.esb.policy.schemavalidate;

import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.cxf.annotations.SchemaValidation.SchemaValidationType;
import org.apache.cxf.common.classloader.ClassLoaderUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageUtils;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.ws.policy.AbstractPolicyInterceptorProvider;
import org.apache.cxf.ws.policy.AssertionInfo;
import org.apache.cxf.ws.policy.AssertionInfoMap;
import org.talend.esb.policy.schemavalidate.SchemaValidationPolicy.MessageType;
import org.talend.esb.policy.schemavalidate.SchemaValidationPolicy.AppliesToType;
import org.talend.esb.policy.schemavalidate.SchemaValidationPolicy.ValidationType;
import org.xml.sax.SAXException;


public class SchemaValidationInterceptorProvider extends AbstractPolicyInterceptorProvider {

    private static final long serialVersionUID = 4222227474541786883L;

    public SchemaValidationInterceptorProvider() {
        super(Arrays.asList(SchemaValidationPolicyBuilder.SCHEMA_VALIDATION));

        this.getOutInterceptors().add(new SchemaValidationPolicyOutInterceptor());
        this.getOutFaultInterceptors().add(new SchemaValidationPolicyOutInterceptor());
        this.getInInterceptors().add(new SchemaValidationPolicyInInterceptor());
        this.getInFaultInterceptors().add(new SchemaValidationPolicyInInterceptor());
    }

    static class SchemaValidationPolicyInInterceptor extends AbstractPhaseInterceptor<Message> {

        public SchemaValidationPolicyInInterceptor() {
            super(Phase.RECEIVE);
        }

        @Override
        public void handleMessage(Message message) throws Fault {
            AssertionInfoMap aim = message.get(AssertionInfoMap.class);
            if (null == aim) {
                return;
            }

            Collection<AssertionInfo> ais = aim.get(SchemaValidationPolicyBuilder.SCHEMA_VALIDATION);
            if (null == ais) {
                return;
            }

            for (AssertionInfo ai : ais) {
                if (ai.getAssertion() instanceof SchemaValidationPolicy) {
                    SchemaValidationPolicy vPolicy = (SchemaValidationPolicy) ai.getAssertion();
                    ValidationType vldType = vPolicy.getValidationType();
                    AppliesToType appliesToType = vPolicy.getApplyToType();
                    MessageType msgType = vPolicy.getMessageType();
                    String customSchemaPath = vPolicy.getCustomSchemaPath();

                    if (vldType != ValidationType.WSDLSchema) {
                        ai.setAsserted(true);
                    }

                    if (shouldSchemaValidate(message, msgType, appliesToType)) {
                        if(vldType == ValidationType.CustomSchema){
                        	// load custom schema from external source
                    		loadCustomSchema(message, customSchemaPath, this.getClass());
                        }	
                        //do schema validation by setting value to "schema-validation-enabled" property
                        validateBySettingProperty(message);
                    }

                    ai.setAsserted(true);
                }
                ai.setAsserted(true);
            }

        }

        private void validateBySettingProperty(Message message) {
            message.setContextualProperty(Message.SCHEMA_VALIDATION_ENABLED, SchemaValidationType.IN);
        }
    }

    static class SchemaValidationPolicyOutInterceptor extends AbstractPhaseInterceptor<Message> {

        public SchemaValidationPolicyOutInterceptor() {
            //use Phase.SETUP in case of using validateBySettingProperty()
            super(Phase.SETUP);
            //super(Phase.MARSHAL);
        }

        @Override
        public void handleMessage(Message message) throws Fault {
            AssertionInfoMap aim = message.get(AssertionInfoMap.class);
            if (null == aim) {
                return;
            }

            Collection<AssertionInfo> ais = aim.get(SchemaValidationPolicyBuilder.SCHEMA_VALIDATION);
            if (null == ais) {
                return;
            }

            for (AssertionInfo ai : ais) {
                if (ai.getAssertion() instanceof SchemaValidationPolicy) {
                    SchemaValidationPolicy vPolicy = (SchemaValidationPolicy) ai.getAssertion();
                    ValidationType vldType = vPolicy.getValidationType();
                    AppliesToType appliesToType = vPolicy.getApplyToType();
                    MessageType msgType = vPolicy.getMessageType();
                    String customSchemaPath = vPolicy.getCustomSchemaPath();

                    if (vldType != ValidationType.WSDLSchema) {
                        ai.setAsserted(true);
                    }

                    if (shouldSchemaValidate(message, msgType, appliesToType)) {
                        if(vldType == ValidationType.CustomSchema){
                        	// load custom schema from external source
                    		loadCustomSchema(message, customSchemaPath, this.getClass());
                        }
                        //do schema validation by setting value to "schema-validation-enabled" property
                        validateBySettingProperty(message);
                    }
                    ai.setAsserted(true);
                }
                ai.setAsserted(true);
            }

        }

        private void validateBySettingProperty(Message message) {
            message.setContextualProperty(Message.SCHEMA_VALIDATION_ENABLED, SchemaValidationType.OUT);
        }

    }
    
    static void loadCustomSchema(Message message, String customSchemaPath, @SuppressWarnings("rawtypes") Class c){
    	
    	if(customSchemaPath==null || customSchemaPath.trim().isEmpty()){
    		throw new IllegalArgumentException("Path to custom schema is not set or empty");
    	}
    	
    	InputStream customSchemaStream = ClassLoaderUtils.getResourceAsStream(customSchemaPath, c);
    	if (customSchemaStream == null) {
    		// try to load schema as web resource
			try {
				URL url = new URL(customSchemaPath);
				customSchemaStream = url.openStream();
			} catch (Exception e) {
			}
			
			if(customSchemaStream == null){
				throw new IllegalArgumentException("Cannot load custom schema from path: " + customSchemaPath);
			}
    		
        }
    	
    	SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    	Source src = new StreamSource(customSchemaStream);
    	Schema customSchema;
		try {
			customSchema = factory.newSchema(src);
		} catch (SAXException e) {
			throw new IllegalArgumentException("Cannot create custom schema from path: " + customSchemaPath, e);
		}
    	message.getExchange().getService().getServiceInfos().get(0).setProperty(Schema.class.getName(), customSchema);
    }    

    static boolean shouldSchemaValidate(Message message, MessageType msgType, AppliesToType appliesToType) {
        if (MessageUtils.isRequestor(message)) {
            if (MessageUtils.isOutbound(message)) { // REQ_OUT
                return ((appliesToType == AppliesToType.consumer || appliesToType == AppliesToType.always)
                        && (msgType == MessageType.request || msgType == MessageType.all));
            } else { // RESP_IN
                return ((appliesToType == AppliesToType.consumer || appliesToType == AppliesToType.always)
                        && (msgType == MessageType.response || msgType == MessageType.all));
            }
        } else {
            if (MessageUtils.isOutbound(message)) { // RESP_OUT
                return ((appliesToType == AppliesToType.provider || appliesToType == AppliesToType.always)
                        && (msgType == MessageType.response || msgType == MessageType.all));
            } else { // REQ_IN
                return ((appliesToType == AppliesToType.provider || appliesToType == AppliesToType.always)
                        && (msgType == MessageType.request || msgType == MessageType.all));
            }
        }
    }

}
