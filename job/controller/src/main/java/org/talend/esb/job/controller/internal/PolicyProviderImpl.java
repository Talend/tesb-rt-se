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
import java.util.Properties;

import org.apache.cxf.Bus;
import org.apache.cxf.ws.policy.PolicyBuilder;
import org.apache.cxf.ws.policy.PolicyEngine;
import org.apache.neethi.Policy;
import org.apache.neethi.PolicyRegistry;
import org.talend.esb.job.controller.ESBEndpointConstants;
import org.talend.esb.job.controller.PolicyProvider;

public class PolicyProviderImpl implements PolicyProvider {

    private Properties policyProperties;

    private Policy policyUsername;
    private Policy policySAML;
    private Policy policySAMLAuthz;
    private Policy policySAMLCrypto;
    private Policy policySAMLAuthzCrypto;

    public void setPolicyProperties(Properties policyProperties) {
        this.policyProperties = policyProperties;
    }

    public Policy getUsernamePolicy(Bus cxf) {
        if (null == policyUsername) {
            policyUsername = loadPolicy(policyProperties.getProperty("policy.username"), cxf);
        }
        return policyUsername;
    }

    public Policy getSAMLPolicy(Bus cxf) {
        if (null == policySAML) {
            policySAML = loadPolicy(policyProperties.getProperty("policy.saml"), cxf);
        }
        return policySAML;
    }

    public Policy getSAMLAuthzPolicy(Bus cxf) {
        if (null == policySAMLAuthz) {
            policySAMLAuthz = loadPolicy(policyProperties.getProperty("policy.saml.authz"), cxf);
        }
        return policySAMLAuthz;
    }

    public Policy getSAMLCryptoPolicy(Bus cxf) {
        if (null == policySAMLCrypto) {
            policySAMLCrypto = loadPolicy(policyProperties.getProperty("policy.saml.crypto"), cxf);
        }
        return policySAMLCrypto;
    }

    public Policy getSAMLAuthzCryptoPolicy(Bus cxf) {
        if (null == policySAMLAuthzCrypto) {
            policySAMLAuthzCrypto = loadPolicy(policyProperties.getProperty("policy.saml.authz.crypto"), cxf);
        }
        return policySAMLAuthzCrypto;
    }

    public void register(Bus cxf) {
        final PolicyRegistry policyRegistry =
                cxf.getExtension(PolicyEngine.class).getRegistry();
        policyRegistry.register(ESBEndpointConstants.ID_POLICY_USERNAME_TOKEN,
                getUsernamePolicy(cxf));
        policyRegistry.register(ESBEndpointConstants.ID_POLICY_SAML_TOKEN,
                getSAMLPolicy(cxf));
        policyRegistry.register(ESBEndpointConstants.ID_POLICY_SAML_AUTHZ,
                getSAMLAuthzPolicy(cxf));
        policyRegistry.register(ESBEndpointConstants.ID_POLICY_SAML_TOKEN_CRYPTO,
                getSAMLCryptoPolicy(cxf));
        policyRegistry.register(ESBEndpointConstants.ID_POLICY_SAML_AUTHZ_CRYPTO,
                getSAMLAuthzCryptoPolicy(cxf));
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
