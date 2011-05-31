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

import javax.annotation.Resource;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;

//@javax.jws.WebService(name = "TalendJobAsWebService", targetNamespace = "http://talend.org/esb/service/job")
//@javax.jws.soap.SOAPBinding(parameterStyle = javax.jws.soap.SOAPBinding.ParameterStyle.BARE)
//@javax.xml.ws.ServiceMode(value = javax.xml.ws.Service.Mode.MESSAGE)
@javax.xml.ws.WebServiceProvider()
class ESBProvider implements javax.xml.ws.Provider<javax.xml.transform.Source> {
	
	private javax.xml.transform.TransformerFactory factory =
		javax.xml.transform.TransformerFactory.newInstance();

	private Map<String, RuntimeESBProviderCallback> callbacks = new ConcurrentHashMap<String, RuntimeESBProviderCallback>();
	private String publishedEndpointUrl;
	private QName serviceName;
	private QName portName;

	@Resource
	private WebServiceContext context;

	public ESBProvider(String publishedEndpointUrl,
			final QName serviceName,
			final QName portName) {
		this.publishedEndpointUrl = publishedEndpointUrl;
		this.serviceName = serviceName;
		this.portName = portName;
	}
	
	
	public String getPublishedEndpointUrl() {
		return publishedEndpointUrl;
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
	
	// TODO: add dynamic WebMethod
	@Override
	@javax.jws.WebMethod(operationName = "getCustomersByName", action = "http://talend.org/esb/service/job/invoke")
//	@javax.jws.WebResult(name = "jobOutput", targetNamespace = "http://talend.org/esb/service/job",
//	partName = "response")
	public Source invoke(Source request) {
		QName operationName = (QName)context.getMessageContext().get(MessageContext.WSDL_OPERATION);
		RuntimeESBProviderCallback esbProviderCallback =
			getESBProviderCallback(operationName.getLocalPart());
		if(esbProviderCallback == null) {
			throw new RuntimeException("Handler for operation '" + operationName.getLocalPart() + "' cannot be found");
		}
		try {
			org.dom4j.io.DocumentResult docResult = new org.dom4j.io.DocumentResult();
			factory.newTransformer().transform(request, docResult);
			org.dom4j.Document requestDoc = docResult.getDocument();
			
			System.out.println("request: " +requestDoc.asXML());
			Object result = esbProviderCallback.invoke(requestDoc);
			
			if(result instanceof org.dom4j.Document) {
				return new org.dom4j.io.DocumentSource(
						(org.dom4j.Document)result);
			} else {
				throw new RuntimeException("Provider return incompatible object: " + result.getClass().getName());
			}

		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public RuntimeESBProviderCallback createESBProviderCallback(String operationName) {
		if(callbacks.get(operationName) != null) {
			throw new RuntimeException("Operation '" + operationName + "' for endpoint '" + publishedEndpointUrl + "' already registered");
		}
		RuntimeESBProviderCallback esbProviderCallback = new RuntimeESBProviderCallback();
		callbacks.put(operationName, esbProviderCallback);
		
		// TODO: add operation

		return esbProviderCallback;
	}
	
	public RuntimeESBProviderCallback getESBProviderCallback(String operationName) {
		return callbacks.get(operationName);
	}
}
