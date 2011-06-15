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
import java.util.List;

import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;

import org.apache.cxf.BusException;
import org.apache.cxf.databinding.source.SourceDataBinding;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.EndpointException;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.jaxws.JaxWsClientFactoryBean;
import org.apache.cxf.service.Service;
import org.apache.cxf.service.model.InterfaceInfo;
import org.apache.cxf.service.model.ServiceInfo;

import routines.system.api.ESBConsumer;

@WebService()
public class RuntimeESBConsumer implements ESBConsumer {

	private final QName serviceName;
	private final QName portName;
	private final String operationName;
	private final String publishedEndpointUrl;
	private final boolean isRequestResponse;
	private final AbstractFeature serviceLocator;
	private final AbstractFeature serviceActivityMonitoring;

	public RuntimeESBConsumer(
			final QName serviceName,
			final QName portName,
			String operationName,
			String publishedEndpointUrl,
			boolean isRequestResponse,
			final AbstractFeature serviceLocator,
			final AbstractFeature serviceActivityMonitoring) {
		this.serviceName = serviceName;
		this.portName = portName;
		this.operationName = operationName;
		this.publishedEndpointUrl = publishedEndpointUrl;
		this.isRequestResponse = isRequestResponse;
		this.serviceLocator = serviceLocator;
		this.serviceActivityMonitoring = serviceActivityMonitoring;
	}

	@Override
	public Object invoke(Object payload) throws Exception {
		if(payload instanceof org.dom4j.Document) {
			Client client = createClient();
			Object[] result = client.invoke(operationName, new org.dom4j.io.DocumentSource(
					(org.dom4j.Document)payload));
			if(result != null) {
				org.dom4j.io.DocumentResult docResult = new org.dom4j.io.DocumentResult();
				javax.xml.transform.TransformerFactory.newInstance().
					newTransformer().transform((Source)result[0], docResult);
				return docResult.getDocument();
			}
			return null;
		} else {
			throw new RuntimeException(
				"Consumer try to send incompatible object: " + payload.getClass().getName());
		}
	}

	private Client createClient() throws BusException, EndpointException {

		final JaxWsClientFactoryBean cf = new JaxWsClientFactoryBean();
		cf.setServiceName(serviceName);
		cf.setEndpointName(portName);
		String endpointUrl =
			(serviceLocator == null)
				? publishedEndpointUrl
				: "locator://" + serviceName.getLocalPart();
		cf.setAddress(endpointUrl);
		cf.setServiceClass(this.getClass());
		List<AbstractFeature> features = new ArrayList<AbstractFeature>();
		if(serviceLocator != null) {
			features.add(serviceLocator);
		}
		if(serviceActivityMonitoring != null) {
			features.add(serviceActivityMonitoring);
		}
		cf.setFeatures(features);

		final Client client = cf.create();
		
		final Service service = client.getEndpoint().getService();
        service.setDataBinding(new SourceDataBinding());

        final ServiceInfo si = service.getServiceInfos().get(0);
		// fix namespace
		InterfaceInfo ii = si.getInterface();
		QName name = ii.getName();
		ii.setName(new QName(serviceName.getNamespaceURI(), name.getLocalPart()));

        ESBProvider.addOperation(si,
				operationName, isRequestResponse);

		return client;
	}

}
