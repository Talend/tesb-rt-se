package org.talend.esb.locator;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.endpoint.ServerLifeCycleListener;
import org.apache.cxf.endpoint.ServerLifeCycleManager;
import org.apache.cxf.endpoint.ServerRegistry;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.service.model.ServiceInfo;

public class LocatorRegistrar implements ServerLifeCycleListener,
		ServiceLocator.PostConnectAction {

	private static final Logger LOG = Logger.getLogger(LocatorRegistrar.class
			.getPackage().getName());

	private Bus bus = BusFactory.getDefaultBus();

	private ServiceLocator locatorClient;

	private String endpointPrefix = "";// "http://localhost:8081";

	public LocatorRegistrar() {
		if (LOG.isLoggable(Level.INFO)) {
			LOG.log(Level.INFO, "Locator Client created.");
		}
	}

	@Override
	public void startServer(Server server) {
		if (LOG.isLoggable(Level.FINE)) {
			LOG.log(Level.FINE, "Server started...");
		}
		try {
			registerEndpoint(server);
		} catch (ServiceLocatorException e) {
			if (LOG.isLoggable(Level.SEVERE)) {
				LOG.log(Level.SEVERE,
						"ServiceLocator Exception thrown during register endpoint. "
								+ e.getMessage());
			}
		} catch (InterruptedException e) {
			if (LOG.isLoggable(Level.SEVERE)) {
				LOG.log(Level.SEVERE,
						"Interrupted Exception thrown during register endpoint. "
								+ e.getMessage());
			}
		}
	}

	@Override
	public void stopServer(Server server) {
		if (LOG.isLoggable(Level.FINE)) {
			LOG.log(Level.FINE, "Server stopped...");
		}
		try {
			unregisterEndpoint(server);
		} catch (ServiceLocatorException e) {
			if (LOG.isLoggable(Level.SEVERE)) {
				LOG.log(Level.SEVERE,
						"ServiceLocator Exception thrown during unregister endpoint. ", e);
			}
		} catch (InterruptedException e) {
			if (LOG.isLoggable(Level.SEVERE)) {
				LOG.log(Level.SEVERE,
						"Interrupted Exception thrown during unregister endpoint.", e);
			}
		}
	}

	@Override
	public void process(ServiceLocator lc) {
		registerAvailableServers();
	}

	public void init() throws IOException, InterruptedException, ServiceLocatorException {
		locatorClient.connect();
		if (LOG.isLoggable(Level.FINE)) {
			LOG.log(Level.FINE, "Registering listener...");
		}
		registerListener();
		if (LOG.isLoggable(Level.FINE)) {
			LOG.log(Level.FINE, "Registering available services...");
		}
		registerAvailableServers();

	}

	public void setBus(Bus bus) {
		this.bus = bus;
	}

	public void setEndpointPrefix(String endpointPrefix) {
		this.endpointPrefix = endpointPrefix!=null?endpointPrefix:"";
	}

	public void setLocatorClient(ServiceLocator locatorClient) {
		this.locatorClient = locatorClient;
		locatorClient.setPostConnectAction(this);
		if (LOG.isLoggable(Level.FINE)) {
			LOG.log(Level.FINE, "Locator client was set.");
		}
	}

	public ServiceLocator getLocatorClient() {
		return locatorClient;
	}

	private void registerAvailableServers() {
		ServerRegistry serverRegistry = bus.getExtension(ServerRegistry.class);
		List<Server> servers = serverRegistry.getServers();
		for (Server server : servers) {
			try {
				registerEndpoint(server);
				if (LOG.isLoggable(Level.FINE)) {
					LOG.log(Level.FINE, "Server available with endpoint "
							+ server.getEndpoint().getEndpointInfo()
									.getAddress());
				}
			} catch (ServiceLocatorException e) {
				if (LOG.isLoggable(Level.SEVERE)) {
					LOG.log(Level.SEVERE,
							"ServiceLocator Exception thrown while registering endpoint.", e);
				}
			} catch (InterruptedException e) {
				if (LOG.isLoggable(Level.SEVERE)) {
					LOG.log(Level.SEVERE,
							"InterruptedException thrown while registering endpoint.", e);
				}
			}
		}
	}

	private void registerListener() {
		ServerLifeCycleManager manager = bus
				.getExtension(ServerLifeCycleManager.class);
		if (manager != null) {
			manager.registerListener(this);
			if (LOG.isLoggable(Level.FINER)) {
				LOG.log(Level.FINER, "Listener was registered.");
			}
		}
	}

	private void registerEndpoint(Server server)
			throws ServiceLocatorException, InterruptedException {
		EndpointInfo eInfo = server.getEndpoint().getEndpointInfo();
		ServiceInfo serviceInfo = eInfo.getService();
		QName serviceName = serviceInfo.getName();
		String endpointAddress = endpointPrefix + eInfo.getAddress();

		if (LOG.isLoggable(Level.FINE)) {
			LOG.log(Level.FINE, "Service name: " + serviceName);
			LOG.log(Level.FINE, "Endpoint Address: " + endpointAddress);
		}
		locatorClient.register(serviceName, endpointAddress);
		if (LOG.isLoggable(Level.FINE)) {
			LOG.log(Level.FINE, "Service was registered in ZooKeeper.");
		}
	}

	private void unregisterEndpoint(Server server)
			throws ServiceLocatorException, InterruptedException {
		EndpointInfo eInfo = server.getEndpoint().getEndpointInfo();
		ServiceInfo serviceInfo = eInfo.getService();
		QName serviceName = serviceInfo.getName();
		String endpointAddress = endpointPrefix + eInfo.getAddress();

		if (LOG.isLoggable(Level.FINEST)) {
			LOG.log(Level.FINEST, "Service name: " + serviceName);
			LOG.log(Level.FINEST, "Endpoint Address: " + endpointAddress);
		}
		locatorClient.unregister(serviceName, endpointAddress);
		if (LOG.isLoggable(Level.FINE)) {
			LOG.log(Level.FINE,
					"Service was unregistered from ZooKeeper. Service name: "
							+ serviceName + " Endpoint Address: "
							+ endpointAddress);
		}
	}
}
