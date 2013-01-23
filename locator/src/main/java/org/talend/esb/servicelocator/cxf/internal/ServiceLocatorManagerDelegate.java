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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.client.ClientConfiguration;
import org.osgi.framework.BundleContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.talend.esb.servicelocator.client.SLProperties;
import org.talend.esb.servicelocator.client.SLPropertiesMatcher;
import org.talend.esb.servicelocator.cxf.ServiceLocatorManager;

public class ServiceLocatorManagerDelegate implements ServiceLocatorManager {

    private static final Logger LOG = Logger.getLogger(ServiceLocatorManagerDelegate.class.getPackage().getName());

    Bus bus;
    
    private ServiceLocatorManagerProvider provider;

    public ServiceLocatorManagerDelegate(Bus cxfBus) {
        bus = cxfBus;

        init();
    }

    @Override
    public void listenForAllServers(Bus anotherBus) {
        provider.getServiceLocatorManager().listenForAllServers(anotherBus);
        
    }

    @Override
    public void registerServer(Server server, Bus anotherBus) {
        provider.getServiceLocatorManager().registerServer(server, anotherBus);
        
    }

    @Override
    public void registerServer(Server server, SLProperties props, Bus anotherBus) {
        provider.getServiceLocatorManager().registerServer(server, props, anotherBus);
    }

    @Override
    public void listenForAllClients(Bus bus) {
        provider.getServiceLocatorManager().listenForAllClients(bus);
    }

    @Override
    public void listenForAllClients(Bus bus, String selectionStrategy) {
        provider.getServiceLocatorManager().listenForAllClients(bus, selectionStrategy);
    }

    @Override
    public void enableClient(Client client) {
        provider.getServiceLocatorManager().enableClient(client);
    }

    @Override
    public void enableClient(Client client, SLPropertiesMatcher matcher) {
        provider.getServiceLocatorManager().enableClient(client, matcher);
    }

    @Override
    public void enableClient(Client client, SLPropertiesMatcher matcher, String selectionStrategy) {
        provider.getServiceLocatorManager().enableClient(client, matcher, selectionStrategy);
    }

    @Override
    public void enableClient(ClientConfiguration clientConf) {
        provider.getServiceLocatorManager().enableClient(clientConf);
        
    }

    @Override
    public void enableClient(ClientConfiguration clientConf, SLPropertiesMatcher matcher) {
        provider.getServiceLocatorManager().enableClient(clientConf, matcher);        
    }

    @Override
    public void enableClient(ClientConfiguration clientConfiguration, SLPropertiesMatcher matcher,
            String selectionStrategy) {
        provider.getServiceLocatorManager().enableClient(clientConfiguration, matcher, selectionStrategy);
    }

    private void init() {
        if (inOSGi()) {            
            provider = new ServiceLocatorManagerProviderOSGi(bus);
        } else {
            provider = new ServiceLocatorManagerProviderSpring();
        }
    }
    
    private boolean inOSGi() {
        boolean inOSGi = false;
        try {
            Class.forName("org.osgi.framework.BundleContext");
            inOSGi = (bus.getExtension(BundleContext.class) != null);
        } catch (ClassNotFoundException e) {
        }

        if (LOG.isLoggable(Level.INFO)) {
            if (inOSGi) {
                LOG.log(Level.INFO, "BundleContext available, running in an OSGi environment.");
            } else {
                LOG.log(Level.INFO, "BundleContext not available, not running in an OSGi environment.");
            }
        }

        return inOSGi;
    }

    public static interface ServiceLocatorManagerProvider {
        
        ServiceLocatorManager getServiceLocatorManager();
    }
}
