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

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;

import routines.system.api.ESBJobInterruptedException;
import routines.system.api.ESBProviderCallback;

//@javax.jws.WebService(name = "TalendJobAsWebService", targetNamespace = "http://talend.org/esb/service/job")
//@javax.jws.soap.SOAPBinding(parameterStyle = javax.jws.soap.SOAPBinding.ParameterStyle.BARE)
//@javax.xml.ws.ServiceMode(value = javax.xml.ws.Service.Mode.MESSAGE)
@javax.xml.ws.WebServiceProvider()
class ESBProvider implements javax.xml.ws.Provider<javax.xml.transform.Source> {
	
	private javax.xml.transform.TransformerFactory factory =
		javax.xml.transform.TransformerFactory.newInstance();

	private Map<String, ESBProviderCallback> callbacks = new ConcurrentHashMap<String, ESBProviderCallback>();
	private String publishedEndpointUrl;
	private QName serviceName;
	private QName portName;

	class TalendESBProviderCallback implements ESBProviderCallback {
		@Override
		public Object getRequest() throws ESBJobInterruptedException {
			System.out.println("getRequest");
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
			final QName serviceName,
			final QName portName) {
		this.publishedEndpointUrl = publishedEndpointUrl;
		this.serviceName = serviceName;
		this.portName = portName;
		
		System.out.println("original serviceName=" + this.serviceName);
		System.out.println("original portName=" + this.portName);
		
		this.serviceName = QName.valueOf("{http://customerservice.example.com/}TOS_TEST_ProviderJob");
		this.portName = QName.valueOf("{http://customerservice.example.com/}TOS_TEST_ProviderJobSoapBinding");

		System.out.println("fixed serviceName=" + this.serviceName);
		System.out.println("fixed portName=" + this.portName);
	}
	
	public void run() {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
	    try{
		    Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
			
		    javax.xml.ws.Endpoint endpoint = javax.xml.ws.Endpoint.create(this);
			
			@SuppressWarnings("serial")
			java.util.Map<String, Object> properties = new java.util.HashMap<String, Object>() {
				{
					put(javax.xml.ws.Endpoint.WSDL_SERVICE,
						serviceName);
					put(javax.xml.ws.Endpoint.WSDL_PORT,
						portName);
				}
			};
			endpoint.setProperties(properties);
			endpoint.publish(publishedEndpointUrl);

//			JaxWsServerFactoryBean sf = new JaxWsServerFactoryBean();
//			sf.setServiceName(serviceName);
//			sf.setEndpointName(portName);
//			sf.setAddress(publishedEndpointUrl);
//			sf.setServiceBean(this);
//			//sf.setInvoker(invoker)
//
//			Server srv = sf.create();
			
			System.out.println("web service [endpoint: "
					+ publishedEndpointUrl + "] published");
        }finally{
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
	}
	
	@Override
//	@javax.jws.WebMethod(operationName = "getCustomersByName", action = "http://talend.org/esb/service/job/invoke")
//	@javax.jws.WebResult(name = "jobOutput", targetNamespace = "http://talend.org/esb/service/job",
//	partName = "response")
	public Source invoke(Source request) {
		//callbacks.get(key)
		try {
			org.dom4j.io.DocumentResult docResult = new org.dom4j.io.DocumentResult();
			factory.newTransformer().transform(request,
					docResult);
			org.dom4j.Document requestDoc = docResult
					.getDocument();
			 System.out.println("request: " +
			 requestDoc.asXML());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	public ESBProviderCallback getESBProviderCallback(String defaultOperationName) {
		if(callbacks.get(defaultOperationName) != null) {
			throw new RuntimeException("Operation '" + defaultOperationName + "' for endpoint '" + publishedEndpointUrl + "' already registered");
		}
		ESBProviderCallback esbProviderCallback = new TalendESBProviderCallback();
		callbacks.put(defaultOperationName, esbProviderCallback);
		
//		this.getClass().getAnnotation(javax.jws.WebMethod.class).
		

		return esbProviderCallback;
	}
}
