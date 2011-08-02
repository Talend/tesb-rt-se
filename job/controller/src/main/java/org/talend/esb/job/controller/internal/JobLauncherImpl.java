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
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.cxf.Bus;
import org.apache.cxf.feature.AbstractFeature;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.talend.esb.job.controller.JobLauncher;
import org.talend.esb.sam.common.handler.impl.CustomInfoHandler;

import routines.system.api.ESBConsumer;
import routines.system.api.ESBEndpointInfo;
import routines.system.api.ESBEndpointRegistry;
import routines.system.api.ESBProviderCallback;
import routines.system.api.TalendESBJob;
import routines.system.api.TalendJob;

public class JobLauncherImpl implements JobLauncher, ESBEndpointRegistry {

    private static final String PUBLISHED_ENDPOINT_URL = "publishedEndpointUrl";
    private static final String DEFAULT_OPERATION_NAME = "defaultOperationName";
    private static final String SERVICE_NAME = "serviceName";
    private static final String PORT_NAME = "portName";
    private static final String COMMUNICATION_STYLE = "COMMUNICATION_STYLE";
    private static final String USE_SERVICE_LOCATOR = "useServiceLocator";
    private static final String USE_SERVICE_ACTIVITY_MONITOR = "useServiceActivityMonitor";

    private static final String VALUE_REQUEST_RESPONSE = "request-response";
    private static final String VALUE_ONE_WAY = "one-way";

    private static final Logger LOG = Logger.getLogger(JobLauncherImpl.class.getName());

    private final Map<ESBProviderKey, Collection<ESBProvider> > endpoints =
        new ConcurrentHashMap<ESBProviderKey, Collection<ESBProvider>>();
    private final Map<TalendJob, Thread > jobs =
        new ConcurrentHashMap<TalendJob, Thread>();

    private Bus bus;
    private AbstractFeature serviceLocator;
    private AbstractFeature serviceActivityMonitoring;
    private CustomInfoHandler customInfoHandler;
    private BundleContext bundleContext;

    public void setBus(Bus bus) {
        this.bus = bus;
    }

    public void setServiceLocator(AbstractFeature serviceLocator) {
        this.serviceLocator = serviceLocator;
    }

    public void setServiceActivityMonitoring(
            AbstractFeature serviceActivityMonitoring) {
        this.serviceActivityMonitoring = serviceActivityMonitoring;
    }

