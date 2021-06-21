/*
 * ============================================================================
 *
 * Copyright (c) 2006-2021 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 5/7 rue Salomon De Rothschild, 92150 Suresnes, France
 *
 * ============================================================================
 */
package org.talend.esb.auxiliary.storage.rest.security;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cxf.Bus;
import org.apache.cxf.common.injection.NoJSR250Annotations;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.endpoint.ServerRegistry;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.blueprint.JAXRSServerFactoryBeanDefinitionParser.BPJAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.security.JAASAuthenticationFilter;
import org.apache.cxf.rs.security.saml.SamlHeaderInHandler;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.ws.security.SecurityConstants;

@NoJSR250Annotations(unlessNull = "bus")
public class AuxiliaryStorageRestServiceSecurityProvider extends AbstractRestSecurityProvider {

    private Object signatureProperties;
    private String signatureUsername;
    private String signaturePassword;

    private JAXRSServerFactoryBean server;

    public void init() {

        if (Authentication.NO == auxiliaryStorageAuthentication) {
            return;
        }

        // TODO: !!! find more correct way to enable/switch(?) security on provider endpoint
         Bus serverBus = server.getBus();
         ServerRegistry registry = serverBus.getExtension(ServerRegistry.class);
         List<Server> servers = registry.getServers();
         for (Server sr : servers) {
             EndpointInfo ei = sr.getEndpoint().getEndpointInfo();
             if (null != ei && ei.getAddress().endsWith(server.getAddress())){
                 registry.unregister(sr);
                 sr.destroy();
             }
         }

        @SuppressWarnings("unchecked")
        List<Object> providers = (List<Object>) server.getProviders();

        if (Authentication.BASIC == auxiliaryStorageAuthentication) {
            JAASAuthenticationFilter jaasAuthFilter = new JAASAuthenticationFilter();
            jaasAuthFilter.setContextName("karaf");

            providers.add(jaasAuthFilter);
            server.setProviders(providers);
        }

        if (Authentication.SAML == auxiliaryStorageAuthentication) {
            Map<String, Object> endpointProps = new HashMap<String, Object>();

            endpointProps.put(SecurityConstants.SIGNATURE_PROPERTIES, signatureProperties);
            endpointProps.put(SecurityConstants.SIGNATURE_USERNAME, signatureUsername);
            endpointProps.put(SecurityConstants.SIGNATURE_PASSWORD, signaturePassword);
            endpointProps.put(SecurityConstants.CALLBACK_HANDLER,
                    new WSPasswordCallbackHandler(signatureUsername, signaturePassword));

            Map<String, Object> properties = server.getProperties();
            if (null == properties) {
                properties = new HashMap<String, Object>();
            }
            properties.putAll(endpointProps);
            server.setProperties(properties);

            SamlHeaderInHandler samlHandler = new SamlHeaderInHandler();

            providers.add(samlHandler);
            server.setProviders(providers);
        }

        // First we have to destroy the cached server that was created automatically by blueprint
        if (server instanceof BPJAXRSServerFactoryBean) {
            ((BPJAXRSServerFactoryBean)server).destroy();
        }

        server.create();
    }

    public void setSignatureProperties(Object signatureProperties) {
        this.signatureProperties = signatureProperties;
    }

    public void setSignatureUsername(String signatureUsername) {
        this.signatureUsername = signatureUsername;
    }

    public void setSignaturePassword(String signaturePassword) {
        this.signaturePassword = signaturePassword;
    }

    public void setAuxiliaryStorageEndpoint(JAXRSServerFactoryBean server) {
        this.server = server;
    }

}
