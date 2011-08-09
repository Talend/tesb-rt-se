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

import java.util.logging.Logger;

import org.talend.esb.job.controller.ESBProviderCallbackController;

import routines.system.api.ESBEndpointInfo;
import routines.system.api.ESBEndpointRegistry;
import routines.system.api.ESBProviderCallback;
import routines.system.api.TalendESBJob;
import routines.system.api.TalendJob;

class ESBJobThread extends Thread {

    private static final Logger LOG =
        Logger.getLogger(ESBJobThread.class.getName());

    private final TalendJob talendJob;
    private final String[] args;
    private final ESBProviderCallbackController controller;
    private final ESBEndpointRegistry esbEndpointRegistry;
    private final JobThreadListener jobThreadListener;

    public ESBJobThread(
            final TalendJob talendJob,
            final String[] args,
            final ESBProviderCallbackController controller,
            final JobThreadListener jobThreadListener,
            final ESBEndpointRegistry esbEndpointRegistry) {
        this.talendJob = talendJob;
        this.args = args;
        this.controller = controller;
        this.esbEndpointRegistry = esbEndpointRegistry;
        this.jobThreadListener = jobThreadListener;
    }

    @Override
    public void run() {
        try {
            if (talendJob instanceof TalendESBJob) {
                // We have an ESB Job;
                final TalendESBJob talendESBJob = (TalendESBJob) talendJob;
                // get provider endpoint information
                final ESBEndpointInfo esbEndpointInfo =
                    talendESBJob.getEndpoint();
                if (null != esbEndpointInfo) {
                    ESBProviderCallback esbProviderCallback = 
                        controller.createESBProviderCallback(
                            esbEndpointInfo);
                    talendESBJob.setProviderCallback(esbProviderCallback);
                } else if (controller.isRequired()) {
                    throw new IllegalArgumentException("Provider job expected");
                }
                talendESBJob.setEndpointRegistry(esbEndpointRegistry);
            }

            LOG.info("Talend Job starting...");
            jobThreadListener.jobStarted(talendJob, this);
            int ret = talendJob.runJobInTOS(args);
            LOG.info("Talend Job finished with code " + ret);

        } finally {
            jobThreadListener.jobFinished(talendJob, this);

            controller.destroyESBProviderCallback();
        }
    }
}