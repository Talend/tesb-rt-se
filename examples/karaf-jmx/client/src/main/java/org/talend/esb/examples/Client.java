package org.talend.esb.examples;

import java.util.HashMap;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.karaf.features.management.FeaturesServiceMBean;

public class Client {

	private static void echo(String msg) {
		System.out.println(msg);
	}

	public static void main(String args[]) {

		try {

			echo("\nInitialize the environment map");
			HashMap<String, String[]> env = new HashMap<String, String[]>();
			String[] credentials = new String[] { "karaf", "karaf" };
			env.put("jmx.remote.credentials", credentials);

			echo("\nCreate an RMI connector client and "
					+ "connect it to the RMI connector server");
			JMXServiceURL url = new JMXServiceURL(
					"service:jmx:rmi://localhost:44444/jndi/rmi://localhost:1099/karaf-tesb");
			JMXConnector jmxc = JMXConnectorFactory.connect(url, env);

			echo("\nGet an MBeanServerConnection");
			MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();

			// XXX connect to MBean through proxy: move to method
			echo("\n>>> Perform operations on Tesb MBean <<<");
			ObjectName mbeanName = new ObjectName(
					"org.apache.karaf:type=features,name=tesb");
			FeaturesServiceMBean mbeanProxy = JMX.newMBeanProxy(mbsc,
					mbeanName, FeaturesServiceMBean.class, false);

			// XXX feature:addUrl move
			mbeanProxy.addRepository("mvn:com.talend.if.examples/osgi/1.0/xml/features");

			// XXX move feature:install
			mbeanProxy.installFeature("tif-example-claimcheck");

			// XXX feature:removeUrl: move
			mbeanProxy.removeRepository("mvn:com.talend.if.examples/osgi/1.0/xml/features");

			// XXX move feature:uninstall
			mbeanProxy.uninstallFeature("tif-example-claimcheck");

			echo("\nClose the connection to the server");
			jmxc.close();
			echo("\nBye! Bye!");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}