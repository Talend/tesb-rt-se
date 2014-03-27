package org.talend.esb.policy.compression.feature;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.transport.common.gzip.GZIPFeature;

/**
 * This class is used to control compression of messages.
 * Attaching this feature to an endpoint will allow the endpoint to handle
 * compressed requests, and will cause outgoing responses to be compressed if
 * the client indicates (via the Accept-Encoding header) that it can handle
 * them.
 * <pre>
 * <![CDATA[
 * <jaxws:endpoint ...>
 *   <jaxws:features>
 *     <bean class="org.apache.cxf.transport.common.gzip.GZIPFeature"
 *     p:threshold="100" p:force="false" />     
 *   </jaxws:features>
 * </jaxws:endpoint>
 * ]]>
 * </pre>
 * Attaching this feature to a client will cause outgoing request messages 
 * to be compressed and incoming compressed responses to be uncompressed. 
 * Accept-Encoding header is sent to let the service know 
 * that your client can accept compressed responses. 
 */

public class CompressionFeature extends GZIPFeature {

    /** The Constant LOG. */
    private static final Logger LOG = Logger.getLogger(CompressionFeature.class.getName());

    /* (non-Javadoc)
     * @see org.apache.cxf.feature.AbstractFeature#initialize(org.apache.cxf.endpoint.Client, org.apache.cxf.Bus)
     */
    public void initialize(Client client, Bus bus) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Initializing Compression feature for bus " + bus + " and client " + client);
        }
        initializeProvider(client, bus);
    }

    /* (non-Javadoc)
     * @see org.apache.cxf.feature.AbstractFeature#initialize(org.apache.cxf.endpoint.Server, org.apache.cxf.Bus)
     */
    @Override
    public void initialize(Server server, Bus bus) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Initializing Compression feature for bus " + bus + " and server " + server);
        }
        initializeProvider(server.getEndpoint(), bus);
    }
}
