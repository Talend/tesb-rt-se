package org.talend.esb.locator.sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.Endpoint;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.talend.esb.locator.LocatorRegistrar;

public class Server {

	private LocatorRegistrar locatorRegistrar;

	private Bus bus = BusFactory.getDefaultBus();

	private List<String> serverPorts = new ArrayList<String>();

	private Map<String, Endpoint> endpoints = new HashMap<String, Endpoint>();

	public static String address = "/services/Greeter";

	private String prefix = "";

	private boolean addJMS = false;

	protected Server() throws Exception {
	}

	public void init() {

		System.out.println("Starting Server on port " + serverPorts.toString()
				+ "...");

		for (String el : serverPorts) {
			prefix = "http://" + Constants.ServiceHOST + ":" + el;
			publishService(prefix, address);
		}
		if (addJMS) {
			publishService(Constants.JMS_ENDPOINT_URI, "");
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

	public void stopAll(List<String> serverPorts) {
		for (String el : serverPorts) {
			prefix = "http://" + Constants.ServiceHOST + ":" + el;
			address = "/services/Greeter";
			stopService(prefix, address);
		}
		System.out.println("Server stopped");

	}

	public LocatorRegistrar getLocatorRegistrar() {
		return locatorRegistrar;
	}

	public void setLocatorRegistrar(LocatorRegistrar locatorRegistrar) {
		this.locatorRegistrar = locatorRegistrar;
	}

	public Bus getBus() {
		return bus;
	}

	public void setBus(Bus bus) {
		this.bus = bus;
	}

	public void setServerPorts(List<String> serverPorts) {
		this.serverPorts = serverPorts;
	}

	public static void main(String args[]) throws Exception {
		// String locatorEndpoints = Constants.LOCATORENDPOINT;
		List<String> serverPorts = new ArrayList<String>();
		serverPorts.add("8080");
		serverPorts.add("8081");
		serverPorts.add("8082");
		int i = 0;
		if (args.length > 0)
			serverPorts.clear();
		while (i < args.length) {
			// if (args[i].equals("-l")) {
			// locatorEndpoints = args[i + 1];
			// i += 2;
			// } else
			if (args[i].equals("-p")) {
				serverPorts.add(args[i + 1]);
				i += 2;
			}
		}

		Server serv = new Server();
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "WEB-INF/beans.xml" });
		
		serv.setServerPorts(serverPorts);

		serv.init();

		System.out.println("Server ready...");

		Thread.sleep(100 * 30 * 1000);

		serv.stopAll(serverPorts);

		context.close();
		
		System.out.println("Server exiting");

		System.exit(0);
	}
}
