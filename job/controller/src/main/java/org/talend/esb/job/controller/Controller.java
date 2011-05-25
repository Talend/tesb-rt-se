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

import java.util.List;

/**
 * Interface describing Talend job controller behaviors.
 */
public interface Controller {

    /**
     * List of Talend Jobs identified by name property available in the running container.
     *
     * @return the list of Talend Jobs identified by the name property.
     * @throws Exception in case of lookup failure.
     */
    public List<String> list() throws Exception;

    /**
     * Run a Talend job with the given name.
     *
     * @param name the Talend job name.
     * @throws Exception in case of run exception.
     */
    public void run(String name) throws Exception;

    /**
     * Run a Talend job with the given name and the given arguments.
     *
     * @param name the Talend job name.
     * @param args the Talend job run arguments.
     * @throws Exception in case of run exception.
     */
    public void run(String name, String[] args) throws Exception;

}
