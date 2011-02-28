package org.talend.esb.locator;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.feature.AbstractFeature;

/**
 * CXF feature to enable the locator client with an CXF service.
 *
 */
public class LocatorFeature extends AbstractFeature {

	private static final Logger LOG = Logger.getLogger(ServiceLocator.class
			.getName());

	private String locatorEndpoints;

	private int sessionTimeout;

	private int connectionTimeout;

	private String prefix;

	@Override
	public void initialize(Client client, Bus bus) {
		LocatorTargetSelector selector = new LocatorTargetSelector();
        selector.setEndpoint(client.getEndpoint());
		
		ServiceLocator sl = null;

		try {
			sl = createServiceLocator();

			if (LOG.isLoggable(Level.FINE)) {
				LOG.log(Level.FINE, "Successfully initialized locator feature");
			}

		} catch (ServiceLocatorException e) {
			if (LOG.isLoggable(Level.SEVERE)) {
				LOG.log(Level.SEVERE,
						"ServiceLocator Exception thrown during initialization of the locator feature.",
						e);
			}
		} catch (InterruptedException e) {
			if (LOG.isLoggable(Level.SEVERE)) {
				LOG.log(Level.SEVERE,
						"Interrupted Exception thrown during initialization of the locator feature.",
						e);
			}
		}

		LocatorSelectionStrategy lfs = new LocatorSelectionStrategy();
		lfs.setServiceLocator(sl);
		selector.setLocatorFailoverStrategy(lfs);
        client.setConduitSelector(selector);

	}

	@Override
	public void initialize(Server server, Bus bus) {
		try {
			ServiceLocator sl = createServiceLocator();
			createLocatorRegistrar(bus, sl);
			
			if (LOG.isLoggable(Level.FINE)) {
				LOG.log(Level.FINE, "Successfully initialized locator feature");
			}

		} catch (InterruptedException e) {
			if (LOG.isLoggable(Level.SEVERE)) {
				LOG.log(Level.SEVERE,
						"Interrupted Exception thrown during initialization of the locator feature.", e);
			}
		} catch (ServiceLocatorException e) {
			if (LOG.isLoggable(Level.SEVERE)) {
				LOG.log(Level.SEVERE,
						"ServiceLocator Exception thrown during initialization of the locator feature.", e);
			}
		}
	}

	/**
	 * Specify the endpoints of all the instances belonging to the service locator ensemble the
	 * service locator client might be talking to. The service locator client will one by one pick
	 * an endpoint (the order is non-deterministic) to connect to the service locator until a
	 * connection is established.
	 * 
	 * @param endpoints comma separated list of endpoints,each corresponding to a servicelocator
	 *           instance. Each endpoint is specified as a host:port pair. At least one endpoint
	 *           must be specified. Valid exmaples are: "127.0.0.1:2181" or
	 *           "sl1.example.com:3210, sl2.example.com:3210, sl3.example.com:3210"
	 */
	public void setLocatorEndpoints(String endpoints) {
		locatorEndpoints = endpoints;
	}

	/**
	 * Specify the time out of the session established at the server. The session is kept alive by
	 * requests sent by the client. If the session is idle for a period of time that would timeout
	 * the session, the client will send a PING request to keep the session alive.
	 * 
	 * @param sessionTimeout timeout in milliseconds, must be greater than  zero and less than 60000. 
	 */
	public void setSessionTimeout(int sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}
	
	/**
	 * Specify the time the service locator client waits for a connection to get established.
	 * 
	 * @param connectionTimeout timeout in milliseconds, must be greater than zero
	 */
	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public void setEndpointPrefix(String prefix) {
		this.prefix = prefix;
	}

	private ServiceLocator createServiceLocator() throws InterruptedException,
			ServiceLocatorException {
		ServiceLocator sl = new ServiceLocator();
		if (locatorEndpoints != null) {
			sl.setLocatorEndpoints(locatorEndpoints);
		}

		if (sessionTimeout > 0) {
			sl.setSessionTimeout(sessionTimeout);
		}

		if (connectionTimeout > 0) {
			sl.setConnectionTimeout(connectionTimeout);
		}
		sl.connect();

		return sl;
	}
	
	private LocatorRegistrar createLocatorRegistrar(Bus bus, ServiceLocator sl)
			throws InterruptedException, ServiceLocatorException {
		LocatorRegistrar lr = new LocatorRegistrar();
		lr.setBus(bus);
		lr.setLocatorClient(sl);
		
		if (prefix != null) {
			lr.setEndpointPrefix(prefix);
		}
		lr.init();
		return lr;
	}
}