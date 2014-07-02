package org.talend.esb.mep.requestcallback.beans;

import javax.xml.ws.Endpoint;

import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.beans.factory.InitializingBean;
import org.talend.esb.mep.requestcallback.feature.CallContext;

public class JmsEndpointConfigurator implements InitializingBean {

	private Endpoint endpoint;
	private boolean publishEndpoint = true;

	public JmsEndpointConfigurator() {
		super();
	}

	public Endpoint getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(Endpoint endpoint) {
		this.endpoint = endpoint;
	}

	public boolean isPublishEndpoint() {
		return publishEndpoint;
	}

	public void setPublishEndpoint(boolean publishEndpoint) {
		this.publishEndpoint = publishEndpoint;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		final JmsUriConfigurator cfg = JmsUriConfigurator.create(endpoint);
		if (cfg == null) {
			return;
		}
		final EndpointImpl ei = (EndpointImpl) endpoint;
		CallContext.setupEndpoint(ei);
		cfg.setPresetJmsAddress(ei.getAddress());
		ei.setAddress(cfg.createJmsAddress());
		if (publishEndpoint) {
			ei.publish();
		}
	}
}
