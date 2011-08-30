package org.talend.esb.examples;

import java.util.HashMap;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import org.apache.karaf.features.management.FeaturesServiceMBean;

import org.talend.esb.examples.impl.ApplicationManagerImpl;

public class Client {

	public static void main(String args[]) {

		try {

			ApplicationManagerImpl applicationManager = new ApplicationManagerImpl();
			
			HashMap<String, String[]> env = applicationManager.authorize();
			
			JMXConnector jmxc = applicationManager.createRMIconnector(
					"service:jmx:rmi://localhost:44444/jndi/rmi://localhost:1099/karaf-tesb",
					env);

			MBeanServerConnection mbsc = applicationManager.getMBeanServerConnection(jmxc);

			FeaturesServiceMBean mbeanProxy = applicationManager.createProxy(mbsc);

			applicationManager.addRepository(mbeanProxy);

			applicationManager.installFeature(mbeanProxy);

			applicationManager.uninstallFeature(mbeanProxy);
			
			applicationManager.removeRepository(mbeanProxy);

			applicationManager.closeConnection(jmxc);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}