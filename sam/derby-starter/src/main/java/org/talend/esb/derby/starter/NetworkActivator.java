package org.talend.esb.derby.starter;

import java.net.InetAddress;

import org.apache.derby.drda.NetworkServerControl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class NetworkActivator implements BundleActivator {
	NetworkServerControl server;
	
	public void start(BundleContext context) throws Exception {
		server = new NetworkServerControl(InetAddress.getByName("localhost"),1527);
		server.start(null);
	}
	
    public void stop(BundleContext context) throws Exception {
		server.shutdown();
    }

}
