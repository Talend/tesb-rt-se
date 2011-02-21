package org.talend.esb.locator;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

/**
 * This class should be used by Service Locator consumers. It is able to
 * retrieve the list of available endpoints for a given service. Only a random
 * selection strategy to select a single endpoint out of the list uses. When
 * creating an object, you must specify Name of service for which to get the
 * endpoints and the hosts where ZooKeeper started.
 * <p>
 * Usecases:
 * <ul>
 * <li>For a given Service get the list of existing providers from the SL.
 * <li>Select from the list the endpoint to be used.
 * <li>Keep the list of endpoints for subsequent requests.
 * <li>Refresh (reload) the list of endpoints from SL.
 * <li>Returns a proxy of specified service endpoint interface.
 * </ul>
 * 
 */
public class EndpointRetriever {

	private static final Logger LOG = Logger.getLogger(EndpointRetriever.class
			.getName());

	private List<String> endpointsList;
	private ServiceLocator serviceLocator;
	private QName serviceName;

	public EndpointRetriever() throws ServiceLocatorException,
			InterruptedException, IOException {
	}

	public void init() throws ServiceLocatorException, InterruptedException,
			IOException {
		if (serviceName == null)
			throw new NullPointerException("Service name can not be null");
		if (serviceLocator == null)
			throw new NullPointerException("Service locator can not be null");
		LOG.log(Level.INFO, "Creating EndpointRetriever object for "
				+ serviceName.toString() + " service...");

		endpointsList = receiveEndpointsList();
		if (!isEmptyList()) {
			if (LOG.isLoggable(Level.WARNING)) {
				LOG.log(Level.INFO,
						"Endpoint Retriever was initialized successfully.");
			}
		} else if (LOG.isLoggable(Level.WARNING)) {
			LOG.log(Level.WARNING,
					"Endpoint Retriever was initialized with empty list of endpoints.");
		}
	}

	public boolean isEmptyList() {
		return (endpointsList == null) || endpointsList.isEmpty();
	}

	public List<String> getEndpointsList() {
		if (LOG.isLoggable(Level.FINE)) {
			LOG.log(Level.FINE,
					"List of endpoints: " + endpointsList.toString());
		}
		return endpointsList;
	}

	/**
	 * List of endpoins replaced by new data from the
	 * {@link org.talend.esb.locator.ServiceLocator ServiceLocator}.
	 */
	public void refreshEndpointsList() {
		try {
			endpointsList = receiveEndpointsList();
			if (endpointsList == null) {
				LOG.log(Level.SEVERE, "Can not receive list of endpoint");
			}
		} catch (Exception e) {
			if (LOG.isLoggable(Level.SEVERE)) {
				LOG.log(Level.SEVERE,
						"Can not refresh list of endpoints due to unknown exception");
			}
		}
	}

	protected ServiceLocator createServiceLocator() {
		return new ServiceLocator();
	}

	public void setServiceLocator(ServiceLocator serviceLocator) {
		this.serviceLocator = serviceLocator;
	}

	public void setServiceName(QName serviceName) {
		this.serviceName = serviceName;
	}

	/**
	 * Establish a connection to the Service Locator, using
	 * {@link org.talend.esb.locator.ServiceLocator ServiceLocator} instance.
	 * Lookup endpoints for Service Name, specified specified in
	 * {@link #EndpointResolver(QName, String) constructor}. Disconnects from a
	 * Service Locator server.
	 * 
	 * @return all endpoints that currently registered at the Service Locator
	 *         Service.
	 * @throws ServiceLocatorException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	private List<String> receiveEndpointsList() throws ServiceLocatorException,
			InterruptedException, IOException {
		LOG.log(Level.INFO, "Getting endpoints of " + serviceName.toString()
				+ " service.");
	
		List<String> endpointsList = null;
	
		try {
			serviceLocator.connect();
			endpointsList = serviceLocator.lookup(this.serviceName);
			serviceLocator.disconnect();
			LOG.log(Level.INFO,
					"Received list of endpoints: " + endpointsList.toString());
		} catch (ServiceLocatorException e) {
			if (LOG.isLoggable(Level.SEVERE)) {
				LOG.log(Level.SEVERE,
						"Failed to execute an request to Service Locator", e);
				throw new ServiceLocatorException(
						"Failed to execute an request to Service Locator");
			}
		} catch (InterruptedException e) {
			if (LOG.isLoggable(Level.SEVERE)) {
				LOG.log(Level.SEVERE,
						"An InterruptedException was thrown while waiting for an answer from the Service Locator",
						e);
				throw new InterruptedException(e.getMessage());
			}
		} catch (IOException e) {
			if (LOG.isLoggable(Level.SEVERE)) {
				LOG.log(Level.SEVERE,
						"An IOException  was thrown when trying to connect to the ServiceLocator",
						e);
				throw new IOException(e.getMessage());
			}
		}
		return endpointsList;
	}
}
