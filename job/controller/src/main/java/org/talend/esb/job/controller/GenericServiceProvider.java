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

import java.util.Dictionary;
import java.util.Map;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.talend.esb.job.controller.internal.Configuration;
import org.talend.esb.job.controller.internal.ESBProviderBase;

public class GenericServiceProvider extends ESBProviderBase
        implements ManagedService {

    private Map<String, String> operations;

    private JobLauncher jobLauncher;

    private Configuration configuration;

    public void setOperations(Map<String, String> operations) {
        this.operations = operations;
    }

    public void setJobLauncher(JobLauncher jobLauncher) {
        this.jobLauncher = jobLauncher;
    }

    @Override
    public GenericOperation getESBProviderCallback(String operationName) {
        final String jobName = operations.get(operationName);
        if (jobName == null) {
            throw new IllegalArgumentException(
                    "Job for operation '" + operationName + "' not found");
        }

        final GenericOperation operation = jobLauncher.retrieveOperation(
            jobName, configuration.getArguments());

        return operation;
    }

    @Override
    public void updated(@SuppressWarnings("rawtypes") Dictionary properties)
            throws ConfigurationException {
        configuration = new Configuration(properties);
    }

}
