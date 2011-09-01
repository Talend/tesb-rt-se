package org.talend.esb.examples.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;

import javax.management.InstanceNotFoundException;
import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.karaf.features.management.FeaturesServiceMBean;
import org.osgi.jmx.framework.FrameworkMBean;
import org.talend.esb.examples.ApplicationManager;
import org.talend.esb.examples.ClientListener;

public class ApplicationManagerImpl implements ApplicationManager {

	private ClientListener clientListener;
	
	public JMXConnector createRMIconnector(String serviceUrl,
			HashMap<String, String[]> environment)
			throws MalformedURLException, IOException {
		JMXServiceURL url = new JMXServiceURL(serviceUrl);
		JMXConnector jmxc = JMXConnectorFactory.connect(url, environment);
		return jmxc;
	}

	public void closeConnection(JMXConnector jmxc) throws IOException {
		jmxc.close();
	}

	public MBeanServerConnection getMBeanServerConnection(JMXConnector jmxc)
			throws IOException {
		MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
		return mbsc;
	}

	public FeaturesServiceMBean createFeaturesServiceMBeanProxy(
			MBeanServerConnection mbsc) throws MalformedObjectNameException,
			NullPointerException, InstanceNotFoundException, IOException {
		ObjectName mbeanName = new ObjectName(
				"org.apache.karaf:type=features,name=tesb");
		FeaturesServiceMBean featuresServiceMBeanProxy = JMX.newMBeanProxy(
				mbsc, mbeanName, FeaturesServiceMBean.class, true);

		mbsc.addNotificationListener(mbeanName, clientListener, null, null);

		return featuresServiceMBeanProxy;
	}

	public FrameworkMBean createOsgiFrameworkMBeanProxy(
			MBeanServerConnection mbsc) throws MalformedObjectNameException,
			NullPointerException {
		ObjectName mbeanName = new ObjectName(
				"osgi.core:type=framework,version=1.5");
		FrameworkMBean osgiFrameworkProxy = JMX.newMBeanProxy(mbsc, mbeanName,
				FrameworkMBean.class, false);	
		return osgiFrameworkProxy;
	}

	public void addRepository(FeaturesServiceMBean featuresServiceMBeanProxy,
			String url) throws Exception {
		featuresServiceMBeanProxy.addRepository(url);
	}

	public void removeRepository(
			FeaturesServiceMBean featuresServiceMBeanProxy, String url)
			throws Exception {
		featuresServiceMBeanProxy.removeRepository(url);
	}

	public void installFeature(FeaturesServiceMBean featuresServiceMBeanProxy,
			String featureName) throws Exception {
		featuresServiceMBeanProxy.installFeature(featureName);
	}

	public void uninstallFeature(
			FeaturesServiceMBean featuresServiceMBeanProxy, String featureName)
			throws Exception {
		featuresServiceMBeanProxy.uninstallFeature(featureName);
	}

	public void startBundle(FrameworkMBean osgiFrameworkProxy, long bundleNumber)
			throws Exception {
		osgiFrameworkProxy.startBundle(bundleNumber);
	}

	public void stopBundle(FrameworkMBean osgiFrameworkProxy, long bundleNumber)
			throws Exception {
		osgiFrameworkProxy.stopBundle(bundleNumber);
	}

	public ClientListener getClientListener() {
		return clientListener;
	}

	public void setClientListener(ClientListener clientListener) {
		this.clientListener = clientListener;
	}

}
