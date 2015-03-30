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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.cxf.Bus;
import org.apache.cxf.BusException;
import org.apache.cxf.binding.soap.SoapFault;
import org.apache.cxf.binding.soap.saaj.SAAJFactoryResolver;
import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.databinding.source.SourceDataBinding;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.EndpointException;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.frontend.ClientFactoryBean;
import org.apache.cxf.headers.Header;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.service.model.InterfaceInfo;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.ws.policy.WSPolicyFeature;
import org.apache.cxf.ws.security.SecurityConstants;
import org.apache.cxf.ws.security.trust.STSClient;
import org.apache.cxf.wsdl.service.factory.AbstractServiceConfiguration;
import org.talend.esb.job.controller.ESBEndpointConstants;
import org.talend.esb.job.controller.ESBEndpointConstants.EsbSecurity;
import org.talend.esb.job.controller.internal.util.DOM4JMarshaller;
import org.talend.esb.policy.correlation.feature.CorrelationIDFeature;
import org.talend.esb.sam.agent.feature.EventFeature;
import org.talend.esb.sam.common.handler.impl.CustomInfoHandler;
import org.talend.esb.security.saml.STSClientUtils;
import org.talend.esb.security.saml.WSPasswordCallbackHandler;
import org.talend.esb.servicelocator.cxf.LocatorFeature;

import routines.system.api.ESBConsumer;


//@javax.jws.WebService()
public class RuntimeESBConsumer implements ESBConsumer {
    private static final Logger LOG = Logger.getLogger(RuntimeESBConsumer.class
            .getName());

    private static final String CONSUMER_SIGNATURE_PASSWORD =
             "ws-security.signature.password";

