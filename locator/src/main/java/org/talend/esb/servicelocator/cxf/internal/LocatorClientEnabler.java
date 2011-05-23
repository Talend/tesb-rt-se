package org.talend.esb.servicelocator.cxf.internal;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.ClientLifeCycleListener;
import org.apache.cxf.endpoint.ClientLifeCycleManager;
import org.talend.esb.servicelocator.client.ServiceLocator;

public class LocatorClientEnabler implements ClientLifeCycleListener {

	private static final Logger LOG = Logger.getLogger(LocatorClientEnabler.class
			.getPackage().getName());

	private ServiceLocator locatorClient;

	private Bus bus;
	
	public void setServiceLocator(ServiceLocator locatorClient) {
		this.locatorClient = locatorClient;
		if (LOG.isLoggable(Level.FINE)) {
			LOG.log(Level.FINE, "Locator client " + locatorClient + " was set for LocatorClientRegistrar.");
		}
	}
	
	public void setBus(Bus bus) {
		this.bus = bus;

		if (LOG.isLoggable(Level.FINE)) {
			LOG.log(Level.FINE, "Bus " + bus + " was set for LocatorClientRegistrar.");
		}
}

	public void enable(Client client) {
		LocatorTargetSelector selector = new LocatorTargetSelector();
        selector.setEndpoint(client.getEndpoint());

		LocatorSelectionStrategy lfs = new LocatorSelectionStrategy();
		lfs.setServiceLocator(locatorClient);
		selector.setLocatorFailoverStrategy(lfs);
        client.setConduitSelector(selector);

		if (LOG.isLoggable(Level.FINE)) {
			LOG.log(Level.FINE, "Successfully enabled client " + client + " for the service locator");
		}
	}

	public void startListenForAllClients() {
		ClientLifeCycleManager clcm = bus.getExtension(ClientLifeCycleManager.class);
		clcm.registerListener(this);
	}

	public void stopListenForAllClients() {
		ClientLifeCycleManager clcm = bus.getExtension(ClientLifeCycleManager.class);
		clcm.unRegisterListener(this);
	}

	@Override
	public void clientCreated(Client client) {
		enable(client);
	}

	@Override
	public void clientDestroyed(Client client) {
	}
}
