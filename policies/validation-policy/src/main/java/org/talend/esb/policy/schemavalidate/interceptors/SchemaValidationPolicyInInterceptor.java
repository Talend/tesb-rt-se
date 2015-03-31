package org.talend.esb.policy.schemavalidate.interceptors;


import org.apache.cxf.annotations.SchemaValidation.SchemaValidationType;
import org.apache.cxf.interceptor.StaxInInterceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.talend.esb.policy.schemavalidate.SchemaValidationPolicy;

public class SchemaValidationPolicyInInterceptor extends SchemaValidationPolicyAbstractInterceptor {


    /**
     * This constructor should be used when using interceptor
     * with service registry (validation activated via policies)
     */
    public SchemaValidationPolicyInInterceptor() {
          super(Phase.POST_STREAM);
          addAfter(StaxInInterceptor.class.getName());
    }


    /**
     * This constructor should be used when using interceptor
     * without service registry (e.g. with CXF feature
     * configured via Spring, Blueprint or programmatically
     * @param policy
     */
    public SchemaValidationPolicyInInterceptor(SchemaValidationPolicy policy) {
          super(Phase.POST_STREAM, policy);
          addAfter(StaxInInterceptor.class.getName());
    }

    @Override
    protected void validateBySettingProperty(Message message) {
        message.put(Message.SCHEMA_VALIDATION_ENABLED, SchemaValidationType.IN);
    }
}
