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

import org.osgi.framework.Bundle;

import java.util.List;
import java.util.Map;

/**
 * Interface describing Talend job controller behaviors.
 */
public interface Controller {

    /**
     * List of Talend jobs and routes available in the running container.
     *
     * @return the list of Talend jobs and routes.
     * @throws Exception in case of lookup failure.
     */
    public Map<String, List<String>> list() throws Exception;

    /**
     * List of Talend jobs available in the running container.
     *
     * @return the list of Talend jobs.
     * @throws Exception in case of lookup failure.
     */
    public List<String> listJobs() throws Exception;

    /**
     * List of Talend routes available in the running container.
     *
     * @return the list of Talend routes
     * @throws Exception in case of lookup failure
     */
    public List<String> listRoutes() throws Exception;

    /**
     * List of Talend Data Services available in the running container.
     *
     * @return the list of Talend services
     * @throws Exception in case of lookup failure
     */
    public List<String> listServices() throws Exception;

    /**
     * Get the bundle corresponding to the job/route name.
     */
    public Bundle getBundle(String name) throws Exception;

    /**
     * Run a Talend job with the given name.
     *
     * @param name the Talend job name.
     * @throws Exception in case of run exception.
     */
//    public void run(String name) throws Exception;

    /**
     * Run a Talend job with the given name and the given arguments.
     *
     * @param name the Talend job name.
     * @param args the Talend job run arguments.
     * @throws Exception in case of run exception.
     */
//    public void run(String name, String[] args) throws Exception;

    /**
     * Run a Talend Data Service with the given name.
     *
     * @param name the Talend service name.
     * @throws Exception in case of run exception.
     */
/*
    public void runService(String name) throws Exception;
*/
    /**
     * Stop a Talend job with the given name.
     *
     * @param name the Talend job name.
     * @throws Exception in case of run exception.
     */
//    public void stop(String name) throws Exception;

    /**
     * Stop a Talend Data Service with the given name.
     *
     * @param name the Talend service name.
     * @throws Exception in case of run exception.
     */
/*
    public void stopService(String name) throws Exception;
*/
}
