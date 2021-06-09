package org.talend.esb.sam.service.soap.security;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.endpoint.ServerRegistry;
import org.apache.cxf.interceptor.security.JAASLoginInterceptor;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.ws.security.SecurityConstants;
import org.apache.cxf.ws.policy.PolicyBuilder;
import org.apache.cxf.ws.policy.WSPolicyFeature;
import org.apache.neethi.Policy;
import org.apache.wss4j.dom.validate.JAASUsernameTokenValidator;

public class SAMServiceSecurityProvider {

    private EndpointImpl serviceEndpoint;
    private String authenticationType;
    private String policyUsernameToken;
    private String policySaml;
    private Object signatureProperties;
    private String signatureUsername;
    private String signaturePassword;

    public EndpointImpl getServiceEndpoint() {
        return serviceEndpoint;
    }

    public void setServiceEndpoint(EndpointImpl serviceEndpoint) {
        this.serviceEndpoint = serviceEndpoint;
    }

    public String getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(String authenticationType) {
        this.authenticationType = authenticationType;
    }

    public String getPolicyUsernameToken() {
        return policyUsernameToken;
    }

    public void setPolicyUsernameToken(String policyUsernameToken) {
        this.policyUsernameToken = policyUsernameToken;
    }

    public String getPolicySaml() {
        return policySaml;
    }

    public void setPolicySaml(String policySaml) {
        this.policySaml = policySaml;
    }

    public Object getSignatureProperties() {
        return signatureProperties;
    }

    public void setSignatureProperties(Object signatureProperties) {
        this.signatureProperties = signatureProperties;
    }

    public String getSignatureUsername() {
        return signatureUsername;
    }

    public void setSignatureUsername(String signatureUsername) {
        this.signatureUsername = signatureUsername;
    }

    public String getSignaturePassword() {
        return signaturePassword;
    }

    public void setSignaturePassword(String signaturePassword) {
        this.signaturePassword = signaturePassword;
    }

    @PostConstruct
    public void init() {

        final EsbSecurityConstants esbSecurity = EsbSecurityConstants.fromString(authenticationType);

        if (EsbSecurityConstants.NO == esbSecurity) {
            return;
        }

        Bus bus = serviceEndpoint.getBus();

        List<Policy> policies = new ArrayList<Policy>();
        WSPolicyFeature policyFeature = new WSPolicyFeature();
        policyFeature.setPolicies(policies);

        Map<String, Object> properties = serviceEndpoint.getProperties();
        if (null == properties) {
            properties = new HashMap<String, Object>();
        }

        if (EsbSecurityConstants.BASIC == esbSecurity) {
            JAASLoginInterceptor interceptor = new JAASLoginInterceptor();
            interceptor.setContextName("karaf");
            serviceEndpoint.getInInterceptors().add(interceptor);
        } else if (EsbSecurityConstants.USERNAMETOKEN == esbSecurity) {
            policies.add(loadPolicy(policyUsernameToken, bus));

            JAASUsernameTokenValidator jaasUTValidator = new JAASUsernameTokenValidator();
            jaasUTValidator.setContextName("karaf");
            properties.put(SecurityConstants.USERNAME_TOKEN_VALIDATOR, jaasUTValidator);
            serviceEndpoint.setProperties(properties);
        } else if (EsbSecurityConstants.SAML == esbSecurity) {
            policies.add(loadPolicy(policySaml, bus));

            properties.put(SecurityConstants.SIGNATURE_PROPERTIES, getSignatureProperties());
            properties.put(SecurityConstants.SIGNATURE_USERNAME, getSignatureUsername());
            properties.put(SecurityConstants.SIGNATURE_PASSWORD, getSignaturePassword());
            properties.put(SecurityConstants.CALLBACK_HANDLER, new WSPasswordCallbackHandler(
                    getSignatureUsername(), getSignaturePassword()));

            serviceEndpoint.setProperties(properties);

        }

        serviceEndpoint.getFeatures().add(policyFeature);

        ServerRegistry registry = bus.getExtension(ServerRegistry.class);
        List<Server> servers = registry.getServers();

        for (Server server : servers) {
            if (server.getEndpoint().getService() == serviceEndpoint.getService()) {
                policyFeature.initialize(server, bus);
            }
        }

    }

    private Policy loadPolicy(String location, Bus cxf) {
        InputStream is = null;
        try {
            is = new FileInputStream(location);
            return cxf.getExtension(PolicyBuilder.class).getPolicy(is);
        } catch (Exception e) {
            throw new RuntimeException("Cannot load policy", e);
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    // just ignore
                }
            }
        }
    }
}
