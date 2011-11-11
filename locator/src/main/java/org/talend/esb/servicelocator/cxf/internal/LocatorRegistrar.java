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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.apache.cxf.Bus;
import org.apache.cxf.buslifecycle.BusLifeCycleListener;
import org.apache.cxf.buslifecycle.BusLifeCycleManager;
import org.apache.cxf.endpoint.Server;
import org.talend.esb.servicelocator.client.SLProperties;
import org.talend.esb.servicelocator.client.ServiceLocator;

/**
 * The LocatorRegistrar is responsible for registering the endpoints of CXF Servers at the Service Locator.
 * The Servers endpoint can either be {@link #registerServer(Server, Bus) registered explicitly} or the
 * LocatorRegistrar can be enabled  {@link #startListenForServers(Bus) to listen for all Servers of a 
 * specific bus} that are in the process to start and to register them all.
 * <p>
 * If a server which was registered before stops the LocatorRegistrar automatically unregisters from the
 * Service Locator.
 */
public class LocatorRegistrar {

    private static final Logger LOG = Logger.getLogger(LocatorRegistrar.class.getPackage().getName());

    private ServiceLocator locatorClient;

    private String endpointPrefix = "";

    private Map<Bus, SingleBusLocatorRegistrar> busRegistrars = 
        Collections.synchronizedMap(new LinkedHashMap<Bus, SingleBusLocatorRegistrar>());

    public void startListenForServers(Bus bus) {
        SingleBusLocatorRegistrar registrar = getRegistrar(bus);
        registrar.startListenForServers();
    }

    public void setEndpointPrefix(String endpointPrefix) {
        this.endpointPrefix = endpointPrefix != null ? endpointPrefix : "";
    }

    public void setServiceLocator(ServiceLocator locatorClient) {
        this.locatorClient = locatorClient;
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Locator client was set.");
        }
    }

    public void registerServer(Server server, Bus bus) {
        registerServer(server, null, bus);
    }

    public void registerServer(Server server, SLProperties props, Bus bus) {
        SingleBusLocatorRegistrar registrar = getRegistrar(bus);
        registrar.registerServer(server, props);
    }

    private SingleBusLocatorRegistrar getRegistrar(Bus bus) {
        SingleBusLocatorRegistrar registrar = busRegistrars.get(bus);
        if (registrar == null) {
            check(locatorClient, "serviceLocator", "registerService");
            registrar = new SingleBusLocatorRegistrar(bus);
            registrar.setServiceLocator(locatorClient);
            registrar.setEndpointPrefix(endpointPrefix);
            busRegistrars.put(bus, registrar);
            addLifeCycleListener(bus);
        }
        return registrar;
    }
    
    private void addLifeCycleListener(final Bus bus) {
        final BusLifeCycleManager manager = bus.getExtension(BusLifeCycleManager.class);
        manager.registerLifeCycleListener(new BusLifeCycleListener() {
            @Override
            public void initComplete() { }

            @Override
            public void preShutdown() { }

            @Override
            public void postShutdown() {
                busRegistrars.remove(bus);
//                manager.unregisterLifeCycleListener(this);
            }
        });
    }

    private  void check(Object obj, String propertyName, String methodName) {
        if (obj == null) {
            throw new IllegalStateException("The property " + propertyName + " must be set before "
                    + methodName + " can be called.");
        }
    }
}
