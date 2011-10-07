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
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.cxf.Bus;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedService;
import org.talend.esb.job.controller.ESBEndpointConstants;
import org.talend.esb.job.controller.ESBEndpointConstants.OperationStyle;
import org.talend.esb.job.controller.GenericOperation;
import org.talend.esb.job.controller.JobLauncher;
import org.talend.esb.sam.agent.feature.EventFeature;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.servicelocator.cxf.LocatorFeature;

import routines.system.api.ESBConsumer;
import routines.system.api.ESBEndpointInfo;
import routines.system.api.ESBEndpointRegistry;

import routines.system.api.TalendESBRoute;
import routines.system.api.TalendESBJob;
import routines.system.api.TalendJob;

public class JobLauncherImpl implements JobLauncher, ESBEndpointRegistry, JobListener {

    private static final Logger LOG =
        Logger.getLogger(JobLauncherImpl.class.getName());

    private Queue<Event> samQueue;

    private Bus bus;
    
    private BundleContext bundleContext;
    
    private ExecutorService executorService;

    private ThreadLocal<RuntimeESBConsumer> tlsConsumer =
            new ThreadLocal<RuntimeESBConsumer>();

    private Map<String, JobTask> jobTasks = new ConcurrentHashMap<String, JobTask>(); 

    private Map<String, TalendESBJob> esbJobs = new ConcurrentHashMap<String, TalendESBJob>(); 

    private Map<String, OperationTask> operationTasks = new ConcurrentHashMap<String, OperationTask>();

    private Map<String, ServiceRegistration> serviceRegistrations = new ConcurrentHashMap<String, ServiceRegistration>();

