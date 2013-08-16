/*
 * #%L
 * Talend :: ESB :: Job :: Controller
 * %%
 * Copyright (C) 2011 - 2012 Talend Inc.
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPFault;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.cxf.Bus;
import org.apache.cxf.BusException;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.databinding.source.SourceDataBinding;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.endpoint.EndpointException;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.frontend.ClientFactoryBean;
import org.apache.cxf.headers.Header;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.jaxws.JaxWsClientFactoryBean;
import org.apache.cxf.message.Message;
import org.apache.cxf.service.Service;
import org.apache.cxf.service.model.InterfaceInfo;
import org.apache.cxf.service.model.ServiceInfo;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.ws.policy.WSPolicyFeature;
import org.apache.cxf.ws.security.SecurityConstants;
import org.apache.cxf.ws.security.trust.STSClient;
import org.apache.cxf.wsdl.EndpointReferenceUtils;
import org.dom4j.Document;
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
    private final String soapAction;
    private final List<Header> soapHeaders;
    private AuthorizationPolicy authorizationPolicy;

    private final ClientFactoryBean clientFactory;

    private Client client;

	private boolean enhancedResponse;

    RuntimeESBConsumer(final QName serviceName, 
            final QName portName,
            String operationName, 
            String publishedEndpointUrl,
            String wsdlURL,
            boolean isRequestResponse, 
            final LocatorFeature slFeature,
            final EventFeature samFeature,
            boolean useServiceRegistry,
            final SecurityArguments securityArguments, 
            Bus bus,
            boolean logging,
            String soapAction,
            final List<Header> soapHeaders,
            boolean enhancedResponse) {
        this.operationName = operationName;
        this.isRequestResponse = isRequestResponse;
        this.samFeature = samFeature;
        this.soapAction = soapAction;
        this.soapHeaders = soapHeaders;
        this.enhancedResponse = enhancedResponse;

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
        if (null != wsdlURL) {
            clientFactory.setWsdlURL(wsdlURL);
        }
        clientFactory.setServiceClass(this.getClass());

        //for TESB-9006, create new bus when registry enabled but no wsdl-client/policy-client
        //extension set on the old bus. (used to instead the action of refresh job controller bundle.
        if (useServiceRegistry && !hasRegistryClientExtension(bus)) {
            SpringBusFactory sbf = new SpringBusFactory();
            bus = sbf.createBus();
        }

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

        if (EsbSecurity.BASIC == securityArguments.getEsbSecurity()) {
            authorizationPolicy = new AuthorizationPolicy();
            authorizationPolicy.setUserName(securityArguments.getUsername());
            authorizationPolicy.setPassword(securityArguments.getPassword());
            authorizationPolicy.setAuthorizationType("Basic");
        } else if (EsbSecurity.DIGEST == securityArguments.getEsbSecurity()) {
            authorizationPolicy = new AuthorizationPolicy();
            authorizationPolicy.setUserName(securityArguments.getUsername());
            authorizationPolicy.setPassword(securityArguments.getPassword());
            authorizationPolicy.setAuthorizationType("Digest");
        } else if (EsbSecurity.TOKEN == securityArguments.getEsbSecurity()) {
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
                    stsProps.put(entry.getKey(), processFileURI(entry.getValue()));
                }
            }

            stsProps.put(SecurityConstants.USERNAME,
                    securityArguments.getUsername());
            stsProps.put(SecurityConstants.PASSWORD,
                    securityArguments.getPassword());
            stsClient.setProperties(stsProps);

            if (null != securityArguments.getRoleName() && securityArguments.getRoleName().length() != 0) {
                ClaimValueCallbackHandler roleCallbackHandler = new ClaimValueCallbackHandler();
                roleCallbackHandler.setClaimValue(securityArguments.getRoleName());
                stsClient.setClaimsCallbackHandler(roleCallbackHandler);
            }
            if (null != securityArguments.getSecurityToken()) {
                stsClient.setOnBehalfOf(securityArguments.getSecurityToken());
            }

            Map<String, Object> clientProps = new HashMap<String, Object>();
            clientProps.put(SecurityConstants.STS_CLIENT, stsClient);

            Map<String, String> clientPropsDef =
                securityArguments.getClientProperties();

            for (Map.Entry<String, String> entry : clientPropsDef.entrySet()) {
                if (SecurityConstants.ALL_PROPERTIES.contains(entry.getKey())) {
                    clientProps.put(entry.getKey(), processFileURI(entry.getValue()));
                }
            }
            clientProps.put(SecurityConstants.CALLBACK_HANDLER,
                    new WSPasswordCallbackHandler(
                        clientPropsDef.get(SecurityConstants.SIGNATURE_USERNAME),
                        clientPropsDef.get(CONSUMER_SIGNATURE_PASSWORD)));

            clientFactory.setProperties(clientProps);
        }

        if (useServiceRegistry) {
            Map<String, Object> clientProps = new HashMap<String, Object>();

            //add properties for Username Token
            clientProps.put(SecurityConstants.USERNAME,
                    securityArguments.getUsername());
            clientProps.put(SecurityConstants.PASSWORD,
                    securityArguments.getPassword());

            //add properties for SAML Token
            final Map<String, String> stsPropsDef = securityArguments.getStsProperties();

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
                    stsProps.put(entry.getKey(), processFileURI(entry.getValue()));
                }
            }

            stsProps.put(SecurityConstants.USERNAME,
                    securityArguments.getUsername());
            stsProps.put(SecurityConstants.PASSWORD,
                    securityArguments.getPassword());
            stsClient.setProperties(stsProps);

            if (null != securityArguments.getRoleName() && securityArguments.getRoleName().length() != 0) {
                ClaimValueCallbackHandler roleCallbackHandler = new ClaimValueCallbackHandler();
                roleCallbackHandler.setClaimValue(securityArguments.getRoleName());
                stsClient.setClaimsCallbackHandler(roleCallbackHandler);
            }

            clientProps.put(SecurityConstants.STS_CLIENT, stsClient);

            Map<String, String> clientPropsDef =
                securityArguments.getClientProperties();

            for (Map.Entry<String, String> entry : clientPropsDef.entrySet()) {
                if (SecurityConstants.ALL_PROPERTIES.contains(entry.getKey())) {
                    clientProps.put(entry.getKey(), processFileURI(entry.getValue()));
                }
            }
            clientProps.put(SecurityConstants.CALLBACK_HANDLER,
                    new WSPasswordCallbackHandler(
                        clientPropsDef.get(SecurityConstants.SIGNATURE_USERNAME),
                        clientPropsDef.get(CONSUMER_SIGNATURE_PASSWORD)));

            clientFactory.setProperties(clientProps);
        }

        clientFactory.getProperties(true).put("soap.no.validate.parts", Boolean.TRUE);
        clientFactory.getProperties(true).put(ESBEndpointConstants.USE_SERVICE_REGISTRY_PROP, 
                Boolean.toString(useServiceRegistry));
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
        Client client = getClient();
        if (null != soapHeaders) {
            client.getRequestContext().put(org.apache.cxf.headers.Header.HEADER_LIST, soapHeaders);
        }

        try {
            //workaround for CXF-5169
            Object svObj = client.getRequestContext().get(Message.SCHEMA_VALIDATION_ENABLED);
            if (svObj instanceof String && ((String) svObj).equalsIgnoreCase("OUT")) {
                //Service service = ServiceModelUtil.getService(message.getExchange());
                Schema schema = EndpointReferenceUtils.getSchema(client.getEndpoint().getService().getServiceInfos().get(0),
                        client.getBus());
                if (null != schema) {
                    schema.newValidator().validate(DOM4JMarshaller.documentToSource(doc));
                }
            }

            Object[] result = client.invoke(operationName, DOM4JMarshaller.documentToSource(doc));
            if (result != null) {
            	Document payload = DOM4JMarshaller.sourceToDocument((Source) result[0]);
            	if(enhancedResponse) {
            		Map<String, Object> enhancedBody = new HashMap<String, Object>();
            		enhancedBody.put("payload", payload);
            		enhancedBody.put(Client.REQUEST_CONTEXT, client.getRequestContext());
            		enhancedBody.put(Client.RESPONSE_CONTEXT, client.getResponseContext());
            		return enhancedBody;
            	}else {
            		return payload;
				}
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

    private Client getClient() throws BusException, EndpointException {
        if (client == null) {
            client = clientFactory.create();

            if (null != authorizationPolicy) {
                HTTPConduit conduit = (HTTPConduit) client.getConduit();
                conduit.setAuthorization(authorizationPolicy);
            }

            final Service service = client.getEndpoint().getService();
            service.setDataBinding(new SourceDataBinding());

            final ServiceInfo si = service.getServiceInfos().get(0);
            ServiceHelper.addOperation(si, operationName, isRequestResponse, soapAction);
        }
        return client;
    }

    private boolean hasRegistryClientExtension(Bus bus) {
        return (bus.hasExtensionByName("org.talend.esb.registry.client.wsdl.RegistryFactoryBeanListener")
            || bus.hasExtensionByName("org.talend.esb.registry.client.policy.RegistryFactoryBeanListener"));
    }

    private Object processFileURI(String fileURI) {
        if (fileURI.startsWith("file:")) {
            try {
                return new URL(fileURI);
            } catch (MalformedURLException e) {
            }
        }
        return fileURI;
    }

}
