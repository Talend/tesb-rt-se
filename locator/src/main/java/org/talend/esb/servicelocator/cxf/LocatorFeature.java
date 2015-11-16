/*
 * #%L
 * Service Locator Client for CXF
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
package org.talend.esb.servicelocator.cxf;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.ConduitSelectorHolder;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.interceptor.InterceptorProvider;
import org.talend.esb.servicelocator.client.SLPropertiesImpl;
import org.talend.esb.servicelocator.client.SLPropertiesMatcher;
import org.talend.esb.servicelocator.cxf.internal.ServiceLocatorManager;

public class LocatorFeature extends AbstractFeature implements LocatorFeatureInterface {

    private static final Logger LOG = Logger.getLogger(LocatorFeature.class.getName());

    private SLPropertiesImpl slProps;

    private SLPropertiesMatcher slPropsMatcher;

    private String selectionStrategy;

    public static final String LOCATOR_PROPERTIES = "esb.locator.properties";

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

        Map<String, String> endpointProps = getEndpointLocatorProperties(client.getEndpoint());
        if (null != endpointProps) {
            setRequiredEndpointProperties(endpointProps);
        }

        ServiceLocatorManager slm = bus.getExtension(ServiceLocatorManager.class);
        slm.enableClient(client, slPropsMatcher, selectionStrategy);
    }

    @Override
    public void initialize(Server server, Bus bus) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Initializing locator feature for bus " + bus + " and server " + server);
        }

        Map<String, String> endpointProps = getEndpointLocatorProperties(server.getEndpoint());
        if (null != endpointProps) {
            setAvailableEndpointProperties(endpointProps);
        }

        ServiceLocatorManager slm = bus.getExtension(ServiceLocatorManager.class);
        slm.registerServer(server, slProps, bus);
    }

    @Override
    public void initialize(InterceptorProvider interceptorProvider, Bus bus) {
        if (interceptorProvider instanceof ConduitSelectorHolder) {
            initialize((ConduitSelectorHolder) interceptorProvider, bus);
        } else {
            if (LOG.isLoggable(Level.WARNING)) {
                LOG.log(Level.WARNING,
                        "Tried to initialize locator feature with unknown interceptor provider "
                                + interceptorProvider);
            }
        }
    }

    void initialize(ConduitSelectorHolder conduitSelectorHolder, Bus bus) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Initializing locator feature for bus " + bus + " and client configuration"
                    + conduitSelectorHolder);
        }

        Map<String, String> endpointProps = getEndpointLocatorProperties(
                conduitSelectorHolder.getConduitSelector().getEndpoint());
        if (null != endpointProps) {
            setRequiredEndpointProperties(endpointProps);
        }

        ServiceLocatorManager slm = bus.getExtension(ServiceLocatorManager.class);
        slm.enableClient(conduitSelectorHolder, slPropsMatcher, selectionStrategy);
    }

    protected ServiceLocatorManager getLocatorManager(Bus bus) {
        return bus.getExtension(ServiceLocatorManager.class);
    }

    @Override
    public void setAvailableEndpointProperties(Map<String, String> properties) {
        slProps = new SLPropertiesImpl();

        for (Map.Entry<String, String> entry : properties.entrySet()) {
            slProps.addProperty(entry.getKey(), tokenize(entry.getValue()));
        }
    }

    @Override
    public void setRequiredEndpointProperties(Map<String, String> properties) {
        slPropsMatcher = new SLPropertiesMatcher();

        if (LOG.isLoggable(Level.FINE)) {
            StringBuilder sb = new StringBuilder();
            for (String prop: properties.keySet()) {
                sb.append(prop + " -> ");
                sb.append(properties.get(prop) + "\n");
            }
        }

        for (Map.Entry<String, String> entry : properties.entrySet()) {
            for (String value : tokenize(entry.getValue())) {
                slPropsMatcher.addAssertion(entry.getKey(), value);
            }
        }

        LOG.fine("set matcher = " + slPropsMatcher.toString());
        for (StackTraceElement trace : new Throwable().getStackTrace()) {
            LOG.fine(trace.toString());
        }
    }

    @Override
    public void setSelectionStrategy(String selectionStrategy) {
        this.selectionStrategy = selectionStrategy;
    }

    Collection<String> tokenize(String valueList) {
        return Arrays.asList(valueList.split(","));
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> getEndpointLocatorProperties(Endpoint endpoint) {
        return (Map<String, String>) endpoint.get(LOCATOR_PROPERTIES);
    }

}
