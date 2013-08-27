package org.talend.esb.policy.correlation.feature;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.interceptor.InterceptorProvider;
import org.talend.esb.policy.correlation.impl.CorrelationIDFeaturePolicyInInterceptor;
import org.talend.esb.policy.correlation.impl.CorrelationIDFeaturePolicyOutInterceptor;

public class CorrelationIDFeature extends AbstractFeature {

    public static final String MESSAGE_CORRELATION_ID = "CorrelationID";

    public static final String CORRELATION_ID_CALLBACK_HANDLER = "correlation-id.callback-handler";

    private static final Logger LOG = Logger.getLogger(CorrelationIDFeature.class.getName());

    public void initialize(Client client, Bus bus) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Initializing CorrelationID feature for bus " + bus + " and client " + client);
        }
        initializeProvider(client, bus);
    }

    @Override
    public void initialize(Server server, Bus bus) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Initializing CorrelationID feature for bus " + bus + " and server " + server);
        }
        initializeProvider(server.getEndpoint(), bus);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.cxf.feature.AbstractFeature#initializeProvider(org.apache.
     * cxf.interceptor.InterceptorProvider, org.apache.cxf.Bus)
     */
    @Override
    protected void initializeProvider(InterceptorProvider provider, Bus bus) {
        super.initializeProvider(provider, bus);

        CorrelationIDFeaturePolicyInInterceptor corrIdProducerIn = new CorrelationIDFeaturePolicyInInterceptor();
        provider.getInInterceptors().add(corrIdProducerIn);
        provider.getInFaultInterceptors().add(corrIdProducerIn);

        CorrelationIDFeaturePolicyOutInterceptor corrIdProducerOut = new CorrelationIDFeaturePolicyOutInterceptor();
        provider.getOutInterceptors().add(corrIdProducerOut);
        provider.getOutFaultInterceptors().add(corrIdProducerOut);

    }
}
