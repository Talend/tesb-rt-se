package org.talend.esb.policy.schemavalidate.interceptors;

import java.io.InputStream;
import java.net.URL;
import java.util.Collection;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.cxf.common.classloader.ClassLoaderUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageUtils;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.ws.policy.AssertionInfo;
import org.apache.cxf.ws.policy.AssertionInfoMap;
import org.talend.esb.policy.schemavalidate.SchemaValidationPolicy;
import org.talend.esb.policy.schemavalidate.SchemaValidationPolicyBuilder;
import org.talend.esb.policy.schemavalidate.SchemaValidationPolicy.AppliesToType;
import org.talend.esb.policy.schemavalidate.SchemaValidationPolicy.MessageType;
import org.talend.esb.policy.schemavalidate.SchemaValidationPolicy.ValidationType;
import org.xml.sax.SAXException;

public abstract class SchemaValidationPolicyAbstractInterceptor extends AbstractPhaseInterceptor<Message> {

    protected SchemaValidationPolicy policy = null;

    public SchemaValidationPolicyAbstractInterceptor(String phase) {
        super(phase);
    }

    public SchemaValidationPolicyAbstractInterceptor(String phase, SchemaValidationPolicy policy) {
        super(phase);
        this.policy = policy;
    }



    @Override
    public void handleMessage(Message message) throws Fault {
        if (policy == null) {
            handleMessageWithAssertionInfo(message);
        } else {
            handleMessageWithoutAssertionInfo(message);
        }
    }


    protected void handleMessageWithAssertionInfo(Message message) throws Fault {
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


    protected void handleMessageWithoutAssertionInfo(Message message) throws Fault {

        ValidationType vldType = policy.getValidationType();
        AppliesToType appliesToType = policy.getApplyToType();
        MessageType msgType = policy.getMessageType();
        String customSchemaPath = policy.getCustomSchemaPath();

        if (shouldSchemaValidate(message, msgType, appliesToType)) {
            if(vldType == ValidationType.CustomSchema){
                // load custom schema from external source
                loadCustomSchema(message, customSchemaPath, this.getClass());
            }
            //do schema validation by setting value to "schema-validation-enabled" property
            validateBySettingProperty(message);
        }
    }


    protected void loadCustomSchema(Message message, String customSchemaPath, @SuppressWarnings("rawtypes") Class c){

        if(customSchemaPath==null || customSchemaPath.trim().isEmpty()){
            throw new IllegalArgumentException("Path to custom schema is not set or empty");
        }

        InputStream customSchemaStream = ClassLoaderUtils.getResourceAsStream(customSchemaPath, c);
        if (customSchemaStream == null) {
            // try to load schema as web resource
            try {
                URL url = new URL(customSchemaPath);
                customSchemaStream = url.openStream();
            } catch (Exception e) { }

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


    protected boolean shouldSchemaValidate(Message message, MessageType msgType, AppliesToType appliesToType) {
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

    protected abstract void validateBySettingProperty(Message message);
}
