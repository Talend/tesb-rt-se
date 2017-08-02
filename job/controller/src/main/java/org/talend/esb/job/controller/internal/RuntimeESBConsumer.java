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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;

import org.apache.cxf.Bus;
import org.apache.cxf.BusException;
import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.databinding.source.SourceDataBinding;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.EndpointException;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.frontend.ClientFactoryBean;
import org.apache.cxf.headers.Header;
import org.apache.cxf.service.model.InterfaceInfo;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.ws.policy.WSPolicyFeature;
import org.apache.cxf.wsdl.service.factory.AbstractServiceConfiguration;
import org.talend.esb.job.controller.ESBEndpointConstants;
import org.talend.esb.job.controller.internal.util.DOM4JMarshaller;
import org.talend.esb.policy.correlation.feature.CorrelationIDFeature;
import org.talend.esb.sam.agent.feature.EventFeature;
import org.talend.esb.servicelocator.cxf.LocatorFeature;

import routines.system.api.ESBConsumer;


//@javax.jws.WebService()
public class RuntimeESBConsumer implements ESBConsumer {
    private static final Logger LOG = Logger.getLogger(RuntimeESBConsumer.class.getName());

    private final QName operationName;
    private final List<Header> soapHeaders;
    private AuthorizationPolicy authorizationPolicy;

    private final ClientFactoryBean clientFactory;

    private Client client;

    private boolean enhancedResponse;

    static interface GenericServiceClass {
        Object invoke(Object param);
    }

    RuntimeESBConsumer(final QName serviceName,
            final QName portName,
            final QName operationName,
            String publishedEndpointUrl,
            String wsdlURL,
            final boolean useServiceLocator,
            final LocatorFeature locatorFeature,
            final Map<String, String> locatorProps,
            final EventFeature samFeature,
            final Map<String, String> samProps,
            boolean useServiceRegistry,
            final SecurityArguments securityArguments,
            Bus bus,
            boolean logging,
            final List<Header> soapHeaders,
            final Feature httpHeadersFeature,
            boolean enhancedResponse,
            Object correlationIDCallbackHandler) {
        this.operationName = operationName;
        this.soapHeaders = soapHeaders;
        this.enhancedResponse = enhancedResponse;

        clientFactory = new ClientFactoryBean();
        clientFactory.setServiceClass(GenericServiceClass.class);
        clientFactory.getServiceFactory().getServiceConfigurations().add(0, new AbstractServiceConfiguration() {
            @Override
            public Boolean isOperation(Method method) {
                return "invoke".equals(method.getName());
            }
            @Override
            public QName getOperationName(InterfaceInfo service, Method method) {
                return operationName;
            }
            @Override
            public Boolean isWrapped() {
                return Boolean.FALSE;
            }
        });

        clientFactory.setServiceName(serviceName);
        clientFactory.setEndpointName(portName);
        if (!useServiceRegistry) {
            String endpointUrl = (useServiceLocator) ? "locator://" + serviceName.getLocalPart() : publishedEndpointUrl;
            clientFactory.setAddress(endpointUrl);
        }
        if (!useServiceRegistry && null != wsdlURL) {
            clientFactory.setWsdlURL(wsdlURL);
        }
        clientFactory.setDataBinding(new SourceDataBinding());

        clientFactory.setBus(bus);

        final List<Feature> features = new ArrayList<Feature>();
        if (useServiceLocator) {
            features.add(locatorFeature);
        }
        if (samFeature != null) {
            features.add(samFeature);
        }
        if (correlationIDCallbackHandler != null && (!useServiceRegistry)) {
            features.add(new CorrelationIDFeature());
        }
        if (null != securityArguments.getPolicy()) {
            features.add(new WSPolicyFeature(securityArguments.getPolicy()));
        }
        if (null != httpHeadersFeature){
        	features.add(httpHeadersFeature);
        }
        if (logging) {
            features.add(new org.apache.cxf.feature.LoggingFeature());
        }
        clientFactory.setFeatures(features);

        authorizationPolicy = securityArguments.buildAuthorizationPolicy();

        Map<String, Object> clientProps = securityArguments.buildClientConfig(bus, useServiceRegistry,
                serviceName.toString());

        clientProps.put("soap.no.validate.parts", Boolean.TRUE);

        clientProps.put(ESBEndpointConstants.USE_SERVICE_REGISTRY_PROP, Boolean.toString(useServiceRegistry));

        if ((useServiceLocator || useServiceRegistry) && null != locatorProps && !locatorProps.isEmpty()) {
            clientProps.put(LocatorFeature.LOCATOR_PROPERTIES, locatorProps);
        }

        if (null != samFeature && null != samProps && !samProps.isEmpty()) {
            clientProps.put(EventFeature.SAM_PROPERTIES, samProps);
        }

        if (correlationIDCallbackHandler != null) {
            clientProps.put(CorrelationIDFeature.CORRELATION_ID_CALLBACK_HANDLER, correlationIDCallbackHandler);
        }

        clientFactory.setProperties(clientProps);
        
        LOG.fine("Generic consumer created, serviceName: " + serviceName + 
        		" portName: " +  portName + 
                " operationName: " + operationName + 
                " publishedEndpointUrl: " + publishedEndpointUrl + 
                " wsdlURL: " + wsdlURL);
        LOG.fine("Generic consumer properties: " + clientProps);
     }

    @Override
    public Object invoke(Object payload) throws Exception {
    	
        LOG.fine("Generic consumer for operation " + operationName + " invoked with payload " + payload);
        		    	   	
        if (payload instanceof org.dom4j.Document) {
            return sendDocument((org.dom4j.Document) payload);
        } else if (payload instanceof java.util.Map) {
            Map<?, ?> map = (Map<?, ?>) payload;
            return sendDocument((org.dom4j.Document) map.get(ESBEndpointConstants.REQUEST_PAYLOAD));
        } else {
            throw new RuntimeException("Consumer try to send incompatible object: " + payload.getClass().getName());
        }
    }

    private Object sendDocument(org.dom4j.Document doc) throws Exception {
        Client client = getClient();
        if (null != soapHeaders) {
            LOG.fine("Generic consumer sendDocument soapHeaders: " + Arrays.toString(soapHeaders.toArray()));
            client.getRequestContext().put(org.apache.cxf.headers.Header.HEADER_LIST, soapHeaders);
        }
        Object[] result = null;
        try {
        	result = client.invoke(operationName, DOM4JMarshaller.documentToSource(doc));
        } catch (Exception ex) {
            LOG.fine("Generic consumer client.invoke throwed exception " + ex.getMessage() + 
            		" trace: " + Arrays.toString(ex.getStackTrace()));
        	throw ex;
        }
        if (result != null) {
            org.dom4j.Document response = DOM4JMarshaller.sourceToDocument((Source) result[0]);
            if (enhancedResponse) {
                Map<String, Object> enhancedBody = new HashMap<String, Object>();
                enhancedBody.put("payload", response);
                enhancedBody.put(CorrelationIDFeature.MESSAGE_CORRELATION_ID,
                        client.getResponseContext().get(CorrelationIDFeature.MESSAGE_CORRELATION_ID));
                return enhancedBody;
            } else {
                return response;
            }
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
        }
        return client;
    }

}