    public void setBus(Bus bus) {
        this.bus = bus;
    }
    
    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public void setSamQueue(Queue<Event> samQueue) {
        this.samQueue = samQueue;
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Deprecated
    public void startJob(final TalendJob talendJob, final String[] args) {
        startJob(new ESBJobThread(talendJob, args, this));
    }

    @Deprecated
    private void startJob(final Thread thread) {
        thread.setContextClassLoader(this.getClass().getClassLoader());
        thread.start();
    }

    @Deprecated
    public void stopJob(final TalendJob talendJob) {
    }
    
    @Override
    public void esbJobAdded(TalendESBJob esbJob, String name) {
        LOG.info("Adding ESB job " +  name + ".");
        esbJobs.put(name, esbJob);   
    }

    @Override
    public void esbJobRemoved(TalendESBJob esbJob, String name) {
        LOG.info("Removing ESB job " +  name + ".");
        esbJobs.remove(name);
        OperationTask task = operationTasks.remove(name);
        if (task != null) {
            task.stop();
        }
    }

    @Override
    public void routeAdded(TalendESBRoute route, String name) {
        LOG.info("Adding route " +  name + ".");

        RouteAdapter adapter = new RouteAdapter(route, name);
        
        jobTasks.put(name, adapter);
        
        ServiceRegistration sr = 
            bundleContext.registerService(ManagedService.class.getName(),
                adapter,
                getManagedServiceProperties(name));
        serviceRegistrations.put(name, sr);
        executorService.execute(adapter);
    }

    @Override
    public void routeRemoved(TalendESBRoute route, String name) {
        LOG.info("Removing route " +  name + ".");

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
    public void jobAdded(TalendJob job, String name) {
        LOG.info("Adding job " +  name + ".");
        
        SimpleJobTask jobTask = new SimpleJobTask(job, name);

        jobTasks.put(name, jobTask);

        ServiceRegistration sr = 
            bundleContext.registerService(ManagedService.class.getName(),
                jobTask,
                getManagedServiceProperties(name));
        serviceRegistrations.put(name, sr);
        executorService.execute(jobTask);
    }

    @Override
    public void jobRemoved(TalendJob job, String name) {
        JobTask jobTask = jobTasks.remove(name);
        if (jobTask != null) {
            jobTask.stop();
        }

        ServiceRegistration sr = serviceRegistrations.remove(name);
        if (sr != null) {
            sr.unregister();
        }
    }

    public void unbind() {
        esbJobs.clear();
        
        for(JobTask jobTask : jobTasks.values()) {
            jobTask.stop();
        }
        
        for(ServiceRegistration sr : serviceRegistrations.values()) {
            sr.unregister();
        }
        
        for(OperationTask operation : operationTasks.values()) {
            operation.stop();
        }
        executorService.shutdownNow();    
    }

    @Override
    public ESBConsumer createConsumer(ESBEndpointInfo endpoint) {
        final Map<String, Object> props = endpoint.getEndpointProperties();

        final QName serviceName = QName.valueOf((String)props.get(ESBEndpointConstants.SERVICE_NAME));
        final QName portName = QName.valueOf((String)props.get(ESBEndpointConstants.PORT_NAME));
        final String operationName = (String)props.get(ESBEndpointConstants.DEFAULT_OPERATION_NAME);

        ESBConsumer esbConsumer = null;
		/*
		 * commenting out this code coz of issue https://jira.sopera.de/browse/TESB-2074
		 * If we get the consumer in the following way, SAM featuer is not set for the consumer
		 * hence the consumer doesnt send out SAM events.
		Collection<ESBProvider> esbProviders = endpoints.get(
				new ESBProviderKey(serviceName, portName));
		if(esbProviders != null) {
			for(ESBProvider provider : esbProviders) {
				esbConsumer = provider.getESBProviderCallback(operationName);
				if(esbConsumer != null) {
					break;
				}
			}
		}

		// create generic consumer
		if(esbConsumer == null) {
		*/
            final String publishedEndpointUrl = (String)props.get(ESBEndpointConstants.PUBLISHED_ENDPOINT_URL);
            boolean useServiceLocator =
                ((Boolean)props.get(ESBEndpointConstants.USE_SERVICE_LOCATOR)).booleanValue();
            boolean useServiceActivityMonitor =
                ((Boolean)props.get(ESBEndpointConstants.USE_SERVICE_ACTIVITY_MONITOR)).booleanValue();
            final RuntimeESBConsumer runtimeESBConsumer = new RuntimeESBConsumer(
                serviceName,
                portName,
                operationName,
                publishedEndpointUrl,
                OperationStyle.isRequestResponse((String)props.get(ESBEndpointConstants.COMMUNICATION_STYLE)),
                useServiceLocator ? new LocatorFeature() : null,
                useServiceActivityMonitor ? createEventFeature() : null,
                bus);

            tlsConsumer.set(runtimeESBConsumer);
            esbConsumer = runtimeESBConsumer;
		//}
        return esbConsumer;
    }

    private EventFeature createEventFeature() {
        EventFeature eventFeature = new EventFeature();
        eventFeature.setQueue(samQueue);
        return eventFeature;
    }

    @Override
    public GenericOperation retrieveOperation(String jobName, String[] args) {
        OperationTask task = operationTasks.get(jobName);
        if (task == null) {
            TalendESBJob job = getJob(jobName);
            if (job == null) {
                throw new IllegalArgumentException("Talend job '" + jobName + "' not found");
            }
            task = new OperationTask(job, args, this);
            operationTasks.put(jobName, task);
            executorService.execute(task);
        }
        return task;
    }
    
    private TalendESBJob getJob(String name) {
        TalendESBJob job = esbJobs.get(name);
        if (job == null ) {
            throw new IllegalArgumentException("Talend ESB job with name " + name + "' not found");
        }
        return (TalendESBJob) job;
    }

    private Dictionary<String, Object> getManagedServiceProperties(String routeName) {
        Dictionary<String, Object> result = new Hashtable<String, Object>();
        result.put(Constants.SERVICE_PID, routeName);
        return result;
    }

}
