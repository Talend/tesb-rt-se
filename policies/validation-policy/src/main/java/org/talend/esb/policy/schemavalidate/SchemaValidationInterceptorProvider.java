package org.talend.esb.policy.schemavalidate;

import java.util.Arrays;

import org.apache.cxf.ws.policy.AbstractPolicyInterceptorProvider;
import org.talend.esb.policy.schemavalidate.interceptors.SchemaValidationPolicyInInterceptor;
import org.talend.esb.policy.schemavalidate.interceptors.SchemaValidationPolicyOutInterceptor;


public class SchemaValidationInterceptorProvider extends AbstractPolicyInterceptorProvider {

    private static final long serialVersionUID = 4222227474541786883L;

    public SchemaValidationInterceptorProvider() {
        super(Arrays.asList(SchemaValidationPolicyBuilder.SCHEMA_VALIDATION));

        this.getOutInterceptors().add(new SchemaValidationPolicyOutInterceptor());
        this.getOutFaultInterceptors().add(new SchemaValidationPolicyOutInterceptor());
        this.getInInterceptors().add(new SchemaValidationPolicyInInterceptor());
        this.getInFaultInterceptors().add(new SchemaValidationPolicyInInterceptor());
    }
}
