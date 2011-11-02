/*
 * #%L
 * Talend :: ESB :: Job :: Controller
 * %%
 * Copyright (C) 2011 Talend Inc.
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
import org.talend.esb.job.controller.ESBEndpointConstants.EsbSecurity;

public class SecurityArguments {

    private final EsbSecurity esbSecurity;
    private final Policy policy;
    private final String username;
    private final String password;
    private final Map<String, String> clientProperties;
    private final Map<String, String> stsProperties;

    public SecurityArguments(final EsbSecurity esbSecurity,
            final Policy policy,
            String username,
            String password,
            Map<String, String> clientProperties,
            Map<String, String> stsProperties) {
        this.esbSecurity = esbSecurity;
        this.policy = policy;
        this.username = username;
        this.password = password;
        this.clientProperties = clientProperties;
        this.stsProperties = stsProperties;
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

    public Map<String, String> getClientProperties() {
        return clientProperties;
    }

    public Map<String, String> getStsProperties() {
        return stsProperties;
    }

}
