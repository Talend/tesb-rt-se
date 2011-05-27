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
import javax.xml.transform.Source;

import routines.system.api.ESBJobInterruptedException;
import routines.system.api.ESBProviderCallback;

class ESBProvider extends Thread implements javax.xml.ws.Provider<javax.xml.transform.Source> {
	
	private Map<String, ESBProviderCallback> callbacks = new ConcurrentHashMap<String, ESBProviderCallback>();
	private String publishedEndpointUrl;
	private QName serviceName;
	private QName portName;

	class TalendESBProviderCallback implements ESBProviderCallback {
		@Override
		public Object getRequest() throws ESBJobInterruptedException {
			while(true) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
//				return null;
		}

		@Override
		public void sendResponse(Object arg0) {
			System.out.println("sendResponse");
		}
	}

	public ESBProvider(String publishedEndpointUrl,
			QName serviceName,
			QName portName) {
		this.publishedEndpointUrl = publishedEndpointUrl;
		this.serviceName = serviceName;
		this.portName = portName;
	}
	
	public void run() {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
	    try{
	    Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
		javax.xml.ws.Endpoint endpoint = javax.xml.ws.Endpoint.create(this);
		
		@SuppressWarnings("serial")
		java.util.Map<String, Object> map = new java.util.HashMap<String, Object>() {
			{
				put(javax.xml.ws.Endpoint.WSDL_SERVICE,
					serviceName);
				put(javax.xml.ws.Endpoint.WSDL_PORT,
					portName);
			}
		};
		endpoint.setProperties(map);
		endpoint.publish(publishedEndpointUrl);
		System.out.println("web service [endpoint: "
				+ publishedEndpointUrl + "] published");
        }finally{
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
	}
	
	@Override
	public Source invoke(Source request) {
		//callbacks.get(key)
		System.out.println("invoke");
		return null;
	}

	public ESBProviderCallback getESBProviderCallback(String defaultOperationName) {
		ESBProviderCallback esbProviderCallback = new TalendESBProviderCallback();
		callbacks.put(defaultOperationName, esbProviderCallback);
		return esbProviderCallback;
	}
}
