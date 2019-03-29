package org.talend.esb.servicelocator.cxf.internal;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.component.cxf.CxfEndpoint;
import org.apache.camel.component.cxf.jaxrs.CxfRsEndpoint;
import org.apache.camel.spi.LifecycleStrategy;
import org.apache.camel.support.LifecycleStrategySupport;
import org.apache.camel.util.CamelContextHelper;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Add a lifecyclestrategy behaviour for associating camelcontext to bus in locatorRegistar and stop all servers when camelcontext stops.
 */
@OsgiServiceProvider(classes = LifecycleStrategy.class)
@Named("locatorLifecycleStrategy")
@Singleton
public class LocatorLifecycleStrategy extends LifecycleStrategySupport {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    LocatorRegistrar locatorRegistrar;

    @Override
    public void onContextStop(CamelContext context) {
        stopAllServersAndRemoveCamelContext(context);
    }

    @Override
    public void onEndpointAdd(Endpoint endpoint) {
        addCamelContextToRegistar(endpoint);
    }

    /**
     * Creates/retrieves the registar linked with the bus and associates the current camel context
     *
     * @param endpoint
     */
    private void addCamelContextToRegistar(Endpoint endpoint) {
        Bus bus = null;
        try {
            if (endpoint instanceof CxfEndpoint) {
                // getBus creates a new one if none
                bus = ((CxfEndpoint) endpoint).getBus();

            } else if (endpoint instanceof CxfRsEndpoint) {
                JAXRSServerFactoryBean jaxrsServerFactoryBean = CamelContextHelper.lookup(endpoint.getCamelContext(), ((CxfRsEndpoint) endpoint).getBeanId(), JAXRSServerFactoryBean.class);
                if (jaxrsServerFactoryBean != null) {
                    bus = jaxrsServerFactoryBean.getBus();
                }
            }
        } catch (Exception e) {
            // nothing special
        }
        if (bus != null) {
            log.debug("Linking bus={} with {}" + bus.getId(), endpoint.getCamelContext());
            locatorRegistrar.getRegistrar(bus).setCamelContext(endpoint.getCamelContext());
        }
    }

    /**
     * Stops all servers linked with the current camel context
     *
     * @param camelContext
     */
    private void stopAllServersAndRemoveCamelContext(CamelContext camelContext) {
        log.debug("Stopping all servers associated with {}", camelContext);
        List<SingleBusLocatorRegistrar> registrars = locatorRegistrar.getAllRegistars(camelContext);
        registrars.forEach(registrar -> registrar.stopAllServersAndRemoveCamelContext());
    }
}
