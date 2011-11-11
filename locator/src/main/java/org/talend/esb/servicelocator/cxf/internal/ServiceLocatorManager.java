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
package org.talend.esb.servicelocator.cxf.internal;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.ClientLifeCycleListener;
import org.apache.cxf.endpoint.ClientLifeCycleManager;
import org.apache.cxf.endpoint.ConduitSelector;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.extension.BusExtension;
import org.apache.cxf.jaxrs.client.ClientConfiguration;
import org.talend.esb.servicelocator.client.SLProperties;
import org.talend.esb.servicelocator.client.SLPropertiesMatcher;
import org.talend.esb.servicelocator.cxf.internal.LocatorClientEnabler.ConduitSelectorHolder;

public class ServiceLocatorManager implements BusExtension {

    private LocatorRegistrar locatorRegistrar;

    private LocatorClientEnabler clientEnabler;

    private Bus bus;

    public void listenForAllServers(Bus bus) {
        locatorRegistrar.startListenForServers(bus);
    }

    public void registerServer(Server server, Bus bus) {
        locatorRegistrar.registerServer(server, bus);
    }

    public void registerServer(Server server, SLProperties props, Bus bus) {
        locatorRegistrar.registerServer(server, props, bus);
    }

    public void listenForAllClients() {
        listenForAllClients(null);
    }

    public void listenForAllClients(String selectionStrategy) {
        ClientLifeCycleManager clcm = bus.getExtension(ClientLifeCycleManager.class);
        clcm.registerListener(new ClientLifeCycleListenerForLocator());
    }

    public void enableClient(Client client) {
        enableClient(client, null);
    }

    public void enableClient(final Client client, SLPropertiesMatcher matcher) {
        enableClient(client, matcher, null);
    }

    public void enableClient(final Client client, SLPropertiesMatcher matcher, String selectionStrategy) {
        clientEnabler.enable(new ConduitSelectorHolder() {
            
            @Override
            public void setConduitSelector(ConduitSelector selector) {
                client.setConduitSelector(selector);
            }
            
            @Override
            public ConduitSelector getConduitSelector() {
                return client.getConduitSelector();
            }
        }, matcher, selectionStrategy);
    }
    
    public void enableClient(ClientConfiguration clientConf) {
        enableClient(clientConf, null);
    }

    public void enableClient(final ClientConfiguration clientConf, SLPropertiesMatcher matcher) {
        enableClient(clientConf, matcher, null);
    }

    public void enableClient(final ClientConfiguration clientConfiguration,
            SLPropertiesMatcher matcher,
            String selectionStrategy) {
        clientEnabler.enable(new ConduitSelectorHolder() {
            
            @Override
            public void setConduitSelector(ConduitSelector selector) {
                clientConfiguration.setConduitSelector(selector);
            }
            
            @Override
            public ConduitSelector getConduitSelector() {
                return clientConfiguration.getConduitSelector();
            }
        }, matcher, selectionStrategy);
    }
    
    public void setBus(Bus bus) {
        if (bus != this.bus) {
            this.bus = bus;
            if (bus != null) {
                bus.setExtension(this, ServiceLocatorManager.class);
            }
        }
    }

    public void setLocatorRegistrar(LocatorRegistrar locatorRegistrar) {
        this.locatorRegistrar = locatorRegistrar;
    }

    public void setLocatorClientEnabler(LocatorClientEnabler clientEnabler) {
        this.clientEnabler = clientEnabler;
    }

    @Override
    public Class<?> getRegistrationType() {
        return ServiceLocatorManager.class;
    }

    class ClientLifeCycleListenerForLocator implements ClientLifeCycleListener {

        @Override
        public void clientCreated(Client client) {
            enableClient(client);
        }

        @Override
        public void clientDestroyed(Client client) {
        }
    }
}
