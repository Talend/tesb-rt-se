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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPFault;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.cxf.Bus;
import org.apache.cxf.BusException;
import org.apache.cxf.databinding.source.SourceDataBinding;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.endpoint.EndpointException;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.jaxws.JaxWsClientFactoryBean;
import org.apache.cxf.service.Service;
import org.apache.cxf.service.model.InterfaceInfo;
import org.apache.cxf.service.model.ServiceInfo;
import org.apache.cxf.ws.policy.WSPolicyFeature;
import org.apache.cxf.ws.security.SecurityConstants;
import org.apache.cxf.ws.security.trust.STSClient;
import org.apache.ws.security.WSPasswordCallback;
import org.talend.esb.job.controller.ESBEndpointConstants.EsbSecurity;
import org.talend.esb.job.controller.internal.util.DOM4JMarshaller;
import org.talend.esb.job.controller.internal.util.ServiceHelper;
import org.talend.esb.sam.agent.feature.EventFeature;
import org.talend.esb.sam.common.handler.impl.CustomInfoHandler;

import routines.system.api.ESBConsumer;

//@javax.jws.WebService()
public class RuntimeESBConsumer implements ESBConsumer {
	private static final Logger LOG = Logger.getLogger(RuntimeESBConsumer.class
			.getName());

	private static final String STS_WSDL_LOCATION = "sts.wsdl.location";
	private static final String STS_NAMESPACE = "sts.namespace";
	private static final String STS_SERVICE_NAME = "sts.service.name";
	private static final String STS_ENDPOINT_NAME = "sts.endpoint.name";

	private final QName serviceName;
	private final QName portName;
	private final String operationName;
	private final String publishedEndpointUrl;
	private final boolean isRequestResponse;
	private final AbstractFeature serviceLocator;
	private final EventFeature eventFeature;
	private final SecurityArguments securityArguments;
	private final Bus bus;

	private Client client;

	public RuntimeESBConsumer(final QName serviceName, final QName portName,
			String operationName, String publishedEndpointUrl,
			boolean isRequestResponse, final AbstractFeature serviceLocator,
			final EventFeature eventFeature,
			final SecurityArguments securityArguments, final Bus bus) {
		this.serviceName = serviceName;
		this.portName = portName;
		this.operationName = operationName;
		this.publishedEndpointUrl = publishedEndpointUrl;
		this.isRequestResponse = isRequestResponse;
		this.serviceLocator = serviceLocator;
		this.eventFeature = eventFeature;
		this.securityArguments = securityArguments;
		this.bus = bus;
	}

	@Override
	public Object invoke(Object payload) throws Exception {
		if (payload instanceof org.dom4j.Document) {
			return sendDocument((org.dom4j.Document) payload);
		} else if (payload instanceof java.util.Map) {
			@SuppressWarnings("unchecked")
			java.util.Map<String, Object> map = (java.util.Map<String, Object>) payload;

			@SuppressWarnings("unchecked")
			java.util.Map<String, String> samProps = (java.util.Map<String, String>) map
					.get(ESBProviderBase.REQUEST_SAM_PROPS);
			if (samProps != null && eventFeature != null) {
				LOG.info("SAM custom properties received: " + samProps);
				CustomInfoHandler ciHandler = new CustomInfoHandler();
				ciHandler.setCustomInfo(samProps);
				eventFeature.setHandler(ciHandler);
			}

			return sendDocument((org.dom4j.Document) map
					.get(ESBProviderBase.REQUEST_PAYLOAD));
		} else {
			throw new RuntimeException(
					"Consumer try to send incompatible object: "
							+ payload.getClass().getName());
		}
	}

	private Object sendDocument(org.dom4j.Document doc) throws Exception {
		Client client = createClient();
		try {
			Object[] result = client.invoke(operationName,
					DOM4JMarshaller.documentToSource(doc));
			if (result != null) {
				return DOM4JMarshaller.sourceToDocument((Source) result[0]);
			}
		} catch (org.apache.cxf.binding.soap.SoapFault e) {
			SOAPFault soapFault = ServiceHelper.createSoapFault(e);
			if (soapFault == null) {
				throw new WebServiceException(e);
			}
			SOAPFaultException exception = new SOAPFaultException(soapFault);
			if (e instanceof Fault && e.getCause() != null) {
				exception.initCause(e.getCause());
			} else {
				exception.initCause(e);
			}
			throw exception;
		}
		return null;
	}

