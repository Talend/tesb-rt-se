package org.talend.esb.policy.correlation.feature;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.feature.AbstractFeature;

public class CorrelationIDFeature extends AbstractFeature {

    public static final String MESSAGE_CORRELATION_ID = "CorrelationID";

    public static final String CORRELATION_ID_CALLBACK_HANDLER = "correlation-id.callback-handler";

    private static final Logger LOG = Logger.getLogger(CorrelationIDFeature.class.getName());

    @Override
    public void initialize(Bus bus) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Initializing CorrelationID feature for bus " + bus);
        }
        bus.setExtension(new CorrelationIDFeatureInterceptorProvider(), CorrelationIDFeatureInterceptorProvider.class);
    }

    public void initialize(Client client, Bus bus) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Initializing CorrelationID feature for bus " + bus + " and client " + client);
        }
        initialize(bus);
    }

    @Override
    public void initialize(Server server, Bus bus) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Initializing CorrelationID feature for bus " + bus + " and server " + server);
        }
        initialize(bus);
    }
}
