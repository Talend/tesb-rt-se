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

        if (type.equals(SchemaValidationPolicy.ValidationType.WSDLSchema.getSymbolicName())) {
            policy.setValidationType(SchemaValidationPolicy.ValidationType.WSDLSchema);
        } else if (type.equals(SchemaValidationPolicy.ValidationType.CustomSchema.getSymbolicName())) {
            policy.setValidationType(SchemaValidationPolicy.ValidationType.CustomSchema);
        } else {
            throw new IllegalArgumentException("Validation type can be 'WSDLSchema' or 'CustomSchema' only");
        }
    }

    public void setAppliesTo(String appliesTo) {
        if (appliesTo == null) {
            throw new IllegalArgumentException("appliesTo cannot be null");
        }

        if (appliesTo.equals(SchemaValidationPolicy.AppliesToType.consumer.getSymbolicName())) {
            policy.setAppliesToType(SchemaValidationPolicy.AppliesToType.consumer);
        } else if (appliesTo.equals(SchemaValidationPolicy.AppliesToType.provider.getSymbolicName())) {
            policy.setAppliesToType(SchemaValidationPolicy.AppliesToType.provider);
        } else if (appliesTo.equals(SchemaValidationPolicy.AppliesToType.always.getSymbolicName())) {
            policy.setAppliesToType(SchemaValidationPolicy.AppliesToType.always);
        } else if (appliesTo.equals(SchemaValidationPolicy.AppliesToType.none.getSymbolicName())) {
            policy.setAppliesToType(SchemaValidationPolicy.AppliesToType.none);
        } else {
            throw new IllegalArgumentException("appliesTo can have 'consumer', 'provider', 'always' or 'none' value only");
        }
    }


    public void setMessage(String message) {
        if (message == null) {
            throw new IllegalArgumentException("message cannot be null");
        }

        if (message.equals(SchemaValidationPolicy.MessageType.request.getSymbolicName())) {
            policy.setMessageType(SchemaValidationPolicy.MessageType.request);
        } else if (message.equals(SchemaValidationPolicy.MessageType.response.getSymbolicName())) {
               policy.setMessageType(SchemaValidationPolicy.MessageType.response);
        } else if (message.equals(SchemaValidationPolicy.MessageType.all.getSymbolicName())) {
            policy.setMessageType(SchemaValidationPolicy.MessageType.all);
        } else if (message.equals(SchemaValidationPolicy.MessageType.none.getSymbolicName())) {
             policy.setMessageType(SchemaValidationPolicy.MessageType.none);
        } else {
            throw new IllegalArgumentException("message can have 'request', 'response', 'all' or 'none' value only");
        }
    }

    public void setSchemaPath(String schemaPath) {
        policy.setCustomSchemaPath(schemaPath);
    }
}
