package org.talend.esb.locator.sample;

import java.io.IOException;

import javax.xml.ws.Endpoint;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;

import org.talend.esb.locator.ServiceLocator;
import org.talend.esb.locator.LocatorRegistrar;
import org.talend.esb.locator.ServiceLocatorException;

public class Server {


    protected Server(String serverPort, String locatorEndpoints) throws Exception {
        System.out.println("Starting Server on port " + serverPort + "...");

//        String address = String.format("http://localhost:%1$/services/Greeter", serverPort);
        String address = "http://localhost:" + serverPort + "/services/Greeter";

        initForLocator(locatorEndpoints);
        publishService(address);

        System.out.println("Server started");
    }

	private void publishService(String address) {
		Object implementor = new org.talend.esb.sample.cxf.GreeterImpl();
        Endpoint endpoint = Endpoint.create(implementor);

        endpoint.publish(address);
	}

	private void initForLocator(String locatorEndpoints) throws IOException,
			InterruptedException, ServiceLocatorException {
		Bus bus = BusFactory.getDefaultBus();
 
        ServiceLocator lc = new ServiceLocator();
        lc.setLocatorEndpoints(locatorEndpoints);
        lc.setSessionTimeout(30 * 60* 1000);
        lc.setConnectionTimeout(30 * 60* 1000);
        lc.connect();
        
        LocatorRegistrar lr = new LocatorRegistrar();
        lr.setLocatorClient(lc);
        lr.setBus(bus);
	}

    public static void main(String args[]) throws Exception {
    	String locatorEndpoints = "localhost:2181";
		String serverPort = "8081";
    	int i = 0;
		while(i < args.length) {
			if(args[i].equals("-l")) {
				locatorEndpoints = args[i + 1];
				i += 2;
			} else if(args[i].equals("-p")) {
				serverPort = args[i + 1];
				i += 2;
			}
		}
        new Server(serverPort, locatorEndpoints);
        System.out.println("Server ready...");

        Thread.sleep(125 * 60 * 1000);
        System.out.println("Server exiting");
        System.exit(0);
    }
}
