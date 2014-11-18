package org.talend.esb.policy.correlation.feature;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.interceptor.InterceptorProvider;
import org.talend.esb.policy.correlation.impl.CorrelationIDAssertion;
import org.talend.esb.policy.correlation.impl.CorrelationIDFeatureInInterceptor;
import org.talend.esb.policy.correlation.impl.CorrelationIDFeatureOutInterceptor;
import org.talend.esb.policy.correlation.impl.CorrelationIDFeatureSelectorInterceptor;
import org.talend.esb.policy.correlation.impl.xpath.XpathNamespace;
import org.talend.esb.policy.correlation.impl.xpath.XpathPart;

public class CorrelationIDFeature extends AbstractFeature {

	public static final String MESSAGE_CORRELATION_ID = "CorrelationID";

	public static final String CORRELATION_ID_CALLBACK_HANDLER = "correlation-id.callback-handler";

	private static final Logger LOG = Logger
			.getLogger(CorrelationIDFeature.class.getName());

	private final CorrelationIDAssertion policy = new CorrelationIDAssertion();

	public void initialize(Client client, Bus bus) {
		if (LOG.isLoggable(Level.FINE)) {
			LOG.log(Level.FINE, "Initializing CorrelationID feature for bus "
					+ bus + " and client " + client);
		}
		initializeProvider(client, bus);
	}

	@Override
	public void initialize(Server server, Bus bus) {
		if (LOG.isLoggable(Level.FINE)) {
			LOG.log(Level.FINE, "Initializing CorrelationID feature for bus "
					+ bus + " and server " + server);
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

		CorrelationIDFeatureInInterceptor corrIdProducerIn = new CorrelationIDFeatureInInterceptor(
				policy);
		provider.getInInterceptors().add(corrIdProducerIn);
		provider.getInFaultInterceptors().add(corrIdProducerIn);

		CorrelationIDFeatureOutInterceptor corrIdProducerOut = new CorrelationIDFeatureOutInterceptor(
				policy);
		provider.getOutInterceptors().add(corrIdProducerOut);
		provider.getOutFaultInterceptors().add(corrIdProducerOut);
		
		// Selector registers SAAJ interceptor for Soap messages only 
		CorrelationIDFeatureSelectorInterceptor selector = new CorrelationIDFeatureSelectorInterceptor();
		provider.getInInterceptors().add(selector);
		provider.getInFaultInterceptors().add(selector);
	}

	public void setName(String name) {
		policy.setCorrelationName(name);
	}

	public void setType(String type) {
		policy.setMethodType(type);
	}

	public void setNamespaces(Map<String, String> namespaces) {
		if (namespaces != null) {
			for (Map.Entry<String, String> entry : namespaces.entrySet()) {
				policy.addNamespace(entry.getKey(), entry.getValue());
			}
		}
	}
	
	public void setXpathNamespaces(List<XpathNamespace> namespaces) {
		for (XpathNamespace ns : namespaces) {
			policy.addNamespace(ns);
		}
	}
	
	public void setXpathParts(List<XpathPart> parts) {
		for (XpathPart p : parts) {
			policy.addXpathPart(p);
		}
	}	
}
