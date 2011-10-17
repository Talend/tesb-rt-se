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

import org.talend.esb.job.controller.ESBEndpointConstants.EsbSecurity;

public class SecurityArguments {

    private final EsbSecurity esbSecurity;
    private final String policyLocation;
    private final String username;
    private final String password;
    private final Map<String, String> securityProperties;
    private final Map<String, String> STSProperties;

    public SecurityArguments(final EsbSecurity esbSecurity,
            String policyLocation,
            String username,
            String password,
            Map<String, String> securityProperties,
            Map<String, String> STSProperties) {
        this.esbSecurity = esbSecurity;
        this.policyLocation = policyLocation;
        this.username = username;
        this.password = password;
        this.securityProperties = securityProperties;
        this.STSProperties = STSProperties;
    }

    public EsbSecurity getEsbSecurity() {
        return esbSecurity;
    }

    public String getPolicyLocation() {
        return policyLocation;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

	public Map<String, String> getSecurityProperties() {
		return securityProperties;
	}

	public Map<String, String> getSTSProperties() {
		return STSProperties;
	}

}
