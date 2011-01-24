package org.talend.esb.locator;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.Service;

import javax.xml.namespace.QName;

public class EndpointResolver {

	private static final Logger LOG = Logger.getLogger(EndpointResolver.class
			.getName());

	private List<String> endpointsList;
	private ServiceLocator sl;
	private QName serviceName;

	public EndpointResolver(QName serviceName, String locatorEndpoints) {
		LOG.log(Level.INFO, "Creating EndpointResolver object for "
				+ serviceName.toString() + " service.");

		this.serviceName = serviceName;
		try {
			sl = new ServiceLocator();
			sl.setLocatorEndpoints(locatorEndpoints);
			sl.connect();
			endpointsList = receiveEndpointsList();
		} catch (IOException e) {
			if (LOG.isLoggable(Level.SEVERE)) {
				LOG.log(Level.SEVERE,
						"An IOException  was thrown when trying to connect to the ServiceLocator",
						e);
			}
		} catch (InterruptedException e) {
			if (LOG.isLoggable(Level.SEVERE)) {
				LOG.log(Level.SEVERE,
						"An InterruptedException was thrown while waiting for an answer from the Service Locator",
						e);
			}
		} catch (ServiceLocatorException e) {
			if (LOG.isLoggable(Level.SEVERE)) {
				LOG.log(Level.SEVERE,
						"Failed to execute an request to Service Locator", e);
			}
		}

		LOG.log(Level.INFO, "Endpoint Resolver was created successfully.");
	}

	private List<String> receiveEndpointsList() {
		LOG.log(Level.INFO, "Getting endpoints of " + serviceName.toString()
				+ " service.");

		List<String> endpointsList = null;

		try {
			endpointsList = sl.lookup(this.serviceName);
		} catch (ServiceLocatorException e) {
			if (LOG.isLoggable(Level.SEVERE)) {
				LOG.log(Level.SEVERE,
						"Failed to execute an request to Service Locator", e);
			}
		} catch (InterruptedException e) {
			if (LOG.isLoggable(Level.SEVERE)) {
				LOG.log(Level.SEVERE,
						"An InterruptedException was thrown while waiting for an answer from the Service Locator",
						e);
			}
		}
		LOG.log(Level.INFO,
				"Received list of endpoints: " + endpointsList.toString());
		return endpointsList;
	}

	public String selectEndpoint() {
		if (endpointsList.isEmpty()) {
			LOG.log(Level.WARNING, "List of endpoints is empty");
		} else {
			int endpointAmount = endpointsList.size();
			int randomNumber = (int) Math.round(Math.random() * endpointAmount);
			int endpointIndex = randomNumber % endpointAmount;

			LOG.log(Level.INFO,
					"Selected endpoint: " + endpointsList.get(endpointIndex));
			return endpointsList.get(endpointIndex);
		}

		return null;
	}

	public List<String> getEndpointsList() {
		LOG.log(Level.INFO, "List of endpoints: " + endpointsList.toString());
		return endpointsList;
	}

	public void refreshEndpointsList() {
		try {
			List<String> el = receiveEndpointsList();
			if (el == null) {
				LOG.log(Level.SEVERE, "Can not receive list of endpoint");
			}
		} catch (Exception e) {
			if (LOG.isLoggable(Level.SEVERE)) {
				LOG.log(Level.SEVERE,
						"Can not refresh list of endpoints due to unknown exception");
			}
		}
	}

	public <T> T getPort(QName portname, String binding,
			Class<T> serviceEndpointInterface) {
		Service service = null;
		String endpoint = selectEndpoint();

		if (endpoint == null) {
			if (LOG.isLoggable(Level.SEVERE)) {
				LOG.log(Level.SEVERE, "Endpoint not found for service "
						+ this.serviceName.toString());
				}
			} else {
				try {
					service = Service.create(this.serviceName);
					service.addPort(portname, binding, endpoint);
					return service.getPort(portname, serviceEndpointInterface);
					
				} catch (Exception e) {
					if (LOG.isLoggable(Level.SEVERE)) {
						LOG.log(Level.SEVERE,
								"Can not add port due to unknown exception");
					}
				}
			}
		return null;
	}

	public void log(String logstr) {
		LOG.log(Level.INFO, "List of endpoints: " + endpointsList.toString());
	}
}
