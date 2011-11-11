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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.endpoint.ServerLifeCycleListener;
import org.apache.cxf.endpoint.ServerLifeCycleManager;
import org.apache.cxf.endpoint.ServerRegistry;
import org.apache.cxf.service.model.EndpointInfo;
import org.talend.esb.servicelocator.client.SLProperties;
import org.talend.esb.servicelocator.client.ServiceLocator;
import org.talend.esb.servicelocator.client.ServiceLocatorException;

/**
 * The LocatorRegistrar is responsible for registering the endpoints of CXF Servers at the Service Locator.
 * The Servers endpoint can either be {@link #registerServer(Server) registered explicitly} or the
 * LocatorRegistrar can be {@link #startListenForServers() enabled to listen for all Servers} that are in the
 * process to start and to register them all.
 * <p>
 * If a server which was registered before stops the LocatorRegistrar automatically unregisters from the
 * Service Locator.
 */
public class SingleBusLocatorRegistrar implements ServerLifeCycleListener, ServiceLocator.PostConnectAction {

    private static final Logger LOG = Logger.getLogger(SingleBusLocatorRegistrar.class.getPackage().getName());

    private Bus bus;

    private ServiceLocator locatorClient;

    private String endpointPrefix = "";

    private Map<Server, CXFEndpointProvider> registeredServers = 
        Collections.synchronizedMap(new LinkedHashMap<Server, CXFEndpointProvider>());

    private boolean listenForServersEnabled;

    public SingleBusLocatorRegistrar() {
        
    }

    public SingleBusLocatorRegistrar(Bus bus) {
        setBus(bus);
    }

    @Override
    public void startServer(Server server) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Server " + server + " starting...");
        }
        if (listenForServersEnabled) {
            registerServer(server);
        }
    }

    @Override
    public void stopServer(Server server) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Server " + server + " stopping...");
        }
        if (registeredServers.containsKey(server)) {
            unregisterServer(server);
        }
    }

    public void startListenForServers() {
        check(bus, "bus", "startListenForServers");
        listenForServersEnabled = true;
        registerAvailableServers();
    }

    public void stopListenForServers() {
        listenForServersEnabled = false;
    }

    @Override
    public void process(ServiceLocator lc) {
        for (Server server : registeredServers.keySet()) {
            registerServer(registeredServers.get(server));
        }
    }

    public void setBus(Bus bus) {
        if (bus != this.bus) {
            this.bus = bus;
            registerListener();
        }
    }

    public void setEndpointPrefix(String endpointPrefix) {
        this.endpointPrefix = endpointPrefix != null ? endpointPrefix : "";
    }

    public void setServiceLocator(ServiceLocator locatorClient) {
        this.locatorClient = locatorClient;
        locatorClient.setPostConnectAction(this);
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Locator client was set.");
        }
    }

    private void registerListener() {
        ServerLifeCycleManager manager = bus.getExtension(ServerLifeCycleManager.class);
        if (manager != null) {
            manager.registerListener(this);
            if (LOG.isLoggable(Level.FINE)) {
                LOG.log(Level.FINE, "Server life cycle listener registered.");
            }
        } else {
            if (LOG.isLoggable(Level.WARNING)) {
                LOG.log(Level.WARNING, "ServerLifeCycleManager is not available.");
            }
        }
    }

    public void registerServer(Server server) {
        registerServer(server, null);
    }

    public void registerServer(Server server, SLProperties props) {
        check(locatorClient, "serviceLocator", "registerEndpoint");
        String absAddress = getAddress(server);
        if (!absAddress.startsWith("http://")) { // relative address
            absAddress = endpointPrefix + absAddress;
        }

        CXFEndpointProvider endpoint = new CXFEndpointProvider(server, absAddress, props);

        registerServer(endpoint);
        registeredServers.put(server, endpoint);
    }
    
    private void registerServer(CXFEndpointProvider endpointProvider) {
        try {
            locatorClient.register(endpointProvider);        
        } catch (ServiceLocatorException e) {
            if (LOG.isLoggable(Level.SEVERE)) {
                LOG.log(Level.SEVERE, "ServiceLocator Exception thrown when registering for endpoint "
                        + endpointProvider, e);
            }
        } catch (InterruptedException e) {
            if (LOG.isLoggable(Level.SEVERE)) {
                LOG.log(Level.SEVERE, "Interrupted Exception thrown when registering  for endpoint "
                        + endpointProvider, e);
            }
        }
    }


    private void unregisterServer(Server server) {
        try {
            CXFEndpointProvider epp = registeredServers.get(server);
            locatorClient.unregister(epp);
        } catch (ServiceLocatorException e) {
            if (LOG.isLoggable(Level.SEVERE)) {
                LOG.log(Level.SEVERE, "ServiceLocator Exception thrown during unregister endpoint. ", e);
            }
        } catch (InterruptedException e) {
            if (LOG.isLoggable(Level.SEVERE)) {
                LOG.log(Level.SEVERE, "Interrupted Exception thrown during unregister endpoint.", e);
            }
        }
    }

    /**
     * 
     */
    private void registerAvailableServers() {
        ServerRegistry serverRegistry = bus.getExtension(ServerRegistry.class);
        List<Server> servers = serverRegistry.getServers();
        for (Server server : servers) {
            registerServer(server);
        }
    }

    private String getAddress(Server server) {
        EndpointInfo eInfo = server.getEndpoint().getEndpointInfo();
        return eInfo.getAddress();
    }
 
    private  void check(Object obj, String propertyName, String methodName) {
        if (obj == null) {
            throw new IllegalStateException("The property " + propertyName + " must be set before "
                    + methodName + " can be called.");
        }
    }
}
