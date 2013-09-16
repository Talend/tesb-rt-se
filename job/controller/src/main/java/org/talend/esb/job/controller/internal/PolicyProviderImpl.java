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
import java.util.Map;

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

    private Map<String, String> policyProperties;

    private PolicyBuilder policyBuilder;

    @javax.annotation.Resource
    public void setBus(Bus bus) {
        policyBuilder = bus.getExtension(PolicyBuilder.class);
    }

    public void setPolicyProperties(Map<String, String> policyProperties) {
        this.policyProperties = policyProperties;
    }

    public Policy getUsernamePolicy() {
        return loadPolicy(policyProperties.get("policy.username"));
    }

    public Policy getSAMLPolicy() {
        return loadPolicy(policyProperties.get("policy.saml"));
    }

    public Policy getSAMLAuthzPolicy() {
        return loadPolicy(policyProperties.get("policy.saml.authz"));
    }

    public Policy getSAMLXkmsPolicy() {
        return loadPolicy(policyProperties.get("policy.saml.xkms"));
    }

    public Policy getSAMLAuthzXkmsPolicy() {
        return loadPolicy(policyProperties.get("policy.saml.authz.xkms"));
    }

    public void register(Bus cxf) {
        final PolicyRegistry policyRegistry =
                cxf.getExtension(PolicyEngine.class).getRegistry();
        policyRegistry.register(ESBEndpointConstants.ID_POLICY_USERNAME_TOKEN,
                getUsernamePolicy());
        policyRegistry.register(ESBEndpointConstants.ID_POLICY_SAML_TOKEN,
                getSAMLPolicy());
        policyRegistry.register(ESBEndpointConstants.ID_POLICY_SAML_AUTHZ,
                getSAMLAuthzPolicy());
        policyRegistry.register(ESBEndpointConstants.ID_POLICY_SAML_TOKEN_XKMS,
                getSAMLXkmsPolicy());
        policyRegistry.register(ESBEndpointConstants.ID_POLICY_SAML_AUTHZ_XKMS,
                getSAMLAuthzXkmsPolicy());
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