    public void setCustomInfoHandler(CustomInfoHandler customInfoHandler) {
        this.customInfoHandler = customInfoHandler;
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    class ESBJobRunnable implements Runnable {
        private final TalendJob talendJob;
        private final String[] args;
        private final ESBProviderCallback esbProviderCallback;

        public ESBJobRunnable(final TalendJob talendJob, final String[] args) {
            this.talendJob = talendJob;
            this.args = args;
            esbProviderCallback = null;
        }

        public ESBJobRunnable(final TalendJob talendJob, final ESBProviderCallback esbProviderCallback) {
            this.talendJob = talendJob;
            args = new String[0];
            this.esbProviderCallback = esbProviderCallback;
        }

        @Override
        public void run() {
            try {
                LazyProviderCallbackDelegate cb = null;
                if (talendJob instanceof TalendESBJob) {
                    // We have an ESB Job;
                    final TalendESBJob talendESBJob = (TalendESBJob) talendJob;
                    // get provider endpoint information
                    final ESBEndpointInfo endpoint = talendESBJob.getEndpoint();
                    if (null != endpoint) {
                        // TODO: check for compatible communication style
                        ESBProviderCallback esbProviderCallback = this.esbProviderCallback;
                        if(esbProviderCallback == null) {
                            // Create callback delegate
                            cb = new LazyProviderCallbackDelegate(new Callable<ESBProviderCallback>() {
                                public ESBProviderCallback call() throws Exception {
                                    // This will be run after #getRequest will be called from the job
                                    // TODO: check if getEndpoint static
                                    return createESBProvider(endpoint.getEndpointProperties());
                                }
                            }, new Runnable() {
                                public void run() {
                                    destroyESBProvider(endpoint.getEndpointProperties());
                                }
                            });
                            // Inject lazy initialization callback to the job
                            esbProviderCallback = cb;
                        }
                        talendESBJob.setProviderCallback(esbProviderCallback);
                    } else if (esbProviderCallback != null) {
                        throw new IllegalArgumentException("Provider job expected");
                    }
                    talendESBJob.setEndpointRegistry(JobLauncherImpl.this);
                }

                LOG.info("Talend Job starting...");
                int ret = talendJob.runJobInTOS(args);
                LOG.info("Talend Job finished with code " + ret);

                // TODO: add cleanup for external esbProviderCallback
                if (cb != null) {
                    cb.shutdown();
                }
            } finally {
                jobs.remove(talendJob);
            }
        }

        public TalendJob getTalendJob() {
            return talendJob;
        }
    }

    public void startJob(final TalendJob talendJob, final String[] args) {
        startJob(new ESBJobRunnable(talendJob, args));
    }

    public void startJob(String name, final ESBProviderCallback esbProviderCallback) {
        // ControllerImpl
        ServiceReference[] references;
        try {
            references = bundleContext.getServiceReferences(TalendJob.class.getName(), "(name=" + name + ")");
        } catch (InvalidSyntaxException e) {
            throw new IllegalArgumentException(e);
        }
        if (references == null) {
            throw new IllegalArgumentException("Talend job '" + name + "' not found");
        }
        final TalendJob talendJob = (TalendJob) bundleContext.getService(references[0]);
        startJob(new ESBJobRunnable(talendJob, esbProviderCallback));
    }

    private void startJob(final ESBJobRunnable runnable) {
        Thread thread = new Thread(runnable);
        thread.setContextClassLoader(this.getClass().getClassLoader());
        thread.start();
        jobs.put(runnable.getTalendJob(), thread);
    }

    public void stopJob(final TalendJob talendJob) {
        Thread thread = jobs.get(talendJob);
        if (thread != null) {
            thread.interrupt();
        }
    }

    private ESBProviderCallback createESBProvider(final Map<String, Object> props) {
        final String publishedEndpointUrl = (String)props.get(PUBLISHED_ENDPOINT_URL);
        final QName serviceName = QName.valueOf((String)props.get(SERVICE_NAME));
        final QName portName = QName.valueOf((String)props.get(PORT_NAME));

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
                ((Boolean)props.get(USE_SERVICE_LOCATOR)).booleanValue();
            boolean useServiceActivityMonitor =
                ((Boolean)props.get(USE_SERVICE_ACTIVITY_MONITOR)).booleanValue();

            esbProvider = new ESBProvider(publishedEndpointUrl,
                serviceName,
                portName,
                useServiceLocator ? serviceLocator : null,
                useServiceActivityMonitor ? serviceActivityMonitoring : null,
                customInfoHandler);
            esbProvider.run(bus);
            esbProviders.add(esbProvider);
        }

        final String operationName = (String)props.get(DEFAULT_OPERATION_NAME);
        ESBProviderCallback esbProviderCallback =
            esbProvider.createESBProviderCallback(operationName,
                isRequestResponse((String)props.get(COMMUNICATION_STYLE)));

        return esbProviderCallback;
    }

    private void destroyESBProvider(final Map<String, Object> props) {
        final QName serviceName = QName.valueOf((String)props.get(SERVICE_NAME));
        final QName portName = QName.valueOf((String)props.get(PORT_NAME));
        final String publishedEndpointUrl = (String)props.get(PUBLISHED_ENDPOINT_URL);

        final Collection<ESBProvider> esbProviders = endpoints.get(
            new ESBProviderKey(serviceName, portName));
        if (esbProviders != null) {
            for (ESBProvider esbProvider : esbProviders) {
                if(publishedEndpointUrl.equals(esbProvider.getPublishedEndpointUrl())) {
                    final String operationName = (String)props.get(DEFAULT_OPERATION_NAME);
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

		final QName serviceName = QName.valueOf((String)props.get(SERVICE_NAME));
		final QName portName = QName.valueOf((String)props.get(PORT_NAME));
		final String operationName = (String)props.get(DEFAULT_OPERATION_NAME);

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
			final String publishedEndpointUrl = (String)props.get(PUBLISHED_ENDPOINT_URL);
			boolean useServiceLocator =
				((Boolean)props.get(USE_SERVICE_LOCATOR)).booleanValue();
			boolean useServiceActivityMonitor =
				((Boolean)props.get(USE_SERVICE_ACTIVITY_MONITOR)).booleanValue();
			esbConsumer = new RuntimeESBConsumer(
					serviceName,
					portName,
					operationName,
					publishedEndpointUrl,
					isRequestResponse((String)props.get(COMMUNICATION_STYLE)),
					useServiceLocator ? serviceLocator : null,
					useServiceActivityMonitor ? serviceActivityMonitoring : null,
					customInfoHandler,
					bus);
		//}
		return esbConsumer;
	}

    private static boolean isRequestResponse(String value) {
        if (VALUE_ONE_WAY.equals(value)) {
            return false;
        } else if (VALUE_REQUEST_RESPONSE.equals(value)) {
            return true;
        }
        throw new RuntimeException("Unsupported communication style: " + value);
    }

}
