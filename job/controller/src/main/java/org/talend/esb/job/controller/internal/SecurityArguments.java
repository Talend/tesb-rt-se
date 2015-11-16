/*
 * #%L
 * Talend :: ESB :: Job :: Controller
 * %%
 * Copyright (C) 2011 - 2012 Talend Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.talend.esb.job.controller.internal;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.cxf.Bus;
import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.transport.http.auth.HttpAuthHeader;
import org.apache.cxf.ws.security.SecurityConstants;
import org.apache.cxf.ws.security.trust.STSClient;
import org.apache.neethi.Policy;
import org.apache.wss4j.common.crypto.Crypto;
import org.talend.esb.job.controller.ESBEndpointConstants.EsbSecurity;
import org.talend.esb.security.saml.SAMLRESTUtils;
import org.talend.esb.security.saml.STSClientUtils;
import org.talend.esb.security.saml.WSPasswordCallbackHandler;

public class SecurityArguments {

    private final EsbSecurity esbSecurity;
    private final Policy policy;
    private final String username;
    private final String password;
    private final String alias;
    private final Map<String, String> clientProperties;
    private final String roleName;
    private final Object securityToken;
    private final Crypto cryptoProvider;

    public SecurityArguments(final EsbSecurity esbSecurity,
            final Policy policy,
            String username,
            String password,
            String alias,
            Map<String, String> clientProperties,
            String roleName,
            Object securityToken,
            Crypto cryptoProvider) {
        this.esbSecurity = esbSecurity;
        this.policy = policy;
        this.username = username;
        this.password = password;
        this.alias = alias;
        this.clientProperties = clientProperties;
        this.roleName = roleName;
        this.securityToken = securityToken;
        this.cryptoProvider = cryptoProvider;
    }

    public EsbSecurity getEsbSecurity() {
        return esbSecurity;
    }

    public Policy getPolicy() {
        return policy;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getAlias() {
        return alias;
    }

    public Map<String, String> getClientProperties() {
        return clientProperties;
    }

    public String getRoleName() {
        return roleName;
    }

    public Object getSecurityToken() {
        return securityToken;
    }

    public Crypto getCryptoProvider() {
        return cryptoProvider;
    }

    public AuthorizationPolicy buildAuthorizationPolicy() {
        AuthorizationPolicy authzPolicy = null;
        if (EsbSecurity.BASIC == esbSecurity) {
            authzPolicy = new AuthorizationPolicy();
            authzPolicy.setUserName(username);
            authzPolicy.setPassword(password);
            authzPolicy.setAuthorizationType(HttpAuthHeader.AUTH_TYPE_BASIC);
        } else if (EsbSecurity.DIGEST == esbSecurity) {
            authzPolicy = new AuthorizationPolicy();
            authzPolicy.setUserName(username);
            authzPolicy.setPassword(password);
            authzPolicy.setAuthorizationType(HttpAuthHeader.AUTH_TYPE_DIGEST);
        }
        return authzPolicy;
    }

    public Map<String, Object> buildClientConfig(final Bus bus, boolean useServiceRegistry, String encryptionUsername) {
        Map<String, Object> clientConfig = new HashMap<String, Object>();

        if (EsbSecurity.TOKEN == esbSecurity || useServiceRegistry) {
            clientConfig.put(SecurityConstants.USERNAME, username);
            clientConfig.put(SecurityConstants.PASSWORD, password);
        }

        if (EsbSecurity.SAML == esbSecurity || useServiceRegistry) {
            final STSClient stsClient = configureSTSClient(bus);
            clientConfig.put(SecurityConstants.STS_CLIENT, stsClient);

            for (Map.Entry<String, String> entry : clientProperties.entrySet()) {
                if (SecurityConstants.ALL_PROPERTIES.contains(entry.getKey())) {
                    clientConfig.put(entry.getKey(), processFileURI(entry.getValue()));
                }
            }
            if (null == alias) {
                String sigUser = clientProperties.get(SecurityConstants.SIGNATURE_USERNAME);
                if (sigUser == null) {
                    sigUser = clientProperties.get("ws-" + SecurityConstants.SIGNATURE_USERNAME);
                }
                clientConfig.put(SecurityConstants.CALLBACK_HANDLER,
                        new WSPasswordCallbackHandler(sigUser, clientProperties.get(SAMLRESTUtils.SIGNATURE_PASSWORD)));
            } else {
                clientConfig.put(SecurityConstants.SIGNATURE_USERNAME, alias);
                clientConfig.put(SecurityConstants.CALLBACK_HANDLER, new WSPasswordCallbackHandler(alias, password));
            }
            if (null != cryptoProvider) {
                clientConfig.put(SecurityConstants.ENCRYPT_CRYPTO, cryptoProvider);
                Object encryptUsername = clientConfig.get(SecurityConstants.ENCRYPT_USERNAME);
                if (encryptUsername == null) {
                    encryptUsername = clientProperties.get("ws-" + SecurityConstants.ENCRYPT_USERNAME);
                }
                if (encryptUsername == null || encryptUsername.toString().isEmpty()) {
                    clientConfig.put(SecurityConstants.ENCRYPT_USERNAME, encryptionUsername);
                }
            }
        }
        return clientConfig;
    }

    private STSClient configureSTSClient(final Bus bus) {
        final STSClient stsClient;
        if (null == alias) {
            stsClient = STSClientUtils.createSTSClient(bus, username, password);
        } else {
            stsClient = STSClientUtils.createSTSX509Client(bus, alias);
        }

        if (null != roleName && roleName.length() != 0) {
            STSClientUtils.applyAuthorization(stsClient, roleName);
        }
        if (null != securityToken) {
            stsClient.setOnBehalfOf(securityToken);
        }
        return stsClient;
    }

    private static Object processFileURI(String fileURI) {
        if (fileURI.startsWith("file:")) {
            try {
                return new URL(fileURI);
            } catch (MalformedURLException e) {
            }
        }
        return fileURI;
    }

}
