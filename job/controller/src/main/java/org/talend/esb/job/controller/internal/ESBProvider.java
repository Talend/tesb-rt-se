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
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.cxf.binding.soap.model.SoapOperationInfo;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.service.model.BindingInfo;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.service.model.InterfaceInfo;
import org.apache.cxf.service.model.MessageInfo;
import org.apache.cxf.service.model.MessagePartInfo;
import org.apache.cxf.service.model.OperationInfo;
import org.apache.cxf.service.model.ServiceInfo;

//@javax.jws.WebService(name = "TalendJobAsWebService", targetNamespace = "http://talend.org/esb/service/job")
//@javax.jws.soap.SOAPBinding(parameterStyle = javax.jws.soap.SOAPBinding.ParameterStyle.BARE)
@javax.xml.ws.ServiceMode(value = javax.xml.ws.Service.Mode.PAYLOAD)
@javax.xml.ws.WebServiceProvider()
class ESBProvider implements javax.xml.ws.Provider<javax.xml.transform.Source> {
	
	private static final Logger LOG = Logger.getLogger(ESBProvider.class.getName());
	private javax.xml.transform.TransformerFactory factory =
		javax.xml.transform.TransformerFactory.newInstance();

	private Map<String, RuntimeESBProviderCallback> callbacks =
		new ConcurrentHashMap<String, RuntimeESBProviderCallback>();
	private String publishedEndpointUrl;
	private QName serviceName;
	private QName portName;

	private Server server;

	@Resource
	private WebServiceContext context;

	public ESBProvider(String publishedEndpointUrl,
			final QName serviceName,
			final QName portName) {
		this.publishedEndpointUrl = publishedEndpointUrl;
		this.serviceName = serviceName;
		this.portName = portName;

		run();
	}

	public String getPublishedEndpointUrl() {
		return publishedEndpointUrl;
	}

	private void run() {
		JaxWsServerFactoryBean sf = new JaxWsServerFactoryBean();
		sf.setServiceName(serviceName);
		sf.setEndpointName(portName);
		sf.setAddress(publishedEndpointUrl);
		sf.setServiceBean(this);

		server = sf.create();

		// remove default operation
		removeOperation("invoke");

		LOG.info("Web service '" + serviceName + "' published at endpoint '"
				+ publishedEndpointUrl + "'");
	}

	@Override
	//@javax.jws.WebMethod(exclude=true)
	public Source invoke(Source request) {
		QName operationQName = (QName)context.getMessageContext().get(MessageContext.WSDL_OPERATION);
		LOG.info("Invoke operation '" + operationQName + "' for service '" + serviceName + "'");
		RuntimeESBProviderCallback esbProviderCallback =
			getESBProviderCallback(operationQName.getLocalPart());
		if(esbProviderCallback == null) {
			throw new RuntimeException("Handler for operation " + operationQName + " cannot be found");
		}
		try {
			org.dom4j.io.DocumentResult docResult = new org.dom4j.io.DocumentResult();
			factory.newTransformer().transform(request, docResult);
			org.dom4j.Document requestDoc = docResult.getDocument();

			//System.out.println("request: " +requestDoc.asXML());
			Object result = esbProviderCallback.invoke(requestDoc);

			// oneway
			if(result == null) {
				return null;
			}
			if(result instanceof org.dom4j.Document) {
				return new org.dom4j.io.DocumentSource(
						(org.dom4j.Document)result);
			} else if (result instanceof RuntimeException){
				throw (RuntimeException)result;
			} else if (result instanceof Throwable){
				throw new RuntimeException((Throwable)result);
			} else {
				throw new RuntimeException(
					"Provider return incompatible object: " + result.getClass().getName());
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public RuntimeESBProviderCallback createESBProviderCallback(String operationName, boolean isRequestResponse) {
		if(callbacks.get(operationName) != null) {
			throw new RuntimeException("Operation '" + operationName + "' for endpoint '" + publishedEndpointUrl + "' already registered");
		}
		RuntimeESBProviderCallback esbProviderCallback = new RuntimeESBProviderCallback(isRequestResponse);
		callbacks.put(operationName, esbProviderCallback);

		addOperation(operationName, isRequestResponse);

		return esbProviderCallback;
	}

	public RuntimeESBProviderCallback getESBProviderCallback(String operationName) {
		return callbacks.get(operationName);
	}

	public boolean destroyESBProviderCallback(String operationName) {
		callbacks.remove(operationName);
		if(!callbacks.isEmpty()) {
			removeOperation(operationName);
		} else {
			LOG.info("Web service '" + serviceName + "' stopped");
			server.destroy();
			return true;
		}
		return false;
	}

	private void addOperation(String operationName, boolean isRequestResponse) {
		ServiceInfo si = server.getEndpoint().getService().getServiceInfos().get(0);
		InterfaceInfo ii = si.getInterface();

        final String namespace = ii.getName().getNamespaceURI();
		OperationInfo oi = ii.addOperation(new QName(namespace,
				operationName));
		MessageInfo mii = oi.createMessage(new QName(namespace,
				operationName + "RequestMsg"), MessageInfo.Type.INPUT);
		oi.setInput(operationName + "RequestMsg", mii);
		MessagePartInfo mpi = mii.addMessagePart("request");
		//SchemaInfo si = ii.getService().getSchemas().get(0);
		//mpi.setElementQName(new QName(serviceName.getNamespaceURI(), operationName + "Request"));
		mpi.setTypeQName(new QName("http://www.w3.org/2001/XMLSchema", "anyType"));
		
		if(isRequestResponse) {
			MessageInfo mio = oi.createMessage(new QName(namespace,
					operationName + "ResponseMsg"), MessageInfo.Type.OUTPUT);
			oi.setOutput(operationName + "ResponseMsg", mio);
			mpi = mio.addMessagePart("response");
			//mpi.setElementQName(new QName(serviceName.getNamespaceURI(), operationName + "Response"));
			mpi.setTypeQName(new QName("http://www.w3.org/2001/XMLSchema", "anyType"));
		}

		BindingInfo bi = si.getBindings().iterator().next();
        BindingOperationInfo boi = new BindingOperationInfo(bi, oi);
		SoapOperationInfo soi = new SoapOperationInfo();
		soi.setAction(operationName);
		boi.addExtensor(soi);

        bi.addOperation(boi);
	}

	private void removeOperation(String operationName) {
		ServiceInfo si = server.getEndpoint().getService().getServiceInfos().get(0);
		InterfaceInfo ii = si.getInterface();

        final String namespace = ii.getName().getNamespaceURI();
		OperationInfo oi = ii.getOperation(new QName(namespace,
				operationName));
		ii.removeOperation(oi);

		BindingInfo bi = si.getBindings().iterator().next();
        BindingOperationInfo boi = bi.getOperation(oi);
        bi.removeOperation(boi);
	}
}
