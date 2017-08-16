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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.extension.Extension;
import org.apache.cxf.bus.extension.ExtensionManager;
import org.apache.cxf.bus.extension.ExtensionManagerImpl;
import org.apache.cxf.bus.extension.ExtensionRegistry;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.headers.Header;
import org.apache.cxf.service.factory.FactoryBeanListenerManager;
import org.apache.neethi.Policy;
import org.apache.wss4j.common.crypto.Crypto;
import org.talend.esb.job.controller.ESBEndpointConstants;
import org.talend.esb.job.controller.ESBEndpointConstants.EsbSecurity;
import org.talend.esb.policy.correlation.feature.CorrelationIDFeature;
import org.talend.esb.sam.agent.feature.EventFeature;
import org.talend.esb.security.policy.PolicyProvider;
import org.talend.esb.servicelocator.cxf.LocatorFeature;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import routines.system.api.ESBConsumer;
import routines.system.api.ESBEndpointInfo;
import routines.system.api.ESBEndpointRegistry;

public class RuntimeESBEndpointRegistry implements ESBEndpointRegistry {

    private static final Logger LOG = Logger.getLogger(RuntimeESBEndpointRegistry.class.getName());

    private static final String WSDL_CLIENT_EXTENSION_NAME =
            "org.talend.esb.registry.client.wsdl.RegistryFactoryBeanListener";

    private static final String POLICY_CLIENT_EXTENSION_NAME =
            "org.talend.esb.registry.client.policy.RegistryFactoryBeanListener";

    private Bus bus;
    private EventFeature samFeature;
    private LocatorFeature locatorFeature;
    private PolicyProvider policyProvider;
    private Map<String, String> clientProperties;
    private Crypto cryptoProvider;

    public void setBus(Bus bus) {
        this.bus = bus;
    }

    public void setSamFeature(EventFeature samFeature) {
        this.samFeature = samFeature;
    }
    
    public void setLocatorFeature(LocatorFeature locatorFeature) {
        this.locatorFeature = locatorFeature;
    }

    public void setPolicyProvider(PolicyProvider policyProvider) {
        this.policyProvider = policyProvider;
    }

    public void setClientProperties(Map<String, String> clientProperties) {
        this.clientProperties = clientProperties;
    }

    public void setCryptoProvider(Crypto cryptoProvider) {
        this.cryptoProvider = cryptoProvider;
    }

    @Override
    public ESBConsumer createConsumer(ESBEndpointInfo endpoint) {
        final Map<String, Object> props = endpoint.getEndpointProperties();

        boolean useServiceRegistry = getBoolean(props, ESBEndpointConstants.USE_SERVICE_REGISTRY);

        final String authorizationRole = (String) props.get(ESBEndpointConstants.AUTHZ_ROLE);
        boolean useCrypto = getBoolean(props, ESBEndpointConstants.USE_CRYPTO);
        final EsbSecurity esbSecurity = EsbSecurity.fromString((String) props.get(ESBEndpointConstants.ESB_SECURITY));
        Policy policy = buildSecurePolicy(authorizationRole, useCrypto, esbSecurity);
        final SecurityArguments securityArguments = new SecurityArguments(
                esbSecurity,
                policy,
                (String) props.get(ESBEndpointConstants.USERNAME),
                (String) props.get(ESBEndpointConstants.PASSWORD),
                (String) props.get(ESBEndpointConstants.ALIAS),
                clientProperties,
                authorizationRole,
                props.get(ESBEndpointConstants.SECURITY_TOKEN),
                (useCrypto || useServiceRegistry) ? cryptoProvider : null);

        List<Header> soapHeaders = listSoapHeaders(props.get(ESBEndpointConstants.SOAP_HEADERS));

        if (useServiceRegistry) {
            ensureServiceRegistryAvailable(bus);
        }

        final QName serviceName = QName.valueOf((String) props.get(ESBEndpointConstants.SERVICE_NAME));
        String operationNamespace = (String) props.get(ESBEndpointConstants.OPERATION_NAMESPACE);
        final QName operationName = new QName(
                (null == operationNamespace) ? serviceName.getNamespaceURI() : operationNamespace,
                (String) props.get(ESBEndpointConstants.DEFAULT_OPERATION_NAME));

        return new RuntimeESBConsumer(
                serviceName,
                QName.valueOf((String) props.get(ESBEndpointConstants.PORT_NAME)),
                operationName,
                (String) props.get(ESBEndpointConstants.PUBLISHED_ENDPOINT_URL),
                (String) props.get(ESBEndpointConstants.WSDL_URL),
                getBoolean(props, ESBEndpointConstants.USE_SERVICE_LOCATOR),
                locatorFeature,
                (Map<String, String>) props.get(ESBEndpointConstants.REQUEST_SL_PROPS),
                getBoolean(props, ESBEndpointConstants.USE_SERVICE_ACTIVITY_MONITOR) ? samFeature : null,
                (Map<String, String>) props.get(ESBEndpointConstants.REQUEST_SAM_PROPS),
                useServiceRegistry,
                securityArguments,
                bus,
                getBoolean(props, ESBEndpointConstants.LOG_MESSAGES),
                soapHeaders,
                (Feature)props.get("httpHeadersFeature"),
                getBoolean(props, ESBEndpointConstants.ENHANCED_RESPONSE),
                props.get(CorrelationIDFeature.CORRELATION_ID_CALLBACK_HANDLER));
    }

