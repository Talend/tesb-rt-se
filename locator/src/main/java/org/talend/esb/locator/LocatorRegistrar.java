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

public class LocatorRegistrar implements ServerLifeCycleListener, ServiceLocator.PostConnectAction {

	private static final Logger LOG = Logger.getLogger(LocatorRegistrar.class.getPackage().getName());
	
	private Bus bus;
	
	private ServiceLocator lc;
	
	public LocatorRegistrar() {
		LOG.log(Level.INFO, "Locator Client created."); 
		//System.out.println("Locator Client created.");
	}

	public void setBus(Bus bus) {
		if(this.bus != bus) {
			this.bus = bus;
			LOG.log(Level.FINE, "Registering listener."); 
			registerListener();
			LOG.log(Level.FINE, "Registering available services."); 
			registerAvailableServers();
		}
	}

	private void registerAvailableServers() {
		ServerRegistry serverRegistry = bus.getExtension(ServerRegistry.class);
		List<Server> servers = serverRegistry.getServers();
		for (Server server : servers) {
			try {
				registerEndpoint(server);
			} catch (ServiceLocatorException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			LOG.log(Level.INFO, "Server available with endpoint " + server.getEndpoint().getEndpointInfo().getAddress()); 
			//System.out.println("Server available with endpoint " + server.getEndpoint().getEndpointInfo().getAddress());
		}
	}
	
	public void setLocatorClient(ServiceLocator locatorClient) {
		lc = locatorClient;
		lc.setPostConnectAction(this);
		LOG.log(Level.FINE, "Locator client was setted."); 
	}

	@Override
	public void startServer(Server server) {
		//System.out.println("Server " + server + " started...");
		LOG.log(Level.INFO, "Server " + server + " started..."); 
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
		LOG.log(Level.INFO, "Server " + server + " stopped."); 
		//System.out.println("Server " + server + " stopped.");
	}

	private void  registerListener() {
		ServerLifeCycleManager manager = bus.getExtension(ServerLifeCycleManager.class);
        if (manager != null) {
            manager.registerListener(this);
            LOG.log(Level.INFO, "Listener was registered."); 
        }
	}
	
	private void registerEndpoint(Server server) throws ServiceLocatorException, InterruptedException {
		EndpointInfo eInfo = server.getEndpoint().getEndpointInfo();
		ServiceInfo serviceInfo = eInfo.getService();
		QName serviceName = serviceInfo.getName();
		String endpointAddress = eInfo.getAddress();
		
		LOG.log(Level.INFO, "Service name: " + serviceName); 
		LOG.log(Level.INFO, "Endpoint Address: " + endpointAddress); 
		//System.out.println("Service name: " + serviceName);	
		//System.out.println("Endpoint Address: " + endpointAddress);		
		lc.register(serviceName, endpointAddress);
		LOG.log(Level.INFO, "Service was registered in ZooKeeper."); 		
	}

	@Override
	public void process(ServiceLocator lc) {
		registerAvailableServers();
	}
}
