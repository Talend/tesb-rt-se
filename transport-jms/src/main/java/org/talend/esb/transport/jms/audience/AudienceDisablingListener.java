package org.talend.esb.transport.jms.audience;

import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.endpoint.*;
import org.apache.cxf.transport.Destination;
import org.apache.cxf.ws.addressing.EndpointReferenceType;

import java.util.logging.Logger;


public class AudienceDisablingListener implements ClientLifeCycleListener,
        ServerLifeCycleListener {

    private static final Logger LOGGER = LogUtils.getL7dLogger(AudienceDisablingListener.class);

    public AudienceDisablingListener() {
        // Explicit default constructor declaration is needed in OSGi environment
    }


    @Override
    public void clientCreated(Client client) {
        try {
            EndpointImpl ep = (EndpointImpl) client.getEndpoint();
            if (ep.getEndpointInfo().getAddress().startsWith("jms")) {
                client.getOutInterceptors().add(new AudienceOutInterceptor());
            }
        } catch (Throwable t) {
           LOGGER.severe(t.getMessage());
        }
    }


    @Override
    public void startServer(Server server) {
        try {
            Destination destination = server.getDestination();
            if (destination != null) {
                EndpointReferenceType ert = destination.getAddress();
                if (ert != null
                        && ert.getAddress() != null
                        && ert.getAddress().getValue() != null
                        && ert.getAddress().getValue().startsWith("jms")) {
                    server.getEndpoint().put("security.validate.audience-restriction", false);
                }
            }
        } catch (Throwable t) {
            LOGGER.severe(t.getMessage());
        }
    }

    @Override
    public void stopServer(Server server) {
        // Do nothing
    }

    @Override
    public void clientDestroyed(Client client) {
        // Do nothing
    }
}
