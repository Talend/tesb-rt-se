package org.talend.esb.examples;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.remote.JMXConnector;

import org.apache.karaf.features.management.FeaturesServiceMBean;
import org.osgi.jmx.framework.FrameworkMBean;

public interface ApplicationManager {

	public JMXConnector createRMIconnector(String serviceUrl,
			HashMap<String, String[]> environment)
			throws MalformedURLException, IOException;

	public MBeanServerConnection getMBeanServerConnection(JMXConnector jmxc)
			throws IOException;

	public void closeConnection(JMXConnector jmxc) throws IOException;

	public FeaturesServiceMBean createFeaturesServiceMBeanProxy(
			MBeanServerConnection mbsc) throws MalformedObjectNameException,
			NullPointerException, InstanceNotFoundException, IOException;

	public FrameworkMBean createOsgiFrameworkMBeanProxy(
			MBeanServerConnection mbsc) throws MalformedObjectNameException,
			NullPointerException;

	public void addRepository(FeaturesServiceMBean featuresServiceMBeanProxy,
			String url) throws Exception;

	public void removeRepository(
			FeaturesServiceMBean featuresServiceMBeanProxy, String url)
			throws Exception;

	public void installFeature(FeaturesServiceMBean featuresServiceMBeanProxy,
			String featureName) throws Exception;

	public void uninstallFeature(
			FeaturesServiceMBean featuresServiceMBeanProxy, String featureName)
			throws Exception;

	public long startBundle(FrameworkMBean osgiFrameworkProxy, String bundleName)
			throws Exception;

	public void stopBundle(FrameworkMBean osgiFrameworkProxy, long bundleNumber)
			throws Exception;

}
