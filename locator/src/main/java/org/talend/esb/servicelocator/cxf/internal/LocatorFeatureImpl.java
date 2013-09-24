package org.talend.esb.servicelocator.cxf.internal;

import java.util.Arrays;
import java.util.Collection;
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
import org.talend.esb.servicelocator.cxf.LocatorFeature;

public class LocatorFeatureImpl extends AbstractFeature implements LocatorFeature {
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
        for ( StackTraceElement trace : new Throwable().getStackTrace() )
        	LOG.fine(trace.toString());
    }

    @Override
    public void setSelectionStrategy(String selectionStrategy) {
        this.selectionStrategy = selectionStrategy;
    }

    Collection<String> tokenize(String valueList) {
        return Arrays.asList(valueList.split(","));
    }
}
