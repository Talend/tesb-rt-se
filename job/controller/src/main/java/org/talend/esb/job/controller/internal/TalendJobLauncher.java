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

	private Map<String, ESBProvider> endpoints = new ConcurrentHashMap<String, ESBProvider>();

	public void runTalendJob(final TalendJob talendJob, final String[] args) {
		
		if (talendJob instanceof TalendESBJob) {
			// We have an ESB Job;
			TalendESBJob talendESBJob =  (TalendESBJob) talendJob;
			// get provider end point information
			final ESBEndpointInfo endpoint = talendESBJob.getEndpoint();
			if (null != endpoint) {
				Map<String, Object> props = endpoint.getEndpointProperties();
				
				ESBProvider esbProvider = createEndpoint(props);
				String defaultOperationName = (String)props.get(DEFAULT_OPERATION_NAME);
				
				ESBProviderCallback esbProviderCallback =
					esbProvider.getESBProviderCallback(defaultOperationName);
				talendESBJob.setProviderCallback(esbProviderCallback);
			}
			
//			talendESBJob.setEndpointRegistry(this);
		}
		
        new Thread(new Runnable() {
			
			@Override
			public void run() {
				talendJob.runJob(args);
			}
		}).start();

	}

	private ESBProvider createEndpoint(final Map<String, Object> props) {
		String publishedEndpointUrl = (String)props.get(PUBLISHED_ENDPOINT_URL);
		ESBProvider esbProvider = endpoints.get(publishedEndpointUrl);
		if(null == esbProvider) {
			esbProvider = new ESBProvider(publishedEndpointUrl,
					QName.valueOf((String)props.get(SERVICE_NAME)),
					QName.valueOf((String)props.get(PORT_NAME)));
			
			esbProvider.run();

			endpoints.put(publishedEndpointUrl, esbProvider);
		}
		return esbProvider;
	}

	@Override
	public ESBConsumer createConsumer(ESBEndpointInfo endpoint) {
		System.out.println("getEndpointKey="+endpoint.getEndpointKey());
		System.out.println("getEndpointUri="+endpoint.getEndpointUri());
		System.out.println("getEndpointProperties="+endpoint.getEndpointProperties());
//		ESBProvider esbProvider = endpoints.get(endpoint.get);
		return null;
	}
}
