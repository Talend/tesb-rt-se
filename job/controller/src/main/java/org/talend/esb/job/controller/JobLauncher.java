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


import routines.system.api.TalendJob;

/**
 * Interface describing Talend job launcher behaviors.
 */
public interface JobLauncher {

    /**
     * Start a given Talend job with the given arguments.
     *
     * @param talendJob the Talend job.
     * @param args the Talend job run arguments.
     */
    void startJob(TalendJob talendJob, String[] args);

    /**
     * Start a Talend job with the given name and the given callback.
     *
     * @param name the Talend job name.
     * @param controller the RuntimeESBProviderCallbackController.
     */
//    public void startJob(String name,
//        final ESBProviderCallbackController controller);

    /**
     * Stop a given Talend job.
     *
     * @param talendJob the Talend job.
     */
    void stopJob(TalendJob talendJob);

    GenericOperation retrieveOperation(String jobName, boolean isRequestResponse,String[] args);
}
