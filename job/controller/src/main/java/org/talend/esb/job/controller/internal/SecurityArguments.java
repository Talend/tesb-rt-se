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

import java.util.Map;

import org.apache.neethi.Policy;
import org.apache.wss4j.common.crypto.Crypto;
import org.talend.esb.job.controller.ESBEndpointConstants.EsbSecurity;

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

}
