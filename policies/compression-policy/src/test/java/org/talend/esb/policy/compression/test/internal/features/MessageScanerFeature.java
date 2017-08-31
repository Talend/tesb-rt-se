package org.talend.esb.policy.compression.test.internal.features;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.interceptor.InterceptorProvider;
import org.talend.esb.policy.compression.test.internal.interceptors.MessageScanerInInterceptor;
import org.talend.esb.policy.compression.test.internal.interceptors.MessageScanerOutInterceptor;

public class MessageScanerFeature extends AbstractFeature {

	private static final Logger LOG = Logger
			.getLogger(MessageScanerFeature.class.getName());

	private String outgoingMessagePattern = null;

	private String incomingMessagePattern = null;

	private boolean throwPatternNotMatchedException = true;

	public void initialize(Client client, Bus bus) {
		if (LOG.isLoggable(Level.FINE)) {
			LOG.log(Level.FINE, "Initializing Scaner feature for bus " + bus
					+ " and client " + client);
		}
		initializeProvider(client, bus);

	}

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

		MessageScanerOutInterceptor out = new MessageScanerOutInterceptor(
				outgoingMessagePattern, throwPatternNotMatchedException);
		MessageScanerInInterceptor in = new MessageScanerInInterceptor(
				incomingMessagePattern, throwPatternNotMatchedException);

		provider.getOutInterceptors().add(out);
		provider.getOutFaultInterceptors().add(out);

		provider.getInInterceptors().add(in);
	}

	public String getOutgoingMessagePattern() {
		return outgoingMessagePattern;
	}

	public void setOutgoingMessagePattern(String outgoingMessagePattern) {
		this.outgoingMessagePattern = outgoingMessagePattern;
	}

	public String getIncomingMessagePattern() {
		return incomingMessagePattern;
	}

	public void setIncomingMessagePattern(String incomingMessagePattern) {
		this.incomingMessagePattern = incomingMessagePattern;
	}

	public void setThrowPatternNotMatchedException(
			boolean throwPatternNotMatchedException) {
		this.throwPatternNotMatchedException = throwPatternNotMatchedException;
	}
}
