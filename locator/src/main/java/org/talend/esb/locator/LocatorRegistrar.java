package org.talend.esb.locator;

import java.util.List;

import javax.xml.namespace.QName;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.endpoint.ServerLifeCycleListener;
import org.apache.cxf.endpoint.ServerLifeCycleManager;
import org.apache.cxf.endpoint.ServerRegistry;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.service.model.ServiceInfo;

public class LocatorRegistrar implements ServerLifeCycleListener, ServiceLocator.PostConnectAction {

	private Bus bus;
	
	private ServiceLocator lc;
	
	public LocatorRegistrar() {
		System.out.println("Locator Client created.");
	}

	public void setBus(Bus bus) {
		if(this.bus != bus) {
			this.bus = bus;
			registerListener();
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
			System.out.println("Server available with endpoint " + server.getEndpoint().getEndpointInfo().getAddress());
		}
	}
	
	public void setLocatorClient(ServiceLocator locatorClient) {
		lc = locatorClient;
		lc.setPostConnectAction(this);
	}

	@Override
	public void startServer(Server server) {
		System.out.println("Server " + server + " started...");
		
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
		System.out.println("Server " + server + " stopped.");
	}

	private void  registerListener() {
        ServerLifeCycleManager manager = bus.getExtension(ServerLifeCycleManager.class);
        if (manager != null) {
            manager.registerListener(this);
        }
	}
	
	private void registerEndpoint(Server server) throws ServiceLocatorException, InterruptedException {
		EndpointInfo eInfo = server.getEndpoint().getEndpointInfo();
		ServiceInfo serviceInfo = eInfo.getService();
		QName serviceName = serviceInfo.getName();
		String endpointAddress = eInfo.getAddress();
		
		System.out.println("Service name: " + serviceName);	
		System.out.println("Endpoint Address: " + endpointAddress);
		
		lc.register(serviceName, endpointAddress);
	}

	@Override
	public void process(ServiceLocator lc) {
		registerAvailableServers();
	}
}
