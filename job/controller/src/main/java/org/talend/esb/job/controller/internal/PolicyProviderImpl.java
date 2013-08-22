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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.cxf.Bus;
import org.apache.cxf.common.injection.NoJSR250Annotations;
import org.apache.cxf.ws.policy.PolicyBuilder;
import org.apache.cxf.ws.policy.PolicyEngine;
import org.apache.neethi.Policy;
import org.apache.neethi.PolicyRegistry;
import org.talend.esb.job.controller.ESBEndpointConstants;
import org.talend.esb.job.controller.PolicyProvider;

@NoJSR250Annotations(unlessNull = "bus") 
public class PolicyProviderImpl implements PolicyProvider {

    private String policyToken;
    private String policySaml;
    private String policySamlAuthzService;
    private String policySamlAuthzClient;
    private String policyCorrelationId;
    private String policySamEnable;
    private PolicyBuilder policyBuilder;

    public void setPolicyToken(String policyToken) {
        this.policyToken = policyToken;
    }

    public void setPolicySaml(String policySaml) {
        this.policySaml = policySaml;
    }

    public void setPolicySamlAuthzService(String policySamlAuthzService) {
        this.policySamlAuthzService = policySamlAuthzService;
    }

    public void setPolicySamlAuthzClient(String policySamlAuthzClient) {
        this.policySamlAuthzClient = policySamlAuthzClient;
    }

	public void setPolicyCorrelationId(String policyCorrelationId) {
		this.policyCorrelationId = policyCorrelationId;
	}
	
    public void setPolicySamEnable(String policySamEnable) {
        this.policySamEnable = policySamEnable;
    }

	@javax.annotation.Resource
    public void setBus(Bus bus) {
        policyBuilder = bus.getExtension(PolicyBuilder.class);
    }

    public Policy getTokenPolicy() {
        return loadPolicy(policyToken);
    }

    public Policy getSamlPolicy() {
        return loadPolicy(policySaml);
    }

    public Policy getSamlAuthzServicePolicy() {
        return loadPolicy(policySamlAuthzService);
    }

    public Policy getSamlAuthzClientPolicy() {
        return loadPolicy(policySamlAuthzClient);
    }

    private Policy getCorrelationIdPolicy() {
		return loadPolicy(policyCorrelationId);
	}
    
    private Policy getSamenablePolicy() {
        return loadPolicy(policySamEnable);
    }
    
    public void register(Bus cxf) {
        final PolicyRegistry policyRegistry =
                cxf.getExtension(PolicyEngine.class).getRegistry();
        policyRegistry.register(ESBEndpointConstants.ID_POLICY_TOKEN,
                getTokenPolicy());
        policyRegistry.register(ESBEndpointConstants.ID_POLICY_SAML,
                getSamlPolicy());
        policyRegistry.register(ESBEndpointConstants.ID_POLICY_SAML_AUTHZ_SERVICE,
                getSamlAuthzServicePolicy());
        policyRegistry.register(ESBEndpointConstants.ID_POLICY_SAML_AUTHZ_CLIENT,
                getSamlAuthzClientPolicy());
        policyRegistry.register(ESBEndpointConstants.ID_POLICY_CORRELATION_ID,
        		getCorrelationIdPolicy());
        policyRegistry.register(ESBEndpointConstants.ID_POLICY_SAMENABLE,
                getSamenablePolicy());
    }

    private Policy loadPolicy(String location) {
        InputStream is = null;
        try {
            is = new FileInputStream(location);
            return policyBuilder.getPolicy(is);
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
