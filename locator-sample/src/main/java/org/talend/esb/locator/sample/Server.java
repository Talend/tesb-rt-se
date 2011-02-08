package org.talend.esb.locator.sample;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.Endpoint;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;

import org.talend.esb.locator.ServiceLocator;
import org.talend.esb.locator.LocatorRegistrar;
import org.talend.esb.locator.ServiceLocatorException;

public class Server {
	private ServiceLocator lc = new ServiceLocator();
	private LocatorRegistrar lr = new LocatorRegistrar();
	private Bus bus = BusFactory.getDefaultBus();

	private Map<String, Endpoint> endpoints = new HashMap<String, Endpoint>();

	private String address = "/services/Greeter";
	private String prefix = "";

	protected Server(List<String> serverPorts, String locatorEndpoints)
			throws Exception {
		System.out.println("Starting Server on port " + serverPorts.toString()
				+ "...");

		initForLocator(locatorEndpoints);
		for (String el : serverPorts) {
			prefix = "http://" + Constants.ServiceHOST + ":" + el;
			publishService(prefix, address);
		}
		System.out.println("Server started");
	}

	private void publishService(String prefix, String address) {
		Object implementor = new org.talend.esb.sample.cxf.GreeterImpl();
		Endpoint endpoint = Endpoint.create(implementor);
		endpoint.publish(prefix + address);
		endpoints.put(prefix + address, endpoint);
	}

	private void stopService(String prefix, String address) {
		Endpoint endpoint = endpoints.get(prefix + address);
		if (endpoint != null)
			endpoint.stop();
	}

	private void stopAll(List<String> serverPorts) {
		for (String el : serverPorts) {
			prefix = "http://" + Constants.ServiceHOST + ":" + el;
			address = "/services/Greeter";
			stopService(prefix, address);
		}
		System.out.println("Server stopped");

	}

	private void initForLocator(String locatorEndpoints) throws IOException,
			InterruptedException, ServiceLocatorException {
		lc.setLocatorEndpoints(locatorEndpoints);
		lc.setSessionTimeout(30 * 60 * 1000);
		lc.setConnectionTimeout(30 * 60 * 1000);
		lc.connect();
		lr.setLocatorClient(lc);
		lr.setBus(bus);
		lr.init();
	}

	public static void main(String args[]) throws Exception {
		String locatorEndpoints = Constants.LOCATORENDPOINT;
		List<String> serverPorts = new ArrayList<String>();
		serverPorts.add("8080");
		serverPorts.add("8081");
		serverPorts.add("8082");
		int i = 0;
		if (args.length > 0)
			serverPorts.clear();
		while (i < args.length) {
			if (args[i].equals("-l")) {
				locatorEndpoints = args[i + 1];
				i += 2;
			} else if (args[i].equals("-p")) {
				serverPorts.add(args[i + 1]);
				i += 2;
			}
		}
		Server serv = new Server(serverPorts, locatorEndpoints);

		System.out.println("Server ready...");

		Thread.sleep(19 * 1 * 1000);
		
		serv.stopAll(serverPorts);
		
		System.out.println("Server exiting");
		
		System.exit(0);
	}
}
