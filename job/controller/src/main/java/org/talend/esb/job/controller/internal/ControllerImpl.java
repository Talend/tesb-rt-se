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

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.talend.esb.job.controller.Controller;
import routines.system.api.TalendJob;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of Talend job controller.
 */
public class ControllerImpl implements Controller, ServiceListener {

    private BundleContext bundleContext;
    private TalendJobLauncher talendJobLauncher;

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
        this.bundleContext.addServiceListener(this);
    }

    public void setLauncher(TalendJobLauncher talendJobLauncher) {
        this.talendJobLauncher = talendJobLauncher;
    }

    public List<String> list() throws Exception {
        ArrayList<String> list = new ArrayList<String>();
        ServiceReference[] references = bundleContext.getServiceReferences(TalendJob.class.getName(), null);
        if (references != null) {
            for (ServiceReference reference:references) {
                if (reference != null) {
                    String name = (String) reference.getProperty("name");
                    if (name != null) {
                        list.add(name);
                    }
                }
            }
        }
        return list;
    }

    public void run(String name) throws Exception {
        this.run(name, new String[0]);
    }

    public void run(String name, final String[] args) throws Exception {
        ServiceReference[] references = bundleContext.getServiceReferences(TalendJob.class.getName(), "(name=" + name + ")");
        if (references == null) {
            throw new IllegalArgumentException("Talend job " + name + " not found");
        }
        final TalendJob job = (TalendJob) bundleContext.getService(references[0]);
        if (job != null) {
            talendJobLauncher.runTalendJob(job, args);
        }
    }

    @Override
    public void serviceChanged(ServiceEvent event) {
        if(event.getType() == ServiceEvent.UNREGISTERING){
            String type = (String)event.getServiceReference().getProperty("type");
            if(type != null && type.equalsIgnoreCase("job")) {
                TalendJob talendJob = (TalendJob)bundleContext.getService(event.getServiceReference());
                talendJobLauncher.stopTalendJob(talendJob);
            }
        }
    }

}
