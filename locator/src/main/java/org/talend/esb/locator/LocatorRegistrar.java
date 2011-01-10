package org.talend.esb.locator;

import java.util.List;

import javax.xml.namespace.QName;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.endpoint.ServerLifeCycleListener;
import org.apache.cxf.endpoint.ServerLifeCycleManager;
import org.apache.cxf.endpoint.ServerRegistry;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.service.model.ServiceInfo;
import org.apache.cxf.transport.Destination;
import org.apache.cxf.ws.addressing.AttributedURIType;
import org.apache.cxf.ws.addressing.EndpointReferenceType;
import org.apache.zookeeper.KeeperException;

public class LocatorRegistrar implements ServerLifeCycleListener, LocatorClient.PostConnectAction {

	private Bus bus;
	
	private LocatorClient lc;
	
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
			} catch (KeeperException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Server available with endpoint " + server.getEndpoint().getEndpointInfo().getAddress());
		}
	}
	
	public void setLocatorClient(LocatorClient locatorClient) {
		lc = locatorClient;
		lc.setPostConnectAction(this);
	}

	@Override
	public void startServer(Server server) {
		System.out.println("Server " + server + " started...");
		
		try {
			registerEndpoint(server);
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Endpoint endpoint = server.getEndpoint();
		EndpointInfo eInfo = endpoint.getEndpointInfo();
		System.out.println("Address of endpoint: " + eInfo.getAddress());

/*
		ServiceInfo serviceInfo = eInfo.getService();
		System.out.println("ServiceInfo - service name: " + serviceInfo.getName());
		System.out.println("ServiceInfo - endpoints: " + serviceInfo.getEndpoints());
*/		
		Destination destination = server.getDestination();
		EndpointReferenceType eprt = destination.getAddress();
		AttributedURIType uri = eprt.getAddress();
		System.out.println("EndpointReference of destination is " + uri.getValue());
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
	
	private void registerEndpoint(Server server) throws KeeperException, InterruptedException {
		EndpointInfo eInfo = server.getEndpoint().getEndpointInfo();
		ServiceInfo serviceInfo = eInfo.getService();
		QName serviceName = serviceInfo.getName();
		String endpointAddress = eInfo.getAddress();
		
		System.out.println("Service name: " + serviceName);	
		System.out.println("Endpoint Address: " + endpointAddress);
		
		lc.register(serviceName, endpointAddress);
	}

	@Override
	public void process(LocatorClient lc) {
		registerAvailableServers();
	}
}
