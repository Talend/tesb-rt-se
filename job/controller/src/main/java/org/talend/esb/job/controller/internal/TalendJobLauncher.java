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

import routines.system.api.ESBEndpointInfo;
import routines.system.api.ESBProviderCallback;
import routines.system.api.TalendESBJob;
import routines.system.api.TalendJob;

public class TalendJobLauncher {

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
//				System.out.println("endpoint.getEndpointKey()="+endpoint.getEndpointKey());
//				System.out.println("endpoint.getEndpointUri()="+endpoint.getEndpointUri());
//				System.out.println("endpoint.getEndpointProperties()="+endpoint.getEndpointProperties());

//				endpoint.getEndpointKey()=cxf
//				endpoint.getEndpointUri()=TOS_TEST_ProviderJob
//				endpoint.getEndpointProperties()=
//					defaultOperationNameSpace=,
//					defaultOperationName=invoke,
//					dataFormat=PAYLOAD,
//					publishedEndpointUrl=http://127.0.0.1:8088/esb/provider,
//					portName={http://talend.org/esb/service/job}TalendJobAsWebService,
//					serviceName={http://talend.org/esb/service/job}TOS_TEST_ProviderJob,
//					COMMUNICATION_STYLE=request-response}
				
				Map<String, Object> props = endpoint.getEndpointProperties();
				
				ESBProvider esbProvider = createEndpoint(props);
				String defaultOperationName = (String)props.get(DEFAULT_OPERATION_NAME);
				
				// uncomment following lines to get
//				karaf@tesb> Exception in thread "Thread-27" javax.xml.ws.spi.FactoryFinder$ConfigurationError: Provider org.apache.cxf.j
//				axws.spi.ProviderImpl not found
//				        at javax.xml.ws.spi.FactoryFinder$2.run(FactoryFinder.java:130)
//				        at javax.xml.ws.spi.FactoryFinder.doPrivileged(FactoryFinder.java:220)
//				        at javax.xml.ws.spi.FactoryFinder.newInstance(FactoryFinder.java:124)
//				        at javax.xml.ws.spi.FactoryFinder.access$200(FactoryFinder.java:44)
//				        at javax.xml.ws.spi.FactoryFinder$3.run(FactoryFinder.java:211)
//				        at javax.xml.ws.spi.FactoryFinder.doPrivileged(FactoryFinder.java:220)
//				        at javax.xml.ws.spi.FactoryFinder.find(FactoryFinder.java:160)
//				        at javax.xml.ws.spi.Provider.provider(Provider.java:43)
//				        at javax.xml.ws.Endpoint.create(Endpoint.java:41)
//				        at javax.xml.ws.Endpoint.create(Endpoint.java:37)
//				        at org.talend.esb.job.controller.internal.ESBProvider.run(ESBProvider.java:67)
				
//				ESBProviderCallback esbProviderCallback =
//					esbProvider.getESBProviderCallback(defaultOperationName);
//				talendESBJob.setProviderCallback(esbProviderCallback);
			}
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
			
			esbProvider.start();

			endpoints.put(publishedEndpointUrl, esbProvider);
		}
		return esbProvider;
	}
}
