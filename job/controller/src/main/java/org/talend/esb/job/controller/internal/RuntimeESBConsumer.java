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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

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
import org.apache.cxf.frontend.ClientFactoryBean;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.jaxws.JaxWsClientFactoryBean;
import org.apache.cxf.service.Service;
import org.apache.cxf.service.model.InterfaceInfo;
import org.apache.cxf.service.model.ServiceInfo;
import org.apache.cxf.ws.policy.WSPolicyFeature;
import org.apache.cxf.ws.security.SecurityConstants;
import org.apache.cxf.ws.security.trust.STSClient;
import org.talend.esb.job.controller.ESBEndpointConstants;
import org.talend.esb.job.controller.ESBEndpointConstants.EsbSecurity;
import org.talend.esb.job.controller.internal.util.DOM4JMarshaller;
import org.talend.esb.job.controller.internal.util.ServiceHelper;
import org.talend.esb.sam.agent.feature.EventFeature;
import org.talend.esb.sam.common.handler.impl.CustomInfoHandler;
import org.talend.esb.servicelocator.cxf.LocatorFeature;

import routines.system.api.ESBConsumer;


//@javax.jws.WebService()
public class RuntimeESBConsumer implements ESBConsumer {
    private static final Logger LOG = Logger.getLogger(RuntimeESBConsumer.class
            .getName());

    private static final String STS_WSDL_LOCATION = "sts.wsdl.location";
    private static final String STS_NAMESPACE = "sts.namespace";
    private static final String STS_SERVICE_NAME = "sts.service.name";
    private static final String STS_ENDPOINT_NAME = "sts.endpoint.name";
    private static final String CONSUMER_SIGNATURE_PASSWORD =
             "ws-security.signature.password";

    private final String operationName;
    private final boolean isRequestResponse;
    private final EventFeature samFeature;

    private final ClientFactoryBean clientFactory;

    public RuntimeESBConsumer(final QName serviceName, 
            final QName portName,
            String operationName, 
            String publishedEndpointUrl,
            boolean isRequestResponse, 
            final LocatorFeature slFeature,
            final EventFeature samFeature,
            final SecurityArguments securityArguments, 
            final Bus bus,
            boolean logging) {
        this.operationName = operationName;
        this.isRequestResponse = isRequestResponse;
        this.samFeature = samFeature;

        clientFactory = new JaxWsClientFactoryBean() {
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
        clientFactory.setServiceName(serviceName);
        clientFactory.setEndpointName(portName);
        final String endpointUrl = (slFeature == null) ? publishedEndpointUrl
                : "locator://" + serviceName.getLocalPart();
        clientFactory.setAddress(endpointUrl);
        clientFactory.setServiceClass(this.getClass());
        clientFactory.setBus(bus);
        final List<AbstractFeature> features = new ArrayList<AbstractFeature>();
        if (slFeature != null) {
            features.add(slFeature);
        }
        if (samFeature != null) {
            features.add(samFeature);
        }
        if (null != securityArguments.getPolicy()) {
            features.add(new WSPolicyFeature(securityArguments.getPolicy()));
        }
        if (logging) {
            features.add(new org.apache.cxf.feature.LoggingFeature());
        }

        clientFactory.setFeatures(features);

        if (EsbSecurity.TOKEN == securityArguments.getEsbSecurity()) {
            Map<String, Object> properties = new HashMap<String, Object>(2);
            properties.put(SecurityConstants.USERNAME,
                    securityArguments.getUsername());
            properties.put(SecurityConstants.PASSWORD,
                    securityArguments.getPassword());
            clientFactory.setProperties(properties);
        } else if (EsbSecurity.SAML == securityArguments.getEsbSecurity()) {
            final Map<String, String> stsPropsDef =
                securityArguments.getStsProperties();

            final STSClient stsClient = new STSClient(bus);
            stsClient.setWsdlLocation(stsPropsDef.get(STS_WSDL_LOCATION));
            stsClient.setServiceQName(
                new QName(stsPropsDef.get(STS_NAMESPACE),
                    stsPropsDef.get(STS_SERVICE_NAME)));
            stsClient.setEndpointQName(
                new QName(stsPropsDef.get(STS_NAMESPACE),
                    stsPropsDef.get(STS_ENDPOINT_NAME)));

            Map<String, Object> stsProps = new HashMap<String, Object>();

            for (Map.Entry<String, String> entry : stsPropsDef.entrySet()) {
                if (SecurityConstants.ALL_PROPERTIES.contains(entry.getKey())) {
                    stsProps.put(entry.getKey(), entry.getValue());
                }
            }

            stsProps.put(SecurityConstants.USERNAME,
                    securityArguments.getUsername());
            stsProps.put(SecurityConstants.PASSWORD,
                    securityArguments.getPassword());
            stsClient.setProperties(stsProps);

            Map<String, Object> clientProps = new HashMap<String, Object>();
            clientProps.put(SecurityConstants.STS_CLIENT, stsClient);

            Map<String, String> clientPropsDef =
                securityArguments.getClientProperties();

            for (Map.Entry<String, String> entry : clientPropsDef.entrySet()) {
                if (SecurityConstants.ALL_PROPERTIES.contains(entry.getKey())) {
                    clientProps.put(entry.getKey(), entry.getValue());
                }
            }
            clientProps.put(SecurityConstants.CALLBACK_HANDLER,
                    new WSPasswordCallbackHandler(
                        clientPropsDef.get(SecurityConstants.SIGNATURE_USERNAME),
                        clientPropsDef.get(CONSUMER_SIGNATURE_PASSWORD)));

            clientFactory.setProperties(clientProps);
        }

    }

    @Override
    @SuppressWarnings("unchecked")
    public Object invoke(Object payload) throws Exception {
        if (payload instanceof org.dom4j.Document) {
            return sendDocument((org.dom4j.Document) payload);
        } else if (payload instanceof java.util.Map) {
            Map<?, ?> map = (Map<?, ?>) payload;

            if (samFeature != null) {
                Object samProps = map.get(ESBEndpointConstants.REQUEST_SAM_PROPS);
                if (samProps != null) {
                    LOG.info("SAM custom properties received: " + samProps);
                    CustomInfoHandler ciHandler = new CustomInfoHandler();
                    ciHandler.setCustomInfo((Map<String, String>)samProps);
                    samFeature.setHandler(ciHandler);
                }
            }

            return sendDocument((org.dom4j.Document) map
                    .get(ESBEndpointConstants.REQUEST_PAYLOAD));
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
        } finally {
            client.destroy();
        }
        return null;
    }

    private Client createClient() throws BusException, EndpointException {
        Client client = clientFactory.create();

        final Service service = client.getEndpoint().getService();
        service.setDataBinding(new SourceDataBinding());

        final ServiceInfo si = service.getServiceInfos().get(0);
        ServiceHelper.addOperation(si, operationName, isRequestResponse);

        return client;
    }

}
