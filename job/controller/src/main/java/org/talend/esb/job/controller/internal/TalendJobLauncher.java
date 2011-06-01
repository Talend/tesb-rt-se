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

import javax.xml.namespace.QName;

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

	private Map<ESBProviderKey, Collection<ESBProvider> > endpoints = new ConcurrentHashMap<ESBProviderKey, Collection<ESBProvider>>();

	public void runTalendJob(final TalendJob talendJob, final String[] args) {
		
		if (talendJob instanceof TalendESBJob) {
			// We have an ESB Job;
			TalendESBJob talendESBJob =  (TalendESBJob) talendJob;
			// get provider end point information
			final ESBEndpointInfo endpoint = talendESBJob.getEndpoint();
			if (null != endpoint) {
				Map<String, Object> props = endpoint.getEndpointProperties();
				
				talendESBJob.setProviderCallback(getESBProviderCallback(props));
			}
			
			talendESBJob.setEndpointRegistry(this);
		}
		
        new Thread(new Runnable() {
			
			@Override
			public void run() {
				talendJob.runJob(args);
				System.out.println("!!! job done");
			}
		}).start();

	}

	private ESBProviderCallback getESBProviderCallback(final Map<String, Object> props) {
		final String publishedEndpointUrl = (String)props.get(PUBLISHED_ENDPOINT_URL);
		final QName serviceName = QName.valueOf((String)props.get(SERVICE_NAME));
		final QName portName = QName.valueOf((String)props.get(PORT_NAME));
		
		ESBProviderKey key = new ESBProviderKey(serviceName, portName);
		Collection<ESBProvider> esbProviders = endpoints.get(key);
		if(null == esbProviders) {
			esbProviders = new ArrayList<ESBProvider>(1);
			endpoints.put(key, esbProviders);
		}

		ESBProvider esbProvider = null;
		for(ESBProvider provider : esbProviders) {
			if(publishedEndpointUrl.equals(provider.getPublishedEndpointUrl())) {
				esbProvider = provider;
				break;
			}
		}
		if(esbProvider == null) {
			esbProvider = new ESBProvider(publishedEndpointUrl,
					serviceName,
					portName);
			
			esbProvider.run();
			esbProviders.add(esbProvider);
		}

		final String operationName = (String)props.get(DEFAULT_OPERATION_NAME);
		ESBProviderCallback esbProviderCallback =
			esbProvider.createESBProviderCallback(operationName);

		return esbProviderCallback;
	}

	@Override
	public ESBConsumer createConsumer(ESBEndpointInfo endpoint) {
		System.out.println("getEndpointProperties="+endpoint.getEndpointProperties());
		
		Map<String, Object> props = endpoint.getEndpointProperties();

		QName serviceName = QName.valueOf((String)props.get(SERVICE_NAME));
		QName portName = QName.valueOf((String)props.get(PORT_NAME));

		ESBProviderKey key = new ESBProviderKey(serviceName, portName);
		Collection<ESBProvider> esbProviders = endpoints.get(key);
		if(esbProviders == null) {
			// TODO: create generic consumer
			throw new RuntimeException("No provider available for serviceName=" + serviceName + "; portName=" + portName);
		}
		
		String operationName = (String)props.get(DEFAULT_OPERATION_NAME);
		System.out.println("operationName="+operationName);
		RuntimeESBProviderCallback esbProviderCallback = null;
		for(ESBProvider provider : esbProviders) {
			esbProviderCallback = provider.getESBProviderCallback(operationName);
			if(esbProviderCallback != null) {
				break;
			}
		}
		if(esbProviderCallback == null) {
			// TODO: create generic consumer
			throw new RuntimeException("No provider available for operationName=" + operationName);
		}
		return esbProviderCallback;
	}
}
