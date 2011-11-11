/*
 * #%L
 * Service Locator Client for CXF
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
package org.talend.esb.servicelocator.cxf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.interceptor.InterceptorProvider;
import org.apache.cxf.jaxrs.client.ClientConfiguration;
import org.talend.esb.servicelocator.client.SLPropertiesImpl;
import org.talend.esb.servicelocator.client.SLPropertiesMatcher;
import org.talend.esb.servicelocator.cxf.internal.ServiceLocatorManager;

/**
 * CXF feature to enable the locator client with an CXF service.
 * 
 */
public class LocatorFeature extends AbstractFeature {

    private static final Logger LOG = Logger.getLogger(LocatorFeature.class.getName());

    private SLPropertiesImpl slProps;

    private SLPropertiesMatcher slPropsMatcher;

    private String selectionStrategy;

    @Override
    public void initialize(Bus bus) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Initializing Locator feature for bus " + bus);
        }

        ServiceLocatorManager slm = bus.getExtension(ServiceLocatorManager.class);
        slm.listenForAllServers(bus);
        slm.listenForAllClients();

    }

    @Override
    public void initialize(Client client, Bus bus) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Initializing locator feature for bus " + bus + " and client " + client);
        }

        ServiceLocatorManager slm = bus.getExtension(ServiceLocatorManager.class);
        slm.enableClient(client, slPropsMatcher, selectionStrategy);
    }

    @Override
    public void initialize(Server server, Bus bus) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Initializing locator feature for bus " + bus + " and server " + server);
        }

        ServiceLocatorManager slm = bus.getExtension(ServiceLocatorManager.class);
        slm.registerServer(server, slProps, bus);
    }

    @Override
    public void initialize(InterceptorProvider interceptorProvider, Bus bus) {
        if (interceptorProvider instanceof ClientConfiguration) {
            initialize((ClientConfiguration) interceptorProvider, bus);
        } else {
            if (LOG.isLoggable(Level.WARNING)) {
                LOG.log(Level.WARNING,
                        "Tried to initialize locator feature with unknown interceptor provider "
                                + interceptorProvider);
            }
        }
    }

    public void initialize(ClientConfiguration clientConf, Bus bus) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Initializing locator feature for bus " + bus + " and client configuration"
                    + clientConf);
        }

        ServiceLocatorManager slm = bus.getExtension(ServiceLocatorManager.class);
        slm.enableClient(clientConf, slPropsMatcher, selectionStrategy);
    }

    protected ServiceLocatorManager getLocatorManager(Bus bus) {
        return bus.getExtension(ServiceLocatorManager.class);
    }

    /**
     * 
     * 
     * @param properties
     */
    public void setAvailableEndpointProperties(Map<String, String> properties) {
        slProps = new SLPropertiesImpl();

        for (String key : properties.keySet()) {
            String valueList = properties.get(key);
            List<String> values = tokenize(valueList);
            slProps.addProperty(key, values);
        }
    }

    public void setRequiredEndpointProperties(Map<String, String> properties) {
        slPropsMatcher = new SLPropertiesMatcher();

        for (String key : properties.keySet()) {
            String valueList = properties.get(key);
            List<String> values = tokenize(valueList);
            for (String value : values) {
                slPropsMatcher.addAssertion(key, value);
            }
        }
    }

    public void setSelectionStrategy(String selectionStrategy) {
        this.selectionStrategy = selectionStrategy;
    }

    List<String> tokenize(String valueList) {
        List<String> normalizedValues = new ArrayList<String>();
        String[] values = valueList.split(",");

        for (String value : values) {
            normalizedValues.add(value.trim());
        }
        return normalizedValues;
    }

}