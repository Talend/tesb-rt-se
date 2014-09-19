package org.talend.esb.policy.schemavalidate.feature;

import org.apache.cxf.Bus;
import org.apache.cxf.common.injection.NoJSR250Annotations;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.interceptor.InterceptorProvider;
import org.talend.esb.policy.schemavalidate.SchemaValidationPolicy;
import org.talend.esb.policy.schemavalidate.interceptors.SchemaValidationPolicyInInterceptor;
import org.talend.esb.policy.schemavalidate.interceptors.SchemaValidationPolicyOutInterceptor;


@NoJSR250Annotations
public class SchemaValidationFeature extends AbstractFeature {


    private final SchemaValidationPolicy policy =  new SchemaValidationPolicy();

    @Override
    protected void initializeProvider(InterceptorProvider provider, Bus bus) {
        provider.getOutInterceptors().add(new SchemaValidationPolicyOutInterceptor(policy));
        provider.getInInterceptors().add(new SchemaValidationPolicyInInterceptor(policy));
    }

    public void setType(String type) {
        if (type == null) {
            throw new IllegalArgumentException("Validation type cannot be null");
        }

        try {
            policy.setValidationType(SchemaValidationPolicy.ValidationType.valueOf(type));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Validation type can be 'WSDLSchema' or 'CustomSchema' only");
        }
    }

    public void setAppliesTo(String appliesTo) {
        if (appliesTo == null) {
            throw new IllegalArgumentException("appliesTo cannot be null");
        }

        try {
            policy.setAppliesToType(SchemaValidationPolicy.AppliesToType.valueOf(appliesTo));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("appliesTo can have 'consumer', 'provider', 'always' or 'none' value only");
        }
    }


    public void setMessage(String message) {
        if (message == null) {
            throw new IllegalArgumentException("message cannot be null");
        }

        try {
            policy.setMessageType(SchemaValidationPolicy.MessageType.valueOf(message));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("message can have 'request', 'response', 'all' or 'none' value only");
        }
    }

    public void setPath(String path) {
        policy.setCustomSchemaPath(path);
    }
}
