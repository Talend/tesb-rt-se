/*
 * #%L
 * Talend :: ESB :: Job :: Controller
 * %%
 * Copyright (c) 2006-2021 Talend Inc. - www.talend.com
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
import routines.system.api.TalendESBJobFactory;
import routines.system.api.TalendESBRoute;
import routines.system.api.TalendJob;

public class JobLauncherImpl implements JobLauncher, JobListener {

    public static final Logger LOG = Logger.getLogger(JobLauncherImpl.class.getName());

    private BundleContext bundleContext;
    private ExecutorService executorService;
    private ESBEndpointRegistry endpointRegistry;

    private Map<String, JobTask> jobTasks = new ConcurrentHashMap<String, JobTask>();
    private Map<String, JobTask> routeTasks = new ConcurrentHashMap<String, JobTask>();
    private Map<String, GenericOperation> operations = new ConcurrentHashMap<String, GenericOperation>();
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
    public void esbJobFactoryAdded(TalendESBJobFactory esbJobFactory, String name) {
        LOG.info("Adding ESB job factory for job " + name + ".");
        MultiThreadedOperation op =
            new MultiThreadedOperation(esbJobFactory, name, endpointRegistry, executorService);
        operations.put(name, op);

        // fix for TESB-7714
        if (esbJobFactory instanceof TalendESBJob) {
            ((TalendESBJob) esbJobFactory).setEndpointRegistry(endpointRegistry);
        }
    }

    @Override
    public void esbJobAdded(TalendESBJob esbJob, String name) {
        LOG.info("Adding ESB job " + name + ".");
        esbJob.setEndpointRegistry(endpointRegistry);
        if (isConsumerOnly(esbJob)) {
            startJob(esbJob, name);
        } else {
            SingleThreadedOperation op =
                    new SingleThreadedOperation(esbJob, name, endpointRegistry, executorService);
            operations.put(name, op);

        }
    }

    @Override
    public void esbJobRemoved(TalendESBJob esbJob, String name) {
        LOG.info("Removing ESB job " + name + ".");
        if (isConsumerOnly(esbJob)) {
            stopJob(esbJob, name);
        } else {
            GenericOperation task = operations.remove(name);
            if (task != null) {
                task.stop();
            }
        }
    }

    @Override
    public void esbJobFactoryRemoved(TalendESBJobFactory esbJobFactory, String name) {
        LOG.info("Removing ESB job factory for job " + name + ".");
        GenericOperation task = operations.remove(name);
        if (task != null) {
            task.stop();
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
    public synchronized GenericOperation retrieveOperation(String jobName, String[] args) {
        GenericOperation task = operations.get(jobName);
        if (task == null) {
            throw new IllegalArgumentException("Talend ESB job with name "
                    + jobName + "' not found");

        }
        task.start(args);
        return task;
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