	private Client createClient() throws BusException, EndpointException {
		if (client != null) {
			return client;
		}
		final JaxWsClientFactoryBean cf = new JaxWsClientFactoryBean() {
			@Override
			protected Endpoint createEndpoint() throws BusException,
					EndpointException {
				final Endpoint endpoint = super.createEndpoint();
				// set portType = serviceName
				InterfaceInfo ii = endpoint.getService().getServiceInfos()
						.get(0).getInterface();
				ii.setName(serviceName);
				return endpoint;
			}
		};
		cf.setServiceName(serviceName);
		cf.setEndpointName(portName);
		final String endpointUrl = (serviceLocator == null) ? publishedEndpointUrl
				: "locator://" + serviceName.getLocalPart();
		cf.setAddress(endpointUrl);
		cf.setServiceClass(this.getClass());
		cf.setBus(bus);
		List<AbstractFeature> features = new ArrayList<AbstractFeature>();
		if (serviceLocator != null) {
			features.add(serviceLocator);
		}
		if (eventFeature != null) {
			features.add(eventFeature);
		}
		cf.setFeatures(features);

		if (EsbSecurity.NO != securityArguments.getEsbSecurity()) {
			if (EsbSecurity.TOKEN == securityArguments.getEsbSecurity()) {
				Map<String, Object> properties = new HashMap<String, Object>(2);
				properties.put(SecurityConstants.USERNAME,
						securityArguments.getUsername());
				properties.put(SecurityConstants.PASSWORD,
						securityArguments.getPassword());
				cf.setProperties(properties);
			} else if (EsbSecurity.SAML == securityArguments.getEsbSecurity()) {
				Map<String, String> sp = securityArguments.getSTSProperties();

				STSClient stsClient = new STSClient(bus);
				stsClient.setWsdlLocation(sp.get(STS_WSDL_LOCATION));
				stsClient.setServiceQName(new QName(sp.get(STS_NAMESPACE), sp
						.get(STS_SERVICE_NAME)));
				stsClient.setEndpointQName(new QName(sp.get(STS_NAMESPACE), sp
						.get(STS_ENDPOINT_NAME)));

				Set<String> all_props = SecurityConstants.ALL_PROPERTIES;
				Map<String, Object> stsProperties = new HashMap<String, Object>();

				for (Map.Entry<String, String> entry : sp.entrySet()) {
					if (all_props.contains(entry.getKey())) {
						stsProperties.put(entry.getKey(), entry.getValue());
					}
				}

				stsProperties.put(SecurityConstants.USERNAME,
				securityArguments.getUsername());
				 
				// stsProperties.put(SecurityConstants.STS_TOKEN_USERNAME,
				// "myclientkey");
				// stsProperties.put(
				// SecurityConstants.STS_TOKEN_USE_CERT_FOR_KEYINFO,
				// "true");
				// stsProperties.put(SecurityConstants.IS_BSP_COMPLIANT,
				// "false");
				// stsProperties.put(SecurityConstants.STS_TOKEN_PROPERTIES,
				// "clientKeystore.properties");
				// stsProperties.put(SecurityConstants.ENCRYPT_USERNAME,
				// "mystskey");
				// stsProperties.put(SecurityConstants.ENCRYPT_PROPERTIES,
				// "clientKeystore.properties");
				stsProperties.put(SecurityConstants.CALLBACK_HANDLER,
						new CallbackHandler() {
							@Override
							public void handle(Callback[] callbacks)
									throws IOException,
									UnsupportedCallbackException {
								for (int i = 0; i < callbacks.length; i++) {
									if (callbacks[i] instanceof WSPasswordCallback) { // CXF
										WSPasswordCallback pc = (WSPasswordCallback) callbacks[i];
										if (securityArguments.getUsername()
												.equals(pc.getIdentifier())) {
											pc.setPassword(securityArguments
													.getPassword());
											break;
										}
									}
								}
							}
						});
				stsClient.setProperties(stsProperties);

				Map<String, Object> cfProperties = new HashMap<String, Object>();
				cfProperties.put(SecurityConstants.STS_CLIENT, stsClient);

				Map<String, String> sprop = securityArguments
						.getSecurityProperties();

				for (Map.Entry<String, String> entry : sprop.entrySet()) {
					if (all_props.contains(entry.getKey())) {
						cfProperties.put(entry.getKey(), entry.getValue());
					}
				}

				// cfProperties.put(SecurityConstants.SIGNATURE_PROPERTIES,
				// "clientKeystore.properties");
				// cfProperties.put(SecurityConstants.SIGNATURE_USERNAME,
				// "myclientkey");
				// cfProperties.put(SecurityConstants.ENCRYPT_PROPERTIES,
				// "clientKeystore.properties");
				// cfProperties.put(SecurityConstants.ENCRYPT_USERNAME,
				// "myservicekey");
				cfProperties.put(SecurityConstants.CALLBACK_HANDLER,
						new CallbackHandler() {
							@Override
							public void handle(Callback[] callbacks)
									throws IOException,
									UnsupportedCallbackException {
								for (int i = 0; i < callbacks.length; i++) {
									if (callbacks[i] instanceof WSPasswordCallback) { // CXF
										WSPasswordCallback pc = (WSPasswordCallback) callbacks[i];
										if (securityArguments.getUsername()
												.equals(pc.getIdentifier())) {
											pc.setPassword(securityArguments
													.getPassword());
											break;
										}
									}
								}
							}
						});
				cf.setProperties(cfProperties);
			}
		}

		client = cf.create();

		final Service service = client.getEndpoint().getService();
		service.setDataBinding(new SourceDataBinding());

		final ServiceInfo si = service.getServiceInfos().get(0);
		ServiceHelper.addOperation(si, operationName, isRequestResponse);

		if (null != securityArguments.getPolicy()) {
			WSPolicyFeature feature = new WSPolicyFeature(securityArguments.getPolicy());
			feature.initialize(client, bus);
		}

		return client;
	}

	public void destroy() {
		if (client != null) {
			client.destroy();
		}
	}

}
