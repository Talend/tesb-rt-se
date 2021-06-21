/*
 * #%L
 * Service Locator Client for CXF
 * %%
 * Copyright (c) 2006-2021 Talend Inc. - www.talend.com
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
package org.talend.esb.servicelocator.cxf.internal;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.ClientLifeCycleListener;
import org.apache.cxf.endpoint.ClientLifeCycleManager;
import org.apache.cxf.endpoint.ConduitSelector;
import org.apache.cxf.endpoint.ConduitSelectorHolder;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.interceptor.InterceptorProvider;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;
import org.talend.esb.servicelocator.client.SLProperties;
import org.talend.esb.servicelocator.client.SLPropertiesImpl;
import org.talend.esb.servicelocator.client.SLPropertiesMatcher;
import org.talend.esb.servicelocator.cxf.LocatorFeature;

@OsgiServiceProvider(classes=LocatorFeature.class)
@Named("locatorFeature")
@Singleton
public class LocatorFeatureImpl extends AbstractFeature implements LocatorFeature {

    private static final Logger LOG = Logger.getLogger(LocatorFeatureImpl.class.getName());

    @Inject
    LocatorRegistrar locatorRegistrar;

    @Inject
    LocatorClientEnabler clientEnabler;

    @Override
    public void initialize(Bus bus) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Initializing Locator feature for bus " + bus);
        }

        locatorRegistrar.startListenForServers(bus);
        ClientLifeCycleManager clcm = bus.getExtension(ClientLifeCycleManager.class);
        clcm.registerListener(new ClientLifeCycleListenerForLocator());
    }

    @Override
    public void initialize(Client client, Bus bus) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Initializing locator feature for bus " + bus + " and client " + client);
        }

        String selectionStrategy = (String)client.getEndpoint().get(KEY_STRATEGY);
        Map<String, String> endpointProps = getEndpointLocatorProperties(client.getEndpoint());
        final Client client1 = client;
        clientEnabler.enable(client1, createMatcher(endpointProps), selectionStrategy);
    }

    @Override
    public void initialize(Server server, Bus bus) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Initializing locator feature for bus " + bus + " and server " + server);
        }

        Map<String, String> endpointProps = getEndpointLocatorProperties(server.getEndpoint());
        locatorRegistrar.registerServer(server, createProperties(endpointProps), bus);
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

    public void initialize(ConduitSelectorHolder conduitSelectorHolder, Bus bus) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Initializing locator feature for bus " + bus + " and client configuration"
                    + conduitSelectorHolder);
        }


        ConduitSelector conduitSelector = conduitSelectorHolder.getConduitSelector();
        String selectionStrategy = (String)conduitSelector.getEndpoint().get(KEY_STRATEGY);
        Map<String, String> endpointProps = getEndpointLocatorProperties(conduitSelector.getEndpoint());

        SLPropertiesMatcher slPropsMatcher = createMatcher(endpointProps);
        final ConduitSelectorHolder conduitSelectorHolder1 = conduitSelectorHolder;
        clientEnabler.enable(conduitSelectorHolder1, slPropsMatcher, selectionStrategy);
    }

    private SLPropertiesMatcher createMatcher(Map<String, String> properties) {
        SLPropertiesMatcher slPropsMatcher = new SLPropertiesMatcher();
        if (properties == null) {
            return slPropsMatcher;
        }
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
        return slPropsMatcher;
    }

    private SLProperties createProperties(Map<String, String> properties) {
        SLPropertiesImpl slProps = new SLPropertiesImpl();
        if (properties != null) {
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                slProps.addProperty(entry.getKey(), tokenize(entry.getValue()));
            }
        }
        return slProps;
    }

    Collection<String> tokenize(String valueList) {
        return Arrays.asList(valueList.split(","));
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> getEndpointLocatorProperties(Endpoint endpoint) {
        return (Map<String, String>) endpoint.get(LOCATOR_PROPERTIES);
    }

    class ClientLifeCycleListenerForLocator implements ClientLifeCycleListener {

        @Override
        public void clientCreated(Client client) {
            clientEnabler.enable(client, null, null);
        }

        @Override
        public void clientDestroyed(Client client) {
        }
    }

    public void setLocatorRegistrar(LocatorRegistrar locatorRegistrar) {
        this.locatorRegistrar = locatorRegistrar;
    }

    public void setClientEnabler(LocatorClientEnabler clientEnabler) {
        this.clientEnabler = clientEnabler;
    }
}
