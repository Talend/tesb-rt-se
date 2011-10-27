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
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.jaxws.JaxWsClientFactoryBean;
import org.apache.cxf.service.Service;
import org.apache.cxf.service.model.InterfaceInfo;
import org.apache.cxf.service.model.ServiceInfo;
import org.apache.cxf.ws.policy.WSPolicyFeature;
import org.apache.cxf.ws.security.SecurityConstants;
import org.apache.cxf.ws.security.trust.STSClient;

import routines.system.api.ESBConsumer;

import org.talend.esb.job.controller.ESBEndpointConstants.EsbSecurity;
import org.talend.esb.job.controller.internal.util.DOM4JMarshaller;
import org.talend.esb.job.controller.internal.util.ServiceHelper;
import org.talend.esb.sam.agent.feature.EventFeature;
import org.talend.esb.sam.common.handler.impl.CustomInfoHandler;
import org.talend.esb.servicelocator.cxf.LocatorFeature;


//@javax.jws.WebService()
public class RuntimeESBConsumer implements ESBConsumer {
    private static final Logger LOG = Logger.getLogger(RuntimeESBConsumer.class
            .getName());

    private static final String STS_WSDL_LOCATION = "sts.wsdl.location";
    private static final String STS_NAMESPACE = "sts.namespace";
    private static final String STS_SERVICE_NAME = "sts.service.name";
    private static final String STS_ENDPOINT_NAME = "sts.endpoint.name";
    private static final String CONSUMER_SIGNATURE_PASSWORD = "ws-security.signature.password";
    
    private final QName serviceName;
    private final QName portName;
    private final String operationName;
    private final String publishedEndpointUrl;
    private final boolean isRequestResponse;
    private final LocatorFeature slFeature;
    private final EventFeature samFeature;
    private final Map<String, String> slProps;
    private final SecurityArguments securityArguments;
    private final Bus bus;

    public RuntimeESBConsumer(final QName serviceName, 
            final QName portName,
            String operationName, 
            String publishedEndpointUrl,
            boolean isRequestResponse, 
            final LocatorFeature slFeature,
            final EventFeature samFeature,
            final Map<String, String> slProps,
            final SecurityArguments securityArguments, 
            final Bus bus) {
        this.serviceName = serviceName;
        this.portName = portName;
        this.operationName = operationName;
        this.publishedEndpointUrl = publishedEndpointUrl;
        this.isRequestResponse = isRequestResponse;
        this.slFeature = slFeature;
        this.samFeature = samFeature;
        this.slProps = slProps;
        this.securityArguments = securityArguments;
        this.bus = bus;
    }

    @Override
    public Object invoke(Object payload) throws Exception {
        if (payload instanceof org.dom4j.Document) {
            return sendDocument((org.dom4j.Document) payload);
        } else if (payload instanceof java.util.Map<?,?>) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) payload;

            @SuppressWarnings("unchecked")
            Map<String, String> samProps = (Map<String, String>) map
                    .get(ESBProviderBase.REQUEST_SAM_PROPS);
            if (samProps != null && samFeature != null) {
                LOG.info("SAM custom properties received: " + samProps);
                CustomInfoHandler ciHandler = new CustomInfoHandler();
                ciHandler.setCustomInfo(samProps);
                samFeature.setHandler(ciHandler);
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
        } finally {
            client.destroy();
        }
        return null;
    }

    private Client createClient() throws BusException, EndpointException {
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
        final String endpointUrl = (slFeature == null) ? publishedEndpointUrl
                : "locator://" + serviceName.getLocalPart();
        cf.setAddress(endpointUrl);
        cf.setServiceClass(this.getClass());
        cf.setBus(bus);
        final List<AbstractFeature> features = new ArrayList<AbstractFeature>();
        if (slFeature != null) {
            if (slProps != null){
                slFeature.setRequiredEndpointProperties(slProps);
            }
            features.add(slFeature);
        }
        if (samFeature != null) {
            features.add(samFeature);
        }
        if (null != securityArguments.getPolicy()) {
            features.add(new WSPolicyFeature(securityArguments.getPolicy()));
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
                Map<String, String> sp = securityArguments.getStsProperties();

                STSClient stsClient = new STSClient(bus);
                stsClient.setWsdlLocation(sp.get(STS_WSDL_LOCATION));
                stsClient.setServiceQName(new QName(sp.get(STS_NAMESPACE), sp
                        .get(STS_SERVICE_NAME)));
                stsClient.setEndpointQName(new QName(sp.get(STS_NAMESPACE), sp
                        .get(STS_ENDPOINT_NAME)));

                Map<String, Object> stsProperties = new HashMap<String, Object>();

                for (Map.Entry<String, String> entry : sp.entrySet()) {
                    if (SecurityConstants.ALL_PROPERTIES.contains(entry.getKey())) {
                        stsProperties.put(entry.getKey(), entry.getValue());
                    }
                }

                stsProperties.put(SecurityConstants.USERNAME,
                        securityArguments.getUsername());
                stsProperties.put(SecurityConstants.PASSWORD,
                        securityArguments.getPassword());
                stsClient.setProperties(stsProperties);

                Map<String, Object> cfProperties = new HashMap<String, Object>();
                cfProperties.put(SecurityConstants.STS_CLIENT, stsClient);

                Map<String, String> sprop = securityArguments
                        .getClientProperties();

                for (Map.Entry<String, String> entry : sprop.entrySet()) {
                    if (SecurityConstants.ALL_PROPERTIES.contains(entry.getKey())) {
                        cfProperties.put(entry.getKey(), entry.getValue());
                    }
                }
                cfProperties.put(SecurityConstants.CALLBACK_HANDLER,
                        new WSPasswordCallbackHandler(
                                sprop.get(SecurityConstants.SIGNATURE_USERNAME),
                                sprop.get(CONSUMER_SIGNATURE_PASSWORD)));

                cf.setProperties(cfProperties);
            }
        }

        Client client = cf.create();

        final Service service = client.getEndpoint().getService();
        service.setDataBinding(new SourceDataBinding());

        final ServiceInfo si = service.getServiceInfos().get(0);
        ServiceHelper.addOperation(si, operationName, isRequestResponse);

        return client;
    }

}