    private Policy buildSecurePolicy(final String authorizationRole, boolean useCrypto, final EsbSecurity esbSecurity) {
        Policy policy = null;
        if (EsbSecurity.TOKEN == esbSecurity) {
            policy = policyProvider.getUsernamePolicy(bus);
        } else if (EsbSecurity.SAML == esbSecurity) {
            if (null != authorizationRole) {
                if (useCrypto) {
                    policy = policyProvider.getSAMLAuthzCryptoPolicy(bus);
                } else {
                    policy = policyProvider.getSAMLAuthzPolicy(bus);
                }
            } else {
                if (useCrypto) {
                    policy = policyProvider.getSAMLCryptoPolicy(bus);
                } else {
                    policy = policyProvider.getSAMLPolicy(bus);
                }
            }
        }
        return policy;
    }

    @SuppressWarnings("unchecked")
    private List<Header> listSoapHeaders(Object soapHeadersObject) throws TransformerFactoryConfigurationError {
        if (null != soapHeadersObject) {
            if (soapHeadersObject instanceof org.dom4j.Document) {
                List<Header> soapHeaders = new ArrayList<Header>();
                try {
                    DOMResult result = new DOMResult();
                    TransformerFactory.newInstance().newTransformer()
                            .transform(new org.dom4j.io.DocumentSource((org.dom4j.Document) soapHeadersObject), result);
                    for (Node node = ((Document) result.getNode()).getDocumentElement()
                            .getFirstChild(); node != null; node = node.getNextSibling()) {
                        if (Node.ELEMENT_NODE == node.getNodeType()) {
                            soapHeaders.add(new Header(new QName(node.getNamespaceURI(), node.getLocalName()), node));
                        }
                    }
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, "Uncaught exception during SOAP headers transformation: ", e);
                }
            } else if (soapHeadersObject instanceof List) {
                return (List<Header>) soapHeadersObject;
            }
        }
        return null;
    }

    private static boolean getBoolean(Map<String, Object> props, String propName) {
        Object propValue = props.get(propName);
        return (null == propValue) ? false : (Boolean) propValue;
    }

    private static void ensureServiceRegistryAvailable(Bus bus) {
        //for TESB-9006, update extensions when registry enabled but no wsdl-client/policy-client
        //extension set on the old bus. (used to instead the action of refresh job controller bundle.
        if (!bus.hasExtensionByName(WSDL_CLIENT_EXTENSION_NAME)
                        || !bus.hasExtensionByName(POLICY_CLIENT_EXTENSION_NAME)) {

            boolean updated = false;
            Map<String, Extension> exts = ExtensionRegistry.getRegisteredExtensions();

            updated |= setExtensionOnBusIfMissing(bus, exts, WSDL_CLIENT_EXTENSION_NAME);
            updated |= setExtensionOnBusIfMissing(bus, exts, POLICY_CLIENT_EXTENSION_NAME);

            if (updated) {
                // this should cause FactoryBeanListenerManager to refresh its list of event listeners
                FactoryBeanListenerManager fblm = bus.getExtension(FactoryBeanListenerManager.class);
                if (fblm != null) {
                    fblm.setBus(bus);
                } else {
                    throw new RuntimeException("CXF bus doesn't contain FactoryBeanListenerManager.");
                }
            }
        }
    }

    private static boolean setExtensionOnBusIfMissing(Bus bus, Map<String, Extension> exts, String extensionName) {
        if (exts.containsKey(extensionName) && !bus.hasExtensionByName(extensionName)) {
            ExtensionManager extMan = bus.getExtension(ExtensionManager.class);
            if (extMan instanceof ExtensionManagerImpl) {
                ((ExtensionManagerImpl) extMan).add(exts.get(extensionName));
                return true;
            } else {
                throw new RuntimeException(
                        "A required extension '"
                                + extensionName
                                + "' is not loaded on the CXF bus used by Job Controller. "
                                + "In the same time, the bus uses unknown implementation of ExtensionManager, "
                                + "so it is not possible to set the extension automatically.");
            }
        }
        return false;
    }
}
