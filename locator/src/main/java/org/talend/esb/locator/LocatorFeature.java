package org.talend.esb.locator;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.cxf.Bus;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.interceptor.InterceptorProvider;

public class LocatorFeature extends AbstractFeature {

	private static final Logger LOG = Logger.getLogger(ServiceLocator.class
			.getName());

	private String locatorEndpoints;

	private int sessionTimeout;

	private int connectionTimeout;

	private String prefix;
	
	@Override
	protected void initializeProvider(InterceptorProvider provider, Bus bus) {
		try {
			ServiceLocator sl = createServiceLocator();
			createLocatorRegistrar(bus, sl);
			
			if (LOG.isLoggable(Level.FINE)) {
				LOG.log(Level.FINE, "Successfully initialized locator feature");
			}

		} catch (IOException e) {
			if (LOG.isLoggable(Level.SEVERE)) {
				if (LOG.isLoggable(Level.SEVERE)) {
					LOG.log(Level.SEVERE,
							"ServiceLocator Exception thrown during initialization of the locator feature.", e);
				}
			}
		} catch (InterruptedException e) {
			if (LOG.isLoggable(Level.SEVERE)) {
				LOG.log(Level.SEVERE,
						"Interrupted Exception thrown during initialization of the locator feature.", e);
			}
		} catch (ServiceLocatorException e) {
			if (LOG.isLoggable(Level.SEVERE)) {
				LOG.log(Level.SEVERE,
						"Interrupted Exception thrown during initialization of the locator feature.", e);
			}
		}
	}

	public void setLocatorEndpoints(String endpoints) {
		locatorEndpoints = endpoints;
	}

	public void setSessionTimeout(int sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}
	
	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public void setEndpointPrefix(String prefix) {
		this.prefix = prefix;
	}

	private ServiceLocator createServiceLocator() {
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

		return sl;
	}
	
	private LocatorRegistrar createLocatorRegistrar(Bus bus, ServiceLocator sl)
			throws IOException, InterruptedException, ServiceLocatorException {
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