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

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.camel.CamelContext;
import org.apache.cxf.Bus;
import org.apache.cxf.buslifecycle.BusLifeCycleListener;
import org.apache.cxf.buslifecycle.BusLifeCycleManager;
import org.apache.cxf.endpoint.Server;
import org.springframework.beans.factory.annotation.Value;
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
@Named
@Singleton
public class LocatorRegistrar {

    private static final Logger LOG = Logger.getLogger(LocatorRegistrar.class.getPackage().getName());

    @Inject
    ServiceLocator locatorClient;

    @Value("${endpoint.prefix}")
    String endpointPrefix = "";

    @Value("${endpoint.http.prefix}")
    String endpointPrefixHttp;

    @Value("${endpoint.https.prefix}")
    String endpointPrefixHttps;

    private Map<Bus, SingleBusLocatorRegistrar> busRegistrars =
            Collections.synchronizedMap(new LinkedHashMap<Bus, SingleBusLocatorRegistrar>());


    public void startListenForServers(Bus bus) {
        SingleBusLocatorRegistrar registrar = getRegistrar(bus);
        registrar.startListenForServers();
    }

    public void setEndpointPrefix(String endpointPrefix) {
        this.endpointPrefix = endpointPrefix != null ? endpointPrefix : "";
    }

    public void setServiceLocator(ServiceLocator serviceLocator) {
        this.locatorClient = serviceLocator;
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Locator client was set.");
        }
    }

    public void registerServer(Server server, Bus bus) {
        registerServer(server, null, bus);
    }

    public void registerServer(Server server, SLProperties props, Bus bus) {
        getRegistrar(bus).registerServer(server, props);
    }

    /**
     * Retrieves the registar linked to the bus.
     * Creates a new registar is not present.
     *
     * @param bus
     * @return
     */
    protected SingleBusLocatorRegistrar getRegistrar(Bus bus) {
        SingleBusLocatorRegistrar registrar = busRegistrars.get(bus);
        if (registrar == null) {
            check(locatorClient, "serviceLocator", "registerService");
            registrar = new SingleBusLocatorRegistrar(bus);
            registrar.setServiceLocator(locatorClient);
            registrar.setEndpointPrefix(endpointPrefix);
            Map<String, String> endpointPrefixes = new HashMap<String, String>();
            endpointPrefixes.put("HTTP", endpointPrefixHttp);
            endpointPrefixes.put("HTTPS", endpointPrefixHttps);
            registrar.setEndpointPrefixes(endpointPrefixes);
            busRegistrars.put(bus, registrar);
            addLifeCycleListener(bus);
        }
        return registrar;
    }

    protected List<SingleBusLocatorRegistrar> getAllRegistars(CamelContext camelContext) {
        return busRegistrars.values().stream().filter(registar -> camelContext.equals(registar.getCamelContext())).collect(Collectors.toList());
    }

    private void addLifeCycleListener(final Bus bus) {
        final BusLifeCycleManager manager = bus.getExtension(BusLifeCycleManager.class);
        manager.registerLifeCycleListener(new BusLifeCycleListener() {
            @Override
            public void initComplete() {
            }

            @Override
            public void preShutdown() {
                // preShutdown
            }

            @Override
            public void postShutdown() {
                locatorClient.removePostConnectAction(busRegistrars.get(bus));
                busRegistrars.remove(bus);
            }
        });
    }

    private void check(Object obj, String propertyName, String methodName) {
        if (obj == null) {
            throw new IllegalStateException("The property " + propertyName + " must be set before "
                    + methodName + " can be called.");
        }
    }

    public void setEndpointPrefixHttp(String endpointPrefixHttp) {
        this.endpointPrefixHttp = endpointPrefixHttp;
    }

    public void setEndpointPrefixHttps(String endpointPrefixHttps) {
        this.endpointPrefixHttps = endpointPrefixHttps;
    }
}
