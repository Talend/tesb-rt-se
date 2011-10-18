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
package org.talend.esb.job.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.cxf.ws.policy.PolicyBuilderImpl;
import org.apache.neethi.Policy;

public class PolicyProvider {

    private static String policyToken;

    private static String policySaml;

    public void setPolicyToken(String policyToken) {
        PolicyProvider.policyToken = policyToken;
    }

    public void setPolicySaml(String policySaml) {
        PolicyProvider.policySaml = policySaml;
    }

    public static Policy getTokenPolicy() {
        return loadPolicy(policyToken);
    }

    public static Policy getSamlPolicy() {
        return loadPolicy(policySaml);
    }

    private static Policy loadPolicy(String location) {
        InputStream is = null;
        try {
            is = new FileInputStream(location);
            PolicyBuilderImpl policyBuilder = new PolicyBuilderImpl();
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
