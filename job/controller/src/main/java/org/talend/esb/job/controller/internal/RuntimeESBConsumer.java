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

import javax.xml.namespace.QName;
import javax.xml.transform.Source;

import org.apache.cxf.Bus;
import org.apache.cxf.BusException;
import org.apache.cxf.binding.BindingFactory;
import org.apache.cxf.binding.BindingFactoryManager;
import org.apache.cxf.binding.soap.SoapBindingConstants;
import org.apache.cxf.databinding.source.SourceDataBinding;
import org.apache.cxf.endpoint.ClientImpl;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.endpoint.EndpointException;
import org.apache.cxf.endpoint.EndpointImpl;
import org.apache.cxf.service.Service;
import org.apache.cxf.service.ServiceImpl;
import org.apache.cxf.service.model.BindingInfo;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.service.model.InterfaceInfo;
import org.apache.cxf.service.model.MessageInfo;
import org.apache.cxf.service.model.MessagePartInfo;
import org.apache.cxf.service.model.OperationInfo;
import org.apache.cxf.service.model.ServiceInfo;
import org.apache.cxf.transport.ConduitInitiator;
import org.apache.cxf.transport.ConduitInitiatorManager;

import routines.system.api.ESBConsumer;

public class RuntimeESBConsumer implements ESBConsumer {

	private final QName serviceName;
	private final QName portName;
	private final String operationName;

	private ClientImpl client;
	private javax.xml.transform.TransformerFactory factory =
		javax.xml.transform.TransformerFactory.newInstance();
	
	public RuntimeESBConsumer(
			final QName serviceName,
			final QName portName,
			String operationName) {
		this.serviceName = serviceName;
		this.portName = portName;
		this.operationName = operationName;
	
		try {
			create();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private void create() throws BusException, EndpointException {
		Bus bus = org.apache.cxf.bus.spring.SpringBusFactory.getDefaultBus(true);

		ServiceInfo si = new ServiceInfo();
		si.setName(serviceName);

		InterfaceInfo ii = new InterfaceInfo(si, serviceName);
		OperationInfo oi = ii.addOperation(new QName(serviceName.getNamespaceURI(),
				operationName));
		MessageInfo mii = oi.createMessage(new QName(serviceName.getNamespaceURI(),
				operationName + "RequestMsg"), MessageInfo.Type.INPUT);
		oi.setInput(operationName + "RequestMsg", mii);
		MessagePartInfo mpi = mii.addMessagePart("request");
		mpi.setElementQName(new QName(serviceName.getNamespaceURI(), operationName + "Request"));

		// TODO: use communication style from ESBEndpointInfo
//		if(isRequestResponse) {
			MessageInfo mio = oi.createMessage(new QName(serviceName.getNamespaceURI(),
					operationName + "ResponseMsg"), MessageInfo.Type.OUTPUT);
			oi.setOutput(operationName + "ResponseMsg", mio);
			mpi = mio.addMessagePart("response");
			mpi.setElementQName(new QName(serviceName.getNamespaceURI(), operationName + "Response"));
//		}
		
		si.setInterface(ii);
		Service service = new ServiceImpl(si);

		BindingFactoryManager bfm = bus
				.getExtension(BindingFactoryManager.class);
		BindingFactory bindingFactory = bfm.getBindingFactory(SoapBindingConstants.SOAP11_BINDING_ID);
		BindingInfo bi = bindingFactory.createBindingInfo(service, SoapBindingConstants.SOAP11_BINDING_ID,
				null);
		si.addBinding(bi);

		// TODO: use endpoint URL from ESBEndpointInfo
		ConduitInitiatorManager cim = bus
				.getExtension(ConduitInitiatorManager.class);
		ConduitInitiator ci = cim.getConduitInitiatorForUri("http://localhost:9090/CustomerServicePort");
		String transportId = ci.getTransportIds().get(0);

		EndpointInfo ei = new EndpointInfo(si, transportId);
		ei.setBinding(bi);
		ei.setName(portName);
		ei.setAddress("http://localhost:9090/CustomerServicePort");
		si.addEndpoint(ei);

//		BindingOperationInfo boi = bi.getOperation(oi);
//		SoapOperationInfo soi = boi.getExtensor(SoapOperationInfo.class);
//		if (soi == null) {
//			soi = new SoapOperationInfo();
//			boi.addExtensor(soi);
//		}
//		soi.setAction(operationName);
		service.setDataBinding(new SourceDataBinding());

		Endpoint endpoint = new EndpointImpl(bus, service, ei);

		client = new ClientImpl(bus, endpoint);
	}

	@Override
	public Object invoke(Object payload) throws Exception {
		if(payload instanceof org.dom4j.Document) {
			Object[] result = client.invoke(operationName, new org.dom4j.io.DocumentSource(
					(org.dom4j.Document)payload));
			if(result != null) {
				org.dom4j.io.DocumentResult docResult = new org.dom4j.io.DocumentResult();
				factory.newTransformer().transform((Source)result[0], docResult);
				return docResult.getDocument();
			}
			return null;
		} else {
			throw new RuntimeException(
				"Consumer try to send incompatible object: " + payload.getClass().getName());
		}
	}

}
