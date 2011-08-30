package org.talend.esb.examples.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.karaf.features.management.FeaturesServiceMBean;
import org.talend.esb.examples.ApplicationManager;

public class ApplicationManagerImpl implements ApplicationManager {

	public JMXConnector createRMIconnector(String serviceUrl,
			HashMap<String, String[]> environment)
			throws MalformedURLException, IOException {
		echo("\nCreate an RMI connector client and "
				+ "connect it to the RMI connector server");
		JMXServiceURL url = new JMXServiceURL(serviceUrl);
		JMXConnector jmxc = JMXConnectorFactory.connect(url, environment);
		return jmxc;
	}

	public MBeanServerConnection getMBeanServerConnection(JMXConnector jmxc)
			throws IOException {
		echo("\nGet an MBeanServerConnection");
		MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
		return mbsc;
	}

	public FeaturesServiceMBean createProxy(MBeanServerConnection mbsc)
			throws MalformedObjectNameException, NullPointerException {
		echo("\n>>> Perform operations on Tesb MBean <<<");
		ObjectName mbeanName = new ObjectName(
				"org.apache.karaf:type=features,name=tesb");
		FeaturesServiceMBean mbeanProxy = JMX.newMBeanProxy(mbsc, mbeanName,
				FeaturesServiceMBean.class, false);
		return mbeanProxy;
	}

	public HashMap<String, String[]> authorize() {
		echo("\nInitialize the environment map");
		HashMap<String, String[]> env = new HashMap<String, String[]>();
		String[] credentials = new String[] { "karaf", "karaf" };
		env.put("jmx.remote.credentials", credentials);
		return env;
	}

	public void addRepository(FeaturesServiceMBean mbeanProxy) throws Exception {
		mbeanProxy
				.addRepository("mvn:com.talend.if.examples/osgi/1.0/xml/features");
	}

	public void closeConnection(JMXConnector jmxc) throws IOException {
		echo("\nClose the connection to the server");
		jmxc.close();
		echo("\nBye! Bye!");
	}

	public void uninstallFeature(FeaturesServiceMBean mbeanProxy)
			throws Exception {
		mbeanProxy.uninstallFeature("tif-example-claimcheck");
	}

	public void removeRepository(FeaturesServiceMBean mbeanProxy)
			throws Exception {
		mbeanProxy
				.removeRepository("mvn:com.talend.if.examples/osgi/1.0/xml/features");
	}

	public void installFeature(FeaturesServiceMBean mbeanProxy)
			throws Exception {
		mbeanProxy.installFeature("tif-example-claimcheck");
	}

	private static void echo(String msg) {
		System.out.println(msg);
	}

}
