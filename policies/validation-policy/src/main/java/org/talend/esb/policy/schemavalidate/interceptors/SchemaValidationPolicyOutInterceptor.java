package org.talend.esb.policy.schemavalidate.interceptors;


import org.apache.cxf.annotations.SchemaValidation.SchemaValidationType;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.talend.esb.policy.schemavalidate.SchemaValidationPolicy;

public class SchemaValidationPolicyOutInterceptor extends SchemaValidationPolicyAbstractInterceptor {

    /**
     * This constructor should be used when using interceptor
     * with service registry (validation activated via policies)
     */
    public SchemaValidationPolicyOutInterceptor() {
        //use Phase.SETUP in case of using validateBySettingProperty()
        super(Phase.SETUP);
        //super(Phase.MARSHAL);
    }

    /**
     * This constructor should be used when using interceptor
     * without service registry (e.g. with CXF feature
     * configured via Spring, Blueprint or programmatically
     * @param policy
     */
    public SchemaValidationPolicyOutInterceptor(SchemaValidationPolicy policy) {
        super(Phase.SETUP, policy);
    }

    @Override
    protected void validateBySettingProperty(Message message) {
        message.put(Message.SCHEMA_VALIDATION_ENABLED, SchemaValidationType.OUT);
    }
}
