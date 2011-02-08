package org.talend.esb.locator;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.cxf.Bus;
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

	private Bus bus;

	private ServiceLocator lc;

	private String endpointPrefix = "";// "http://localhost:8081";

	public LocatorRegistrar() {
		if (LOG.isLoggable(Level.INFO)) {
			LOG.log(Level.INFO, "Locator Client created.");
		}
	}

	public void init() {
		if (LOG.isLoggable(Level.FINE)) {
			LOG.log(Level.FINE, "Registering listener.");
		}
		registerListener();
		if (LOG.isLoggable(Level.FINE)) {
			LOG.log(Level.FINE, "Registering available services.");
		}
		registerAvailableServers();

	}

	private void registerAvailableServers() {
		ServerRegistry serverRegistry = bus.getExtension(ServerRegistry.class);
		List<Server> servers = serverRegistry.getServers();
		for (Server server : servers) {
			try {
				registerEndpoint(server);
				if (LOG.isLoggable(Level.INFO)) {
					LOG.log(Level.INFO, "Server available with endpoint "
							+ server.getEndpoint().getEndpointInfo()
									.getAddress());
				}
			} catch (ServiceLocatorException e) {
				if (LOG.isLoggable(Level.SEVERE)) {
					LOG.log(Level.SEVERE,
							"ServiceLocator Exception thrown during register endpoint. "
									+ e.getMessage());
				}
			} catch (InterruptedException e) {
				if (LOG.isLoggable(Level.SEVERE)) {
					LOG.log(Level.SEVERE,
							"InterruptedException thrown during register endpoint. "
									+ e.getMessage());
				}
			}
		}
	}

	public void setBus(Bus bus) {
		this.bus = bus;
	}

	public void setEndpointPrefix(String endpointPrefix) {
		this.endpointPrefix = endpointPrefix;
	}

	public void setLocatorClient(ServiceLocator locatorClient) {
		lc = locatorClient;
		lc.setPostConnectAction(this);
		if (LOG.isLoggable(Level.FINE)) {
			LOG.log(Level.FINE, "Locator client was setted.");
		}
	}

	@Override
	public void startServer(Server server) {
		if (LOG.isLoggable(Level.INFO)) {
			LOG.log(Level.INFO, "Server " + server + " started...");
		}
		try {
			registerEndpoint(server);
		} catch (ServiceLocatorException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stopServer(Server server) {
		if (LOG.isLoggable(Level.INFO)) {
			LOG.log(Level.INFO, "Server " + server + " stopped...");
		}
		try {
			unregisterEndpoint(server);
		} catch (ServiceLocatorException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	private void registerListener() {
		ServerLifeCycleManager manager = bus
				.getExtension(ServerLifeCycleManager.class);
		if (manager != null) {
			manager.registerListener(this);
			if (LOG.isLoggable(Level.INFO)) {
				LOG.log(Level.INFO, "Listener was registered.");
			}
		}
	}

	private void registerEndpoint(Server server)
			throws ServiceLocatorException, InterruptedException {
		EndpointInfo eInfo = server.getEndpoint().getEndpointInfo();
		ServiceInfo serviceInfo = eInfo.getService();
		QName serviceName = serviceInfo.getName();
		String endpointAddress = endpointPrefix + eInfo.getAddress();

		if (LOG.isLoggable(Level.INFO)) {
			LOG.log(Level.INFO, "Service name: " + serviceName);
			LOG.log(Level.INFO, "Endpoint Address: " + endpointAddress);
		}
		lc.register(serviceName, endpointAddress);
		if (LOG.isLoggable(Level.INFO)) {
			LOG.log(Level.INFO, "Service was registered in ZooKeeper.");
		}
	}

	private void unregisterEndpoint(Server server)
			throws ServiceLocatorException, InterruptedException {
		EndpointInfo eInfo = server.getEndpoint().getEndpointInfo();
		ServiceInfo serviceInfo = eInfo.getService();
		QName serviceName = serviceInfo.getName();
		String endpointAddress = endpointPrefix + eInfo.getAddress();

		if (LOG.isLoggable(Level.INFO)) {
			LOG.log(Level.INFO, "Service name: " + serviceName);
			LOG.log(Level.INFO, "Endpoint Address: " + endpointAddress);
		}
		lc.unregister(serviceName, endpointAddress);
		if (LOG.isLoggable(Level.INFO)) {
			LOG.log(Level.INFO,
					"Service was unregistered from ZooKeeper. Service name: "
							+ serviceName + " Endpoint Address: "
							+ endpointAddress);
		}
	}

	@Override
	public void process(ServiceLocator lc) {
		registerAvailableServers();
	}
}
