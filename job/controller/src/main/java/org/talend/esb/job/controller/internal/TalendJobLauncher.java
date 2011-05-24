package org.talend.esb.job.controller.internal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.transform.Source;

import routines.system.api.ESBEndpointInfo;
import routines.system.api.ESBJobInterruptedException;
import routines.system.api.ESBProviderCallback;
import routines.system.api.TalendESBJob;
import routines.system.api.TalendJob;

public class TalendJobLauncher {

	private static final String PUBLISHED_ENDPOINT_URL = "publishedEndpointUrl";
	private static final String DEFAULT_OPERATION_NAME = "defaultOperationName";

	private Map<String, ESBProvider> endpoints = new ConcurrentHashMap<String, TalendJobLauncher.ESBProvider>();

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
//				endpoint.getEndpointProperties()={defaultOperationNameSpace=, defaultOperationName=invoke, dataFormat=PAYLOAD, published
//				EndpointUrl=http://127.0.0.1:8088/esb/provider, portName={http://talend.org/esb/service/job}TalendJobAsWebService, servi
//				ceName={http://talend.org/esb/service/job}TOS_TEST_ProviderJob, COMMUNICATION_STYLE=request-response}
				
				Map<String, Object> props = endpoint.getEndpointProperties();
				String publishedEndpointUrl = (String)props.get(PUBLISHED_ENDPOINT_URL);
				
				ESBProvider esbProvider = openEndpoint(publishedEndpointUrl);
				String defaultOperationName = (String)props.get(DEFAULT_OPERATION_NAME);
				
				//talendESBJob.setProviderCallback(esbProvider.getESBProviderCallback(defaultOperationName));
			}
		}
		
        new Thread(new Runnable() {
			
			@Override
			public void run() {
				talendJob.runJob(args);
			}
		}).start();

	}

	class ESBProvider implements javax.xml.ws.Provider<javax.xml.transform.Source> {
		
		private Map<String, ESBProviderCallback> callbacks = new ConcurrentHashMap<String, ESBProviderCallback>();

		class TalendESBProviderCallback implements ESBProviderCallback {
			@Override
			public Object getRequest() throws ESBJobInterruptedException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void sendResponse(Object arg0) {
				// TODO Auto-generated method stub
				
			}
		}
		@Override
		public Source invoke(Source request) {
			//callbacks.get(key)
			return null;
		}

		public ESBProviderCallback getESBProviderCallback(String defaultOperationName) {
			ESBProviderCallback esbProviderCallback = new TalendESBProviderCallback();
			callbacks.put(defaultOperationName, esbProviderCallback);
			return esbProviderCallback;
		}
	}

	private ESBProvider openEndpoint(String publishedEndpointUrl) {
		ESBProvider esbProvider = endpoints.get(publishedEndpointUrl);
		if(null == esbProvider) {
			esbProvider = new ESBProvider();
			endpoints.put(publishedEndpointUrl, esbProvider);
			
//			javax.xml.ws.Endpoint endpoint = javax.xml.ws.Endpoint.create(esbProvider);
		}
		return esbProvider;
	}
}
