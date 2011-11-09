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

import org.apache.cxf.BusFactory;
import org.talend.esb.job.controller.GenericServiceProvider;
import org.talend.esb.job.controller.JobLauncher;
import org.talend.esb.job.controller.PolicyProvider;
import org.talend.esb.job.controller.ProviderFactory;

public class ProviderFactoryImpl implements ProviderFactory {

    private JobLauncher jobLauncher;
    private PolicyProvider policyProvider;

    public void setJobLauncher(JobLauncher jobLauncher) {
        this.jobLauncher = jobLauncher;
    }

    public void setPolicyProvider(PolicyProvider policyProvider) {
        this.policyProvider = policyProvider;
    }

    public GenericServiceProvider create(Map<String, String> operations) {
        policyProvider.register(BusFactory.getThreadDefaultBus());
        return new GenericServiceProviderImpl(
                jobLauncher, operations);
    }

}
