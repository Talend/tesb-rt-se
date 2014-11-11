package org.talend.esb.policy.compression.feature;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.interceptor.InterceptorProvider;
import org.apache.cxf.message.Message;
import org.talend.esb.policy.compression.impl.CompressionInInterceptor;
import org.talend.esb.policy.compression.impl.CompressionOutInterceptor;

/**
 * This class is used to control compression of messages. Attaching this feature
 * to an endpoint will allow the endpoint to handle compressed requests, and
 * will cause outgoing responses to be compressed.
 * 
 * <pre>
 * <![CDATA[
 * <jaxws:endpoint ...>
 *    <jaxws:features>
 * 	     <bean id="compressionFeature" class="org.talend.esb.policy.compression.feature.CompressionFeature">
 * 		    <property name="threshold" value="100"/>	
 * 	     </bean>
 *    </jaxws:features>
 * </jaxws:endpoint>
 * ]]>
 * </pre>
 * 
 * Attaching this feature to a client will cause outgoing request messages to be
 * compressed and incoming compressed responses to be uncompressed.
 */

public class CompressionFeature extends AbstractFeature {

	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(CompressionFeature.class
			.getName());

	/**
	 * The compression threshold to pass to the outgoing interceptor.
	 */
	int threshold = -1;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.cxf.feature.AbstractFeature#initialize(org.apache.cxf.endpoint
	 * .Client, org.apache.cxf.Bus)
	 */
	public void initialize(Client client, Bus bus) {
		if (LOG.isLoggable(Level.FINE)) {
			LOG.log(Level.FINE, "Initializing Compression feature for bus "
					+ bus + " and client " + client);
		}
		initializeProvider(client, bus);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.cxf.feature.AbstractFeature#initialize(org.apache.cxf.endpoint
	 * .Server, org.apache.cxf.Bus)
	 */
	@Override
	public void initialize(Server server, Bus bus) {
		if (LOG.isLoggable(Level.FINE)) {
			LOG.log(Level.FINE, "Initializing Compression feature for bus "
					+ bus + " and server " + server);
		}
		initializeProvider(server.getEndpoint(), bus);
	}

	@Override
	protected void initializeProvider(InterceptorProvider provider, Bus bus) {

		CompressionOutInterceptor out = new CompressionOutInterceptor();
		CompressionInInterceptor in = new CompressionInInterceptor();

		out.setThreshold(getThreshold());

		remove(provider.getOutInterceptors());
		remove(provider.getOutFaultInterceptors());

		provider.getOutInterceptors().add(out);
		provider.getOutFaultInterceptors().add(out);

		provider.getInInterceptors().add(in);
	}

	private void remove(List<Interceptor<? extends Message>> outInterceptors) {
		int x = outInterceptors.size();
		while (x > 0) {
			--x;
			if (outInterceptors.get(x) instanceof CompressionOutInterceptor) {
				outInterceptors.remove(x);
			}
		}
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}

	public int getThreshold() {
		return threshold;
	}
}
