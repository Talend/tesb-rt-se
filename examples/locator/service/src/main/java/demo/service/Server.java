package demo.service;

import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.talend.esb.locator.LocatorFeature;

import demo.common.Greeter;

public class Server {

	public static void main(String args[]) throws Exception {
		String port = "8080";
		if (args.length == 2 && args[0].equals("-p")) {
			port = args[1];
		}
		String address = String.format("http://localhost:%1$s/services/Greeter", port);
		
		LocatorFeature locatorFeature = new LocatorFeature();
		locatorFeature.setLocatorEndpoints("localhost:2181");

		Greeter greeterService = new GreeterImpl();
		JaxWsServerFactoryBean server = new JaxWsServerFactoryBean();
		server.setServiceBean(greeterService);
		server.setAddress(address);
		
		server.getFeatures().add(locatorFeature);
		server.create();
		System.out.println("Server ready....");

		Thread.sleep(30 * 60 * 1000);

		System.out.println("Server exiting");
		System.exit(0);
	}
}