    private final QName operationName;
    private final EventFeature samFeature;
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
            final boolean isRequestResponse,
            final LocatorFeature slFeature,
            final EventFeature samFeature,
            boolean useServiceRegistry,
            final SecurityArguments securityArguments,
            Bus bus,
            boolean logging,
            final String soapAction,
            final List<Header> soapHeaders,
            boolean enhancedResponse,
            Object correlationIDCallbackHandler) {
        this.operationName = operationName;
        this.samFeature = samFeature;
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
        final String endpointUrl = (slFeature == null) ? publishedEndpointUrl
                : "locator://" + serviceName.getLocalPart();
        if (!useServiceRegistry) {
            clientFactory.setAddress(endpointUrl);
        }
        if (!useServiceRegistry && null != wsdlURL) {
            clientFactory.setWsdlURL(wsdlURL);
        }
        clientFactory.setDataBinding(new SourceDataBinding());

        clientFactory.setBus(bus);
        final List<Feature> features = new ArrayList<Feature>();
        if (slFeature != null) {
            features.add(slFeature);
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
        if (logging) {
            features.add(new org.apache.cxf.feature.LoggingFeature());
        }
        clientFactory.setFeatures(features);

        Map<String, Object> clientProps = new HashMap<String, Object>();
        if (EsbSecurity.BASIC == securityArguments.getEsbSecurity()) {
            authorizationPolicy = new AuthorizationPolicy();
            authorizationPolicy.setUserName(securityArguments.getUsername());
            authorizationPolicy.setPassword(securityArguments.getPassword());
            authorizationPolicy.setAuthorizationType(org.apache.cxf.transport.http.auth.HttpAuthHeader.AUTH_TYPE_BASIC);
        } else if (EsbSecurity.DIGEST == securityArguments.getEsbSecurity()) {
            authorizationPolicy = new AuthorizationPolicy();
            authorizationPolicy.setUserName(securityArguments.getUsername());
            authorizationPolicy.setPassword(securityArguments.getPassword());
            authorizationPolicy.setAuthorizationType(org.apache.cxf.transport.http.auth.HttpAuthHeader.AUTH_TYPE_DIGEST);
        }
        if (EsbSecurity.TOKEN == securityArguments.getEsbSecurity() || useServiceRegistry) {
            clientProps.put(SecurityConstants.USERNAME, securityArguments.getUsername());
            clientProps.put(SecurityConstants.PASSWORD, securityArguments.getPassword());
        }
        if (EsbSecurity.SAML == securityArguments.getEsbSecurity() || useServiceRegistry) {
            Map<String, String> stsProps = new HashMap<String, String>(securityArguments.getStsProperties());
            final STSClient stsClient;
            if (null == securityArguments.getAlias()) {
                stsProps.put(SecurityConstants.USERNAME, securityArguments.getUsername());
                stsProps.put(SecurityConstants.PASSWORD, securityArguments.getPassword());
                stsClient= STSClientUtils.createSTSClient(bus, stsProps);
            } else {
                stsProps.put(SecurityConstants.STS_TOKEN_USERNAME, securityArguments.getAlias());
                stsClient= STSClientUtils.createSTSX509Client(bus, stsProps);
            }

            if (null != securityArguments.getRoleName() && securityArguments.getRoleName().length() != 0) {
                STSClientUtils.applyAuthorization(stsClient, securityArguments.getRoleName());
            }
            if (null != securityArguments.getSecurityToken()) {
                stsClient.setOnBehalfOf(securityArguments.getSecurityToken());
            }

            clientProps.put(SecurityConstants.STS_CLIENT, stsClient);

            Map<String, String> clientPropsDef = securityArguments.getClientProperties();

            for (Map.Entry<String, String> entry : clientPropsDef.entrySet()) {
                if (SecurityConstants.ALL_PROPERTIES.contains(entry.getKey())) {
                    clientProps.put(entry.getKey(), processFileURI(entry.getValue()));
                }
            }
            if (null == securityArguments.getAlias()) {
                clientProps.put(SecurityConstants.CALLBACK_HANDLER,
                        new WSPasswordCallbackHandler(
                            clientPropsDef.get(SecurityConstants.SIGNATURE_USERNAME),
                            clientPropsDef.get(CONSUMER_SIGNATURE_PASSWORD)));
            } else {
                clientProps.put(SecurityConstants.SIGNATURE_USERNAME, securityArguments.getAlias());
                clientProps.put(SecurityConstants.CALLBACK_HANDLER,
                        new WSPasswordCallbackHandler(
                            securityArguments.getAlias(),
                            securityArguments.getPassword()));
            }
            if (null != securityArguments.getCryptoProvider()) {
                clientProps.put(SecurityConstants.ENCRYPT_CRYPTO, securityArguments.getCryptoProvider());
                Object encryptUsername = clientProps.get(SecurityConstants.ENCRYPT_USERNAME);
                if (encryptUsername == null || encryptUsername.toString().isEmpty()) {
                    clientProps.put(SecurityConstants.ENCRYPT_USERNAME, serviceName.toString());
                }
            }
        }

        clientProps.put("soap.no.validate.parts", Boolean.TRUE);
        clientProps.put(ESBEndpointConstants.USE_SERVICE_REGISTRY_PROP,
                Boolean.toString(useServiceRegistry));
        if (correlationIDCallbackHandler != null) {
            clientProps.put(
                CorrelationIDFeature.CORRELATION_ID_CALLBACK_HANDLER, correlationIDCallbackHandler);
        }
        clientFactory.setProperties(clientProps);
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
            Object[] result = client.invoke(operationName, DOM4JMarshaller.documentToSource(doc));
            if (result != null) {
                org.dom4j.Document response = DOM4JMarshaller.sourceToDocument((Source) result[0]);
                if(enhancedResponse) {
                    Map<String, Object> enhancedBody = new HashMap<String, Object>();
                    enhancedBody.put("payload", response);
                    enhancedBody.put(CorrelationIDFeature.MESSAGE_CORRELATION_ID, client.getResponseContext().get(CorrelationIDFeature.MESSAGE_CORRELATION_ID));
                    return enhancedBody;
                } else {
                    return response;
                }
            }
        } catch (org.apache.cxf.binding.soap.SoapFault e) {
            SOAPFault soapFault = createSoapFault(e);
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
        }
        return client;
    }

    private static Object processFileURI(String fileURI) {
        if (fileURI.startsWith("file:")) {
            try {
                return new URL(fileURI);
            } catch (MalformedURLException e) {
            }
        }
        return fileURI;
    }

    // org.apache.cxf.jaxws.JaxWsClientProxy
    private static SOAPFault createSoapFault(Exception ex) throws SOAPException {
        SOAPFault soapFault = SAAJFactoryResolver.createSOAPFactory(null).createFault(); 
        if (ex instanceof SoapFault) {
            if (!soapFault.getNamespaceURI().equals(((SoapFault)ex).getFaultCode().getNamespaceURI())
                && SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE
                    .equals(((SoapFault)ex).getFaultCode().getNamespaceURI())) {
                //change to 1.1
                try {
                    soapFault = SAAJFactoryResolver.createSOAPFactory(null).createFault();
                } catch (Throwable t) {
                    //ignore
                }
            }
            soapFault.setFaultString(((SoapFault)ex).getReason());
            soapFault.setFaultCode(((SoapFault)ex).getFaultCode());
            soapFault.setFaultActor(((SoapFault)ex).getRole());
            if (((SoapFault)ex).getSubCode() != null) {
                soapFault.appendFaultSubcode(((SoapFault)ex).getSubCode());
            }

            if (((SoapFault)ex).hasDetails()) {
                org.w3c.dom.Node nd = soapFault.getOwnerDocument().importNode(((SoapFault)ex).getDetail(),
                                                                  true);
                nd = nd.getFirstChild();
                soapFault.addDetail();
                while (nd != null) {
                    org.w3c.dom.Node next = nd.getNextSibling();
                    soapFault.getDetail().appendChild(nd);
                    nd = next;
                }
            }
        } else {
            String msg = ex.getMessage();
            if (msg != null) {
                soapFault.setFaultString(msg);
            }
        }      
        return soapFault;
    }

}
