package org.talend.esb.locator.sample;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        String address = "http://192.168.40.15:" + serverPort + "/services/Greeter";

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
    	String locatorEndpoints = "192.168.40.14:2181";
		List<String> serverPorts = new ArrayList<String>();
		serverPorts.add("8080");
		serverPorts.add("8081");
		serverPorts.add("8082");
		serverPorts.add("8083");
		serverPorts.add("8084");
		serverPorts.add("8085");
		serverPorts.add("8086");
		serverPorts.add("8087");
		serverPorts.add("8088");
		serverPorts.add("8089");
    	int i = 0;
    	if(args.length > 0) serverPorts.clear();
    	while(i < args.length) {
			if(args[i].equals("-l")) {
				locatorEndpoints = args[i + 1];
				i += 2;
			} else if(args[i].equals("-p")) {
				serverPorts.add(args[i + 1]);
				i += 2;
			}
		}		
		for(String serverPort: serverPorts) {			
			new Server(serverPort, locatorEndpoints);
		}
		
        System.out.println("Server ready...");

        Thread.sleep(125 * 60 * 1000);
        System.out.println("Server exiting");
        System.exit(0);
    }
}
