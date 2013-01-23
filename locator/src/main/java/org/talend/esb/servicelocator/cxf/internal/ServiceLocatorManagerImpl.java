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
package org.talend.esb.servicelocator.cxf.internal;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.ClientLifeCycleListener;
import org.apache.cxf.endpoint.ClientLifeCycleManager;
import org.apache.cxf.endpoint.ConduitSelector;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.client.ClientConfiguration;
import org.talend.esb.servicelocator.client.SLProperties;
import org.talend.esb.servicelocator.client.SLPropertiesMatcher;
import org.talend.esb.servicelocator.cxf.ServiceLocatorManager;
import org.talend.esb.servicelocator.cxf.internal.LocatorClientEnabler.ConduitSelectorHolder;

public class ServiceLocatorManagerImpl implements ServiceLocatorManager /*, BusExtension*/ {

    private LocatorRegistrar locatorRegistrar;

    private LocatorClientEnabler clientEnabler;

    @Override
    public void listenForAllServers(Bus anotherBus) {
        locatorRegistrar.startListenForServers(anotherBus);
    }

    @Override
    public void registerServer(Server server, Bus anotherBus) {
        locatorRegistrar.registerServer(server, anotherBus);
    }

    @Override
    public void registerServer(Server server, SLProperties props, Bus anotherBus) {
        locatorRegistrar.registerServer(server, props, anotherBus);
    }

    @Override
    public void listenForAllClients(Bus bus) {
        listenForAllClients(bus, null);
    }

    @Override
    public void listenForAllClients(Bus bus, String selectionStrategy) {
        ClientLifeCycleManager clcm = bus.getExtension(ClientLifeCycleManager.class);
        clcm.registerListener(new ClientLifeCycleListenerForLocator());
    }

    @Override
    public void enableClient(Client client) {
        enableClient(client, null);
    }

    @Override
    public void enableClient(final Client client, SLPropertiesMatcher matcher) {
        enableClient(client, matcher, null);
    }

    @Override
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

    @Override
    public void enableClient(ClientConfiguration clientConf) {
        enableClient(clientConf, null);
    }

    @Override
    public void enableClient(final ClientConfiguration clientConf, SLPropertiesMatcher matcher) {
        enableClient(clientConf, matcher, null);
    }

    @Override
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


    public void setLocatorRegistrar(LocatorRegistrar locatorRegistrar) {
        this.locatorRegistrar = locatorRegistrar;
    }

    public void setLocatorClientEnabler(LocatorClientEnabler locatorClientEnabler) {
        clientEnabler = locatorClientEnabler;
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
