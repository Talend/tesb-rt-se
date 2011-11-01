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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.cxf.Bus;
import org.apache.cxf.common.injection.NoJSR250Annotations;
import org.apache.cxf.ws.policy.PolicyBuilderImpl;
import org.apache.neethi.Policy;
import org.talend.esb.job.controller.PolicyProvider;

@NoJSR250Annotations(unlessNull = "bus") 
public class PolicyProviderImpl implements PolicyProvider {

    private String policyToken;
    private String policySaml;
    private PolicyBuilderImpl policyBuilder;

    public void setPolicyToken(String policyToken) {
        this.policyToken = policyToken;
    }

    public void setPolicySaml(String policySaml) {
        this.policySaml = policySaml;
    }

    @javax.annotation.Resource
    public void setBus(Bus bus) {
        policyBuilder = new PolicyBuilderImpl(bus);
    }

    public Policy getTokenPolicy() {
        return loadPolicy(policyToken);
    }

    public Policy getSamlPolicy() {
        return loadPolicy(policySaml);
    }

    private Policy loadPolicy(String location) {
        InputStream is = null;
        try {
            is = new FileInputStream(location);
            return policyBuilder.getPolicy(is);
        } catch (Exception e) {
            throw new RuntimeException("Cannot load policy");
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
