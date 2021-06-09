package org.talend.esb.sam.agent.serviceclient;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.xml.namespace.QName;

import org.apache.cxf.Bus;
import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transport.http.auth.HttpAuthHeader;
import org.apache.cxf.ws.security.SecurityConstants;
import org.apache.cxf.ws.security.trust.STSClient;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.cxf.ws.policy.PolicyBuilder;
import org.apache.cxf.ws.policy.WSPolicyFeature;
import org.apache.neethi.Policy;
import org.apache.wss4j.common.ConfigurationConstants;
import org.apache.wss4j.common.WSS4JConstants;
import org.apache.wss4j.common.ext.WSPasswordCallback;

public class SAMClientSecurityProvider {

    private Client client;
    private String authenticationType;
    private String policyUsernameToken;
    private String policySaml;
    private Object signatureProperties;
    private String signatureUsername;
    private String signaturePassword;
    private String username;
    private String password;

    private String stsWsdlLocation;
    private String stsNamespace;
    private String stsServiceName;
    private String stsEndpointName;
    private String stsTokenUsecert;
    private String encryptionUsername;
    private String isBspCompliant;

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStsWsdlLocation() {
        return stsWsdlLocation;
    }

    public void setStsWsdlLocation(String stsWsdlLocation) {
        this.stsWsdlLocation = stsWsdlLocation;
    }

    public String getStsNamespace() {
        return stsNamespace;
    }

    public void setStsNamespace(String stsNamespace) {
        this.stsNamespace = stsNamespace;
    }

    public String getStsServiceName() {
        return stsServiceName;
    }

    public void setStsServiceName(String stsServiceName) {
        this.stsServiceName = stsServiceName;
    }

    public String getStsEndpointName() {
        return stsEndpointName;
    }

    public void setStsEndpointName(String stsEndpointName) {
        this.stsEndpointName = stsEndpointName;
    }

    public String getStsTokenUsecert() {
        return stsTokenUsecert;
    }

    public void setStsTokenUsecert(String stsTokenUsecert) {
        this.stsTokenUsecert = stsTokenUsecert;
    }

    public String getEncryptionUsername() {
        return encryptionUsername;
    }

    public void setEncryptionUsername(String encryptionUsername) {
        this.encryptionUsername = encryptionUsername;
    }

    public String getIsBspCompliant() {
        return isBspCompliant;
    }

    public void setIsBspCompliant(String isBspCompliant) {
        this.isBspCompliant = isBspCompliant;
    }

    @PostConstruct
    public void init() {

        final EsbSecurityConstants esbSecurity = EsbSecurityConstants.fromString(authenticationType);

        if (EsbSecurityConstants.NO == esbSecurity) {
            return;
        }

        Bus bus = client.getBus();

        List<Policy> policies = new ArrayList<Policy>();
        WSPolicyFeature policyFeature = new WSPolicyFeature();
        policyFeature.setPolicies(policies);

        Map<String, Object> properties = client.getRequestContext();
        if (null == properties) {
            properties = new HashMap<String, Object>();
        }

        if (EsbSecurityConstants.BASIC == esbSecurity) {
            AuthorizationPolicy authzPolicy = new AuthorizationPolicy();
            authzPolicy.setUserName(username);
            authzPolicy.setPassword(password);
            authzPolicy.setAuthorizationType(HttpAuthHeader.AUTH_TYPE_BASIC);
            HTTPConduit conduit = (HTTPConduit)client.getConduit();
            conduit.setAuthorization(authzPolicy);
        } else if (EsbSecurityConstants.USERNAMETOKEN == esbSecurity) {
            policies.add(loadPolicy(policyUsernameToken, bus));

            java.util.Map<String, Object> wssProps = new java.util.HashMap<String, Object>();
            wssProps.put(ConfigurationConstants.ACTION, ConfigurationConstants.USERNAME_TOKEN);
            wssProps.put(ConfigurationConstants.USER,username);
            wssProps.put(ConfigurationConstants.PASSWORD_TYPE, WSS4JConstants.PW_TEXT);
            wssProps.put(ConfigurationConstants.PW_CALLBACK_REF, new CallbackHandler() {
                public void handle (Callback[] callbacks) throws IOException, UnsupportedCallbackException {
                    ((WSPasswordCallback)callbacks[0]).setPassword(password);
                }
            });
            client.getEndpoint().getOutInterceptors().add(new WSS4JOutInterceptor(wssProps));
            client.getRequestContext().put("security.username", username);
            client.getRequestContext().put("security.password", password);

        } else if (EsbSecurityConstants.SAML == esbSecurity) {
            policies.add(loadPolicy(policySaml, bus));

            properties.put(SecurityConstants.SIGNATURE_PROPERTIES, processFileURI(getSignatureProperties()));
            properties.put(SecurityConstants.SIGNATURE_USERNAME, getSignatureUsername());
            properties.put(SecurityConstants.SIGNATURE_PASSWORD, getSignaturePassword());
            properties.put(SecurityConstants.CALLBACK_HANDLER, 
                    new WSPasswordCallbackHandler(getSignatureUsername(), getSignaturePassword()));

            // STS client
            STSClient stsClient = new STSClient(bus);
            stsClient.setWsdlLocation(stsWsdlLocation);
            stsClient.setServiceQName(new QName(stsNamespace, stsServiceName));
            stsClient.setEndpointQName(new QName(stsNamespace, stsEndpointName));
            Map<String, Object> stsProperties = new HashMap<String, Object>();
            stsProperties.put(SecurityConstants.USERNAME, username);
            stsProperties.put(SecurityConstants.PASSWORD, password);
            stsProperties.put(SecurityConstants.CALLBACK_HANDLER,
                    new WSPasswordCallbackHandler(username, password));
            stsProperties.put(SecurityConstants.STS_TOKEN_PROPERTIES, processFileURI(getSignatureProperties()));
            stsProperties.put(SecurityConstants.STS_TOKEN_USERNAME, signatureUsername);
            stsProperties.put(SecurityConstants.STS_TOKEN_USE_CERT_FOR_KEYINFO, stsTokenUsecert);
            stsProperties.put(SecurityConstants.ENCRYPT_PROPERTIES, processFileURI(getSignatureProperties()));
            stsProperties.put(SecurityConstants.ENCRYPT_USERNAME, encryptionUsername);
            stsProperties.put(SecurityConstants.IS_BSP_COMPLIANT, isBspCompliant);

            stsClient.setProperties(stsProperties);
            properties.put(SecurityConstants.STS_CLIENT, stsClient);

        }

        client.getEndpoint().getActiveFeatures().add(policyFeature);
        policyFeature.initialize(client, bus);

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

    private static Object processFileURI(Object fileURI) {
        if (fileURI instanceof String) {
            String fileURIName = (String) fileURI;
            if (fileURIName.startsWith("file:")) {
                try {
                    return new URL(fileURIName);
                } catch (MalformedURLException e) {
                    // assume file path name
                }
            }
        }
        return fileURI;
    }

}

