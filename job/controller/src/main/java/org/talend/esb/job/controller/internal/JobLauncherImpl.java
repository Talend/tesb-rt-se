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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.namespace.QName;

import org.apache.cxf.Bus;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.talend.esb.job.controller.ESBEndpointConstants;
import org.talend.esb.job.controller.ESBEndpointConstants.OperationStyle;
import org.talend.esb.job.controller.ESBProviderCallbackController;
import org.talend.esb.job.controller.JobLauncher;
import org.talend.esb.sam.agent.feature.EventFeature;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.servicelocator.cxf.LocatorFeature;

import routines.system.api.ESBConsumer;
import routines.system.api.ESBEndpointInfo;
import routines.system.api.ESBEndpointRegistry;
import routines.system.api.ESBJobInterruptedException;
import routines.system.api.ESBProviderCallback;
import routines.system.api.TalendJob;

public class JobLauncherImpl implements JobLauncher, ESBEndpointRegistry,
        JobThreadListener {

    private Queue<Event> samQueue;
    private Bus bus;
    private BundleContext bundleContext;

    private final Map<ESBProviderKey, Collection<ESBProvider> > endpoints =
            new ConcurrentHashMap<ESBProviderKey, Collection<ESBProvider>>();
    private final Map<TalendJob, Thread > jobs =
            new ConcurrentHashMap<TalendJob, Thread>();
    private ThreadLocal<RuntimeESBConsumer> tlsConsumer =
            new ThreadLocal<RuntimeESBConsumer>();

    public void setBus(Bus bus) {
        this.bus = bus;
    }

    public void setSamQueue(Queue<Event> samQueue) {
        this.samQueue = samQueue;
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public void startJob(final TalendJob talendJob, final String[] args) {
        final ESBProviderCallbackController controller =
            new LazyESBProviderCallbackController();
        startJob(new ESBJobThread(talendJob, args, controller, this, this));
    }

    public void startJob(String name,
        final ESBProviderCallbackController controller) {
        // ControllerImpl
        ServiceReference[] references;
        try {
            references = bundleContext.getServiceReferences(
                TalendJob.class.getName(), "(name=" + name + ")");
        } catch (InvalidSyntaxException e) {
            throw new IllegalArgumentException(e);
        }
        if (references == null) {
            throw new IllegalArgumentException("Talend job '" + name + "' not found");
        }
        final TalendJob talendJob = (TalendJob) bundleContext.getService(references[0]);
        startJob(new ESBJobThread(talendJob, new String[0], controller, this, this));
    }

    private void startJob(final Thread thread) {
        thread.setContextClassLoader(this.getClass().getClassLoader());
        thread.start();
    }

    public void stopJob(final TalendJob talendJob) {
        Thread thread = jobs.get(talendJob);
        if (thread != null) {
            thread.interrupt();
        }
    }

    public void jobStarted(TalendJob talendJob, Thread thread) {
        jobs.put(talendJob, thread);
    }

    public void jobFinished(TalendJob talendJob, Thread thread) {
        Thread registeredThread = jobs.remove(talendJob);
        if (registeredThread != thread) {
            throw new IllegalArgumentException(
                "Different threads found for the talend job");
        }

        final RuntimeESBConsumer runtimeESBConsumer = tlsConsumer.get();
        if (runtimeESBConsumer != null) {
            runtimeESBConsumer.destroy();
        }
    }

    private ESBProviderCallback createESBProvider(final Map<String, Object> props) {
        final String publishedEndpointUrl = (String)props.get(ESBEndpointConstants.PUBLISHED_ENDPOINT_URL);
        final QName serviceName = QName.valueOf((String)props.get(ESBEndpointConstants.SERVICE_NAME));
        final QName portName = QName.valueOf((String)props.get(ESBEndpointConstants.PORT_NAME));

        ESBProviderKey key = new ESBProviderKey(serviceName, portName);
        Collection<ESBProvider> esbProviders = endpoints.get(key);
        if(null == esbProviders) {
            esbProviders = new ArrayList<ESBProvider>(1);
            endpoints.put(key, esbProviders);
        }

        // TODO: add publishedEndpointUrl to ESBProviderKey
        ESBProvider esbProvider = null;
        for(ESBProvider provider : esbProviders) {
            if(publishedEndpointUrl.equals(provider.getPublishedEndpointUrl())) {
                esbProvider = provider;
                break;
            }
        }
        if(esbProvider == null) {
            boolean useServiceLocator =
                ((Boolean)props.get(ESBEndpointConstants.USE_SERVICE_LOCATOR)).booleanValue();
            boolean useServiceActivityMonitor =
                ((Boolean)props.get(ESBEndpointConstants.USE_SERVICE_ACTIVITY_MONITOR)).booleanValue();

            esbProvider = new ESBProvider(publishedEndpointUrl,
                serviceName,
                portName,
                useServiceLocator ? new LocatorFeature() : null,
                useServiceActivityMonitor ? createEventFeature() : null);
            esbProvider.run(bus);
            esbProviders.add(esbProvider);
        }

        final String operationName = (String)props.get(ESBEndpointConstants.DEFAULT_OPERATION_NAME);
        ESBProviderCallback esbProviderCallback =
            esbProvider.createESBProviderCallback(operationName,
                OperationStyle.isRequestResponse((String)props.get(ESBEndpointConstants.COMMUNICATION_STYLE)));

        return esbProviderCallback;
    }

    private void destroyESBProvider(final Map<String, Object> props) {
        final QName serviceName = QName.valueOf((String)props.get(ESBEndpointConstants.SERVICE_NAME));
        final QName portName = QName.valueOf((String)props.get(ESBEndpointConstants.PORT_NAME));
        final String publishedEndpointUrl = (String)props.get(ESBEndpointConstants.PUBLISHED_ENDPOINT_URL);

        final Collection<ESBProvider> esbProviders = endpoints.get(
            new ESBProviderKey(serviceName, portName));
        if (esbProviders != null) {
            for (ESBProvider esbProvider : esbProviders) {
                if(publishedEndpointUrl.equals(esbProvider.getPublishedEndpointUrl())) {
                    final String operationName = (String)props.get(ESBEndpointConstants.DEFAULT_OPERATION_NAME);
                    if(esbProvider.destroyESBProviderCallback(operationName)) {
                        esbProviders.remove(esbProvider);
                    }
                    break;
                }
            }
        }
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

    class LazyESBProviderCallbackController
            implements ESBProviderCallbackController, ESBProviderCallback {

        private ESBEndpointInfo endpointInfo;
        private ESBProviderCallback delegate;

        public ESBProviderCallback createESBProviderCallback(
            final ESBEndpointInfo esbEndpointInfo) {
            this.endpointInfo = esbEndpointInfo;
            // Inject lazy initialization callback to the job
            return this;
        }

        public void destroyESBProviderCallback() {
            if (null != endpointInfo) {
                destroyESBProvider(endpointInfo.getEndpointProperties());
            }
        }

        public boolean isRequired() {
            return false;
        }

        public synchronized Object getRequest() throws ESBJobInterruptedException {
            if (delegate == null) {
                // This will be run after #getRequest will be called from the job
                try {
                    delegate = createESBProvider(endpointInfo.getEndpointProperties());
                } catch (Exception e) {
                    throw new ESBJobInterruptedException(e.getMessage(), e);
                }
            }
            return delegate.getRequest();
        }

        public void sendResponse(Object response) {
            if (delegate != null) {
                delegate.sendResponse(response);
            }
        }

    }

}
