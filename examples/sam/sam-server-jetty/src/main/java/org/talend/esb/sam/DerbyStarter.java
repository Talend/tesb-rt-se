package org.talend.esb.sam;

import java.net.InetAddress;


import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


import org.apache.derby.drda.NetworkServerControl;

public class DerbyStarter implements ServletContextListener{
	NetworkServerControl server;
	
	public void contextDestroyed(ServletContextEvent arg0) {
		try {
			server.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

	public void contextInitialized(ServletContextEvent arg0) {
		
		try {
			server = new NetworkServerControl(InetAddress.getByName("localhost"),1527);
			server.start(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
