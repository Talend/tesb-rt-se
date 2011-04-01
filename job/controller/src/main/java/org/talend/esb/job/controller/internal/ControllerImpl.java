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

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.esb.job.controller.Controller;
import routines.TalendJob;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of Talend job controller.
 */
public class ControllerImpl implements Controller {

    private static Logger LOGGER = LoggerFactory.getLogger(ControllerImpl.class);

    private BundleContext bundleContext;

    private List<TalendJob> services;

    public List<Bundle> list() throws Exception {
        LOGGER.debug("Looking for Talend job with META-INF/job.properties");
        Bundle[] bundles = bundleContext.getBundles();
        ArrayList<Bundle> jobBundles = new ArrayList<Bundle>();
        for (int i = 0; i < bundles.length; i++) {
            if (bundles[i].getEntry("META-INF/job.properties") != null) {
                jobBundles.add(bundles[i]);
            }
        }
        return jobBundles;
    }

    public List<TalendJob> listServices() throws Exception {
        return services;
    }

    public void start(Bundle job) throws Exception {
        // TODO
    }

    public void stop(Bundle job) throws Exception {
        // TODO
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

}
