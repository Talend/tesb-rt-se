package org.talend.esb.examples;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.remote.JMXConnector;

import org.apache.karaf.features.management.FeaturesServiceMBean;

public interface ApplicationManager {

	public HashMap<String, String[]> authorize();

	public JMXConnector createRMIconnector(String serviceUrl,
			HashMap<String, String[]> environment)
			throws MalformedURLException, IOException;

	public MBeanServerConnection getMBeanServerConnection(JMXConnector jmxc)
			throws IOException;

	public FeaturesServiceMBean createProxy(MBeanServerConnection mbsc)
			throws MalformedObjectNameException, NullPointerException;

	public void addRepository(FeaturesServiceMBean mbeanProxy) throws Exception;

	public void closeConnection(JMXConnector jmxc) throws IOException;

	public void uninstallFeature(FeaturesServiceMBean mbeanProxy)
			throws Exception;

	public void removeRepository(FeaturesServiceMBean mbeanProxy)
			throws Exception;

	public void installFeature(FeaturesServiceMBean mbeanProxy)
			throws Exception;

}
