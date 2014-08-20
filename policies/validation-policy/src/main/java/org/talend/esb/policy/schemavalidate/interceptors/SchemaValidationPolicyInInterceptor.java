package org.talend.esb.policy.schemavalidate.interceptors;


import org.apache.cxf.annotations.SchemaValidation.SchemaValidationType;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.talend.esb.policy.schemavalidate.SchemaValidationPolicy;

public class SchemaValidationPolicyInInterceptor extends SchemaValidationPolicyAbstractInterceptor {


    /**
     * This constructor should be used when using interceptor
     * with service registry (validation activated via policies)
     */
    public SchemaValidationPolicyInInterceptor() {
          super(Phase.RECEIVE);
    }


    /**
     * This constructor should be used when using interceptor
     * without service registry (e.g. with CXF feature
     * configured via Spring, Blueprint or programmatically
     * @param policy
     */
    public SchemaValidationPolicyInInterceptor(SchemaValidationPolicy policy) {
          super(Phase.RECEIVE, policy);
    }

    @Override
    protected void validateBySettingProperty(Message message) {
        message.setContextualProperty(Message.SCHEMA_VALIDATION_ENABLED, SchemaValidationType.IN);
    }
}
