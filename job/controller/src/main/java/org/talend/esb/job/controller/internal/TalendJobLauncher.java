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
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.cxf.feature.AbstractFeature;

import routines.system.api.ESBConsumer;
import routines.system.api.ESBEndpointInfo;
import routines.system.api.ESBEndpointRegistry;
import routines.system.api.ESBProviderCallback;
import routines.system.api.TalendESBJob;
import routines.system.api.TalendJob;

public class TalendJobLauncher implements ESBEndpointRegistry {

	private static final String PUBLISHED_ENDPOINT_URL = "publishedEndpointUrl";
	private static final String DEFAULT_OPERATION_NAME = "defaultOperationName";
	private static final String SERVICE_NAME = "serviceName";
	private static final String PORT_NAME = "portName";
	private static final String COMMUNICATION_STYLE = "COMMUNICATION_STYLE";
	private static final String USE_SERVICE_LOCATOR = "useServiceLocator";
	private static final String USE_SERVICE_ACTIVITY_MONITOR = "useServiceActivityMonitor";

	private static final String VALUE_REQUEST_RESPONSE = "request-response";

	private static final Logger LOG = Logger.getLogger(TalendJobLauncher.class.getName());

	private final Map<ESBProviderKey, Collection<ESBProvider> > endpoints =
		new ConcurrentHashMap<ESBProviderKey, Collection<ESBProvider>>();
	private final Map<TalendJob, Thread > jobs =
		new ConcurrentHashMap<TalendJob, Thread>();

	private AbstractFeature serviceLocator;
	private AbstractFeature serviceActivityMonitoring;

	public void setServiceLocator(AbstractFeature serviceLocator) {
		this.serviceLocator = serviceLocator;
	}

	public void setServiceActivityMonitoring(
			AbstractFeature serviceActivityMonitoring) {
		this.serviceActivityMonitoring = serviceActivityMonitoring;
	}

	public void runTalendJob(final TalendJob talendJob, final String[] args) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
		        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
				try {
				    Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());

					if (talendJob instanceof TalendESBJob) {
						// We have an ESB Job;
						TalendESBJob talendESBJob =  (TalendESBJob) talendJob;
						// get provider end point information
						final ESBEndpointInfo endpoint = talendESBJob.getEndpoint();
						if (null != endpoint) {
							talendESBJob.setProviderCallback(
								createESBProvider(endpoint.getEndpointProperties()));
						}
						talendESBJob.setEndpointRegistry(TalendJobLauncher.this);
					}

					LOG.info("Talend Job starting...");
					int ret = talendJob.runJobInTOS(args);
					LOG.info("Talend Job finished with code " + ret);

					if (talendJob instanceof TalendESBJob) {
						TalendESBJob talendESBJob =  (TalendESBJob) talendJob;
						final ESBEndpointInfo endpoint = talendESBJob.getEndpoint();
						if (null != endpoint) {
							destroyESBProvider(endpoint.getEndpointProperties());
						}
					}
				} finally {
					jobs.remove(talendJob);
		            Thread.currentThread().setContextClassLoader(contextClassLoader);
				}
			}
		});
		thread.start();
		jobs.put(talendJob, thread);
	}

	public void stopTalendJob(final TalendJob talendJob) {
		jobs.get(talendJob).interrupt();
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
					useServiceActivityMonitor ? serviceActivityMonitoring : null);
			esbProviders.add(esbProvider);
		}

		final String operationName = (String)props.get(DEFAULT_OPERATION_NAME);
		ESBProviderCallback esbProviderCallback =
			esbProvider.createESBProviderCallback(operationName,
					VALUE_REQUEST_RESPONSE.equals(props.get(COMMUNICATION_STYLE)));

		return esbProviderCallback;
	}

	private void destroyESBProvider(final Map<String, Object> props) {
		final QName serviceName = QName.valueOf((String)props.get(SERVICE_NAME));
		final QName portName = QName.valueOf((String)props.get(PORT_NAME));
		final String publishedEndpointUrl = (String)props.get(PUBLISHED_ENDPOINT_URL);

		Collection<ESBProvider> esbProviders = endpoints.get(
				new ESBProviderKey(serviceName, portName));
		ESBProvider esbProvider = null;
		for(ESBProvider provider : esbProviders) {
			if(publishedEndpointUrl.equals(provider.getPublishedEndpointUrl())) {
				esbProvider = provider;
				break;
			}
		}

		final String operationName = (String)props.get(DEFAULT_OPERATION_NAME);
		if(esbProvider.destroyESBProviderCallback(operationName)) {
			esbProviders.remove(esbProvider);
		}
	}

	@Override
	public ESBConsumer createConsumer(ESBEndpointInfo endpoint) {
		final Map<String, Object> props = endpoint.getEndpointProperties();

		final QName serviceName = QName.valueOf((String)props.get(SERVICE_NAME));
		final QName portName = QName.valueOf((String)props.get(PORT_NAME));
		final String operationName = (String)props.get(DEFAULT_OPERATION_NAME);

		ESBConsumer esbConsumer = null;
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
					VALUE_REQUEST_RESPONSE.equals(props.get(COMMUNICATION_STYLE)),
					useServiceLocator ? serviceLocator : null,
					useServiceActivityMonitor ? serviceActivityMonitoring : null);
		}
		return esbConsumer;
	}
}
