package org.talend.esb.sam.server.listener;

import java.net.InetAddress;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.derby.drda.NetworkServerControl;

public class DerbyStarterContextListener implements ServletContextListener {
	private NetworkServerControl server;
	private boolean startDerby;

	public DerbyStarterContextListener() {
		startDerby = false;
		String startDerbyProperty = System.getProperty("org.talend.esb.sam.server.embedded");
		if ((startDerbyProperty != null)
				&& (startDerbyProperty.toUpperCase().equals("TRUE"))) {
			startDerby = true;
		}
	}

	public void contextDestroyed(ServletContextEvent arg0) {
		if (startDerby) {
			try {
				server.shutdown();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void contextInitialized(ServletContextEvent arg0) {
		if (startDerby) {
			try {
				server = new NetworkServerControl(
						InetAddress.getByName("localhost"), 1527);
				server.start(null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}