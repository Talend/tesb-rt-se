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

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedService;
import org.talend.esb.job.controller.GenericOperation;
import org.talend.esb.job.controller.JobLauncher;

import routines.system.api.ESBEndpointRegistry;
import routines.system.api.TalendESBJob;
import routines.system.api.TalendESBRoute;
import routines.system.api.TalendJob;

public class JobLauncherImpl implements JobLauncher, JobListener {

    public static final Logger LOG = Logger.getLogger(JobLauncherImpl.class.getName());

    private BundleContext bundleContext;
    private ExecutorService executorService;
    private ESBEndpointRegistry endpointRegistry;

    private Map<String, JobTask> jobTasks = new ConcurrentHashMap<String, JobTask>();
    private Map<String, JobTask> routeTasks = new ConcurrentHashMap<String, JobTask>();
    private Map<String, TalendESBJob> esbJobs = new ConcurrentHashMap<String, TalendESBJob>();
    private Map<String, OperationTask> operationTasks = new ConcurrentHashMap<String, OperationTask>();
    private Map<String, ServiceRegistration> serviceRegistrations =
            new ConcurrentHashMap<String, ServiceRegistration>();

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public void setEndpointRegistry(ESBEndpointRegistry esbEndpointRegistry) {
        endpointRegistry = esbEndpointRegistry;
    }

    @Override
    public void esbJobAdded(TalendESBJob esbJob, String name) {
        LOG.info("Adding ESB job " + name + ".");
        esbJob.setEndpointRegistry(endpointRegistry);
        if (isConsumerOnly(esbJob)) {
            startJob(esbJob, name);
        } else {
            esbJobs.put(name, esbJob);
        }
    }

    @Override
    public void esbJobRemoved(TalendESBJob esbJob, String name) {
        LOG.info("Removing ESB job " + name + ".");
        if (isConsumerOnly(esbJob)) {
            stopJob(esbJob, name);
        } else {
            esbJobs.remove(name);
            OperationTask task = operationTasks.remove(name);
            if (task != null) {
                task.stop();
            }
        }
    }

    @Override
    public void routeAdded(TalendESBRoute route, String name) {
        LOG.info("Adding route " + name + ".");

        RouteAdapter adapter = new RouteAdapter(route, name);

        routeTasks.put(name, adapter);

        ServiceRegistration sr = bundleContext.registerService(
                ManagedService.class.getName(), adapter,
                getManagedServiceProperties(name));
        serviceRegistrations.put(name, sr);
        executorService.execute(adapter);
    }

    @Override
    public void routeRemoved(TalendESBRoute route, String name) {
        LOG.info("Removing route " + name + ".");

        JobTask routeTask = routeTasks.remove(name);
        if (routeTask != null) {
            routeTask.stop();
        }

        ServiceRegistration sr = serviceRegistrations.remove(name);
        if (sr != null) {
            sr.unregister();
        }
    }

    @Override
    public void jobAdded(TalendJob job, String name) {
        LOG.info("Adding job " + name + ".");

        startJob(job, name);
    }

    @Override
    public void jobRemoved(TalendJob job, String name) {
        LOG.info("Removing job " + name + ".");

        stopJob(job, name);
    }

    public void unbind() {
        esbJobs.clear();
        executorService.shutdownNow();
    }

    private void startJob(TalendJob job, String name) {
        SimpleJobTask jobTask = new SimpleJobTask(job, name);

        jobTasks.put(name, jobTask);

        ServiceRegistration sr = bundleContext.registerService(
                ManagedService.class.getName(), jobTask,
                getManagedServiceProperties(name));
        serviceRegistrations.put(name, sr);
        executorService.execute(jobTask);
    }

    private void stopJob(TalendJob job, String name) {
        JobTask jobTask = jobTasks.remove(name);
        if (jobTask != null) {
            jobTask.stop();
        }

        ServiceRegistration sr = serviceRegistrations.remove(name);
        if (sr != null) {
            sr.unregister();
        }
    }

    @Override
    public GenericOperation retrieveOperation(String jobName, String[] args) {
        OperationTask task = operationTasks.get(jobName);
        if (task == null) {
            TalendESBJob job = getJob(jobName);
            if (job == null) {
                throw new IllegalArgumentException("Talend job '" + jobName
                        + "' not found");
            }
            task = new OperationTask(job, args);
            operationTasks.put(jobName, task);
            executorService.execute(task);
        }
        return task;
    }

    private TalendESBJob getJob(String name) {
        TalendESBJob job = esbJobs.get(name);
        if (job == null) {
            throw new IllegalArgumentException("Talend ESB job with name "
                    + name + "' not found");
        }
        return (TalendESBJob) job;
    }

    private Dictionary<String, Object> getManagedServiceProperties(
            String routeName) {
        Dictionary<String, Object> result = new Hashtable<String, Object>();
        result.put(Constants.SERVICE_PID, routeName);
        return result;
    }

    private boolean isConsumerOnly(TalendESBJob esbJob) {
        return esbJob.getEndpoint() == null;
    }

}
