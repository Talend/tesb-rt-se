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

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.cxf.Bus;
import org.apache.cxf.common.injection.NoJSR250Annotations;
import org.apache.cxf.headers.Header;
import org.apache.neethi.Policy;
import org.talend.esb.job.controller.ESBEndpointConstants;
import org.talend.esb.job.controller.ESBEndpointConstants.EsbSecurity;
import org.talend.esb.job.controller.ESBEndpointConstants.OperationStyle;
import org.talend.esb.job.controller.PolicyProvider;
import org.talend.esb.sam.agent.feature.EventFeature;
import org.talend.esb.servicelocator.cxf.LocatorFeature;
import org.w3c.dom.Node;

import routines.system.api.ESBConsumer;
import routines.system.api.ESBEndpointInfo;
import routines.system.api.ESBEndpointRegistry;

@NoJSR250Annotations(unlessNull = "bus") 
public class RuntimeESBEndpointRegistry implements ESBEndpointRegistry {

    private static final Logger LOG = Logger.getLogger(RuntimeESBEndpointRegistry.class.getName());

    private Bus bus;
    private EventFeature samFeature;
    private PolicyProvider policyProvider;
    private Map<String, String> clientProperties;
    private Map<String, String> stsProperties;
//    private static final String HTTPS_CONFIG = "https.config";

    @javax.annotation.Resource
    public void setBus(Bus bus) {
        this.bus = bus;
    }

    public void setSamFeature(EventFeature samFeature) {
        this.samFeature = samFeature;
    }

    public void setPolicyProvider(PolicyProvider policyProvider) {
        this.policyProvider = policyProvider;
    }

    public void setClientProperties(Map<String, String> clientProperties) {
        this.clientProperties = clientProperties;
    }

    public void setStsProperties(Map<String, String> stsProperties) {
        this.stsProperties = stsProperties;
    }

    @Override
    public ESBConsumer createConsumer(ESBEndpointInfo endpoint) {
        final Map<String, Object> props = endpoint.getEndpointProperties();

        final QName serviceName = QName.valueOf((String) props
                .get(ESBEndpointConstants.SERVICE_NAME));
        final QName portName = QName.valueOf((String) props
                .get(ESBEndpointConstants.PORT_NAME));
        final String operationName = (String) props
                .get(ESBEndpointConstants.DEFAULT_OPERATION_NAME);

        final String publishedEndpointUrl = (String) props
                .get(ESBEndpointConstants.PUBLISHED_ENDPOINT_URL);
        boolean useServiceLocator = ((Boolean) props
                .get(ESBEndpointConstants.USE_SERVICE_LOCATOR)).booleanValue();
        boolean useServiceActivityMonitor = ((Boolean) props
                .get(ESBEndpointConstants.USE_SERVICE_ACTIVITY_MONITOR))
                .booleanValue();
        boolean useServiceRegistry = ((Boolean) props
                .get(ESBEndpointConstants.USE_SERVICE_REGISTRY)).booleanValue();
        boolean logMessages = false;
        if (null != props.get(ESBEndpointConstants.LOG_MESSAGES)) {
            logMessages = ((Boolean) props.get(ESBEndpointConstants.LOG_MESSAGES)).booleanValue();
        }
        //for future HTTPS checking
//      boolean useHTTPS = ((Boolean) props
//                .get(ESBEndpointConstants.USE_HTTPS))
//                .booleanValue();
//     boolean useHTTPS = publishedEndpointUrl.startsWith("https://");
        
//        if (useHTTPS) {
//            Bus currentBus = BusFactory.getThreadDefaultBus();
//            SpringBusFactory bf = new SpringBusFactory();
//            this.bus = bf.createBus(clientProperties.get(HTTPS_CONFIG));   
//                if (useServiceLocator) {
//                    ServiceLocatorManager slm = currentBus.getExtension(ServiceLocatorManager.class);
//                    bus.setExtension(slm, ServiceLocatorManager.class);
//                }
//        }
        
        LocatorFeature slFeature = null;
        if (useServiceLocator) {
            slFeature = new LocatorFeature();
            //pass SL custom properties to Consumer
            Object slProps = props.get(ESBEndpointConstants.REQUEST_SL_PROPS);
            if (slProps != null) {
                slFeature.setRequiredEndpointProperties((Map<String, String>)slProps);
            }
        }

        String wsdlURL = null;
        if (useServiceRegistry) {
            wsdlURL = clientProperties.get("registry.url") + "/rest/" + serviceName + "/WSDL";
            //to do: process policies
        }else {
            wsdlURL = (String) props.get(ESBEndpointConstants.WSDL_URL);
        }

        final EsbSecurity esbSecurity = EsbSecurity.fromString((String) props
                .get(ESBEndpointConstants.ESB_SECURITY));
        Policy policy = null;
        if (EsbSecurity.TOKEN == esbSecurity) {
            policy = policyProvider.getTokenPolicy();
        } else if (EsbSecurity.SAML == esbSecurity) {
            policy = policyProvider.getSamlPolicy();
        }

        List<Header> soapHeaders = null;
        Object soapHeadersDoc = props.get(ESBEndpointConstants.SOAP_HEADERS);
        if (null != soapHeadersDoc) {
            soapHeaders = new java.util.ArrayList<Header>();
            try {
                javax.xml.transform.dom.DOMResult result = new javax.xml.transform.dom.DOMResult();
                javax.xml.transform.TransformerFactory.newInstance().newTransformer().transform(
                    new org.dom4j.io.DocumentSource((org.dom4j.Document) soapHeadersDoc), result);
                for (Node node = ((org.w3c.dom.Document) result.getNode())
                            .getDocumentElement().getFirstChild();
                        node != null;
                        node = node.getNextSibling()) {
                    if (org.w3c.dom.Node.ELEMENT_NODE == node.getNodeType()) {
                        soapHeaders.add(new org.apache.cxf.headers.Header(
                            new javax.xml.namespace.QName(node.getNamespaceURI(), node.getLocalName()),
                            node));
                    }
                }
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Uncaught exception during SOAP headers transformation: ", e);
            }
        }
        final SecurityArguments securityArguments = new SecurityArguments(
                esbSecurity,
                policy,
                (String) props.get(ESBEndpointConstants.USERNAME),
                (String) props.get(ESBEndpointConstants.PASSWORD),
                clientProperties,
                stsProperties);
        return new RuntimeESBConsumer(
                serviceName, portName, operationName, publishedEndpointUrl, wsdlURL,
                OperationStyle.isRequestResponse((String) props
                        .get(ESBEndpointConstants.COMMUNICATION_STYLE)),
                slFeature,
                useServiceActivityMonitor ? samFeature : null,
                securityArguments,
                bus,
                logMessages,
                (String) props.get(ESBEndpointConstants.SOAPACTION),
                soapHeaders);
    }

}
