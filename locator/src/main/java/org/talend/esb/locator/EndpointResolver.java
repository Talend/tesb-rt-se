package org.talend.esb.locator;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

import javax.xml.namespace.QName;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.jms.spec.JMSSpecConstants;

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
public class EndpointResolver {

	private static final Logger LOG = Logger.getLogger(EndpointResolver.class
			.getName());

	private List<String> endpointsList;
	private ServiceLocator serviceLocator;
	private QName serviceName;

	/**
	 * 
	 * @param serviceName
	 *            the name of the service for which to get the endpoints, must
	 *            not be <code>null</code>
	 * @param locatorEndpoints
	 *            the hosts where ZooKeeper started. Specify ZooKeeper address
	 *            and port. You can specify multiple hosts to which you want to
	 *            connect, separated by a comma.
	 */
	public EndpointResolver(QName serviceName, String locatorEndpoints) {
		LOG.log(Level.INFO, "Creating EndpointResolver object for "
				+ serviceName.toString() + " service.");

		this.serviceName = serviceName;
		serviceLocator = createServiceLocator();
		serviceLocator.setLocatorEndpoints(locatorEndpoints);
		endpointsList = receiveEndpointsList();
		if (isReady())
			LOG.log(Level.INFO, "Endpoint Resolver was created successfully.");
		else if (LOG.isLoggable(Level.SEVERE)) {
			LOG.log(Level.SEVERE, "Failed to create Endpoint Resolver");
		}
	}

	public boolean isReady() {
		return endpointsList != null;
	}

	/**
	 * Establish a connection to the Service Locator, using
	 * {@link org.talend.esb.locator.ServiceLocator ServiceLocator} instance. Lookup endpoints
	 * for Service Name, specified specified in {@link #EndpointResolver(QName, String) constructor}. Disconnects from a Service Locator
	 * server.
	 * 
	 * @return all endpoints that currently registered at the Service Locator
	 *         Service.
	 */
	private List<String> receiveEndpointsList() {
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
			}
		} catch (InterruptedException e) {
			if (LOG.isLoggable(Level.SEVERE)) {
				LOG.log(Level.SEVERE,
						"An InterruptedException was thrown while waiting for an answer from the Service Locator",
						e);
			}
		} catch (IOException e) {
			if (LOG.isLoggable(Level.SEVERE)) {
				LOG.log(Level.SEVERE,
						"An IOException  was thrown when trying to connect to the ServiceLocator",
						e);
			}
		}
		return endpointsList;
	}

	/**
	 * Select endpoint from the list  to be used by just using a simple
	 * strategy to randomly pick one entry from the list.
	 */
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

	/**
	 * The getPort method select the endpoint from the list of endpoins and use
	 * it to returns a proxy. A service client uses this proxy to invoke
	 * operations on the target service. The serviceEndpointInterface specifies
	 * the service endpoint interface that is supported by the created dynamic
	 * proxy instance.
	 * 
	 * @param portname
	 *            Qualified name for the target service endpoint.
	 * 
	 * @param serviceEndpointInterface
	 *            Service endpoint interface supported by the dynamic proxy
	 *            instance.
	 * 
	 * @return Object Proxy instance that supports the specified service
	 *         endpoint interface.
	 */
	public <T> T getPort(QName portname, Class<T> serviceEndpointInterface) {
		Service service = null;
		String endpoint = selectEndpoint();

		if (endpoint == null) {
			if (LOG.isLoggable(Level.SEVERE)) {
				LOG.log(Level.SEVERE, "Endpoint not found for service "
						+ this.serviceName.toString());
			}
		} else {
			try {
				Pattern pattern = Pattern.compile("^jms:",
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
				Matcher matcher = pattern.matcher(endpoint);
				if (matcher.find()) {
					JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
					factory.setTransportId(JMSSpecConstants.SOAP_JMS_SPECIFICATION_TRANSPORTID);
					factory.setAddress(endpoint);
					return factory.create(serviceEndpointInterface);
				}
				service = Service.create(this.serviceName);
				service.addPort(portname, SOAPBinding.SOAP11HTTP_BINDING,
						endpoint);
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
	
	public QName getServiceName() {
		LOG.log(Level.INFO, "Service name: " + serviceName.toString());
		return serviceName;
	}
	
	protected ServiceLocator createServiceLocator() {
		return new ServiceLocator();
	}
}
