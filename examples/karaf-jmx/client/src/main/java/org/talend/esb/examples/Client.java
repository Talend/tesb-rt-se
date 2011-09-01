package org.talend.esb.examples;

import java.util.HashMap;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;

import org.apache.karaf.features.management.FeaturesServiceMBean;

import org.osgi.jmx.framework.FrameworkMBean;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Client {

	private static ApplicationManager applicationManager;
	private HashMap<String, String[]> environment;
	private String serviceURL;
	private String repositoryURL;
	private String featureName;

	public void setApplicationManager(ApplicationManager applicationManager) {
		Client.applicationManager = applicationManager;
	}
	
	public HashMap<String, String[]> getEnvironment() {
		return environment;
	}

	public void setEnvironment(HashMap<String, String[]> environment) {
		this.environment = environment;
	}

	public String getServiceURL() {
		return serviceURL;
	}
	
	public void setServiceURL(String serviceURL) {
		this.serviceURL = serviceURL;
	}
	
	public String getRepositoryURL() {
		return repositoryURL;
	}

	public void setRepositoryURL(String repositoryURL) {
		this.repositoryURL = repositoryURL;
	}

	public String getFeatureName() {
		return featureName;
	}

	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}

	public static void main(String args[]) {

		try {

			ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
					new String[] { "META-INF/spring/client-beans.xml" });

			Client client = (Client) context.getBean("Client");

			JMXConnector jmxc = applicationManager.createRMIconnector(
					client.getServiceURL(), client.getEnvironment());

			MBeanServerConnection mbsc = applicationManager
					.getMBeanServerConnection(jmxc);

			FeaturesServiceMBean featuresServiceMBeanProxy = applicationManager
					.createFeaturesServiceMBeanProxy(mbsc);

			FrameworkMBean osgiFrameworkProxy = applicationManager
					.createOsgiFrameworkMBeanProxy(mbsc);

			applicationManager.addRepository(featuresServiceMBeanProxy,
					client.getRepositoryURL());

			applicationManager.installFeature(featuresServiceMBeanProxy,
					client.getFeatureName());

			applicationManager.startBundle(osgiFrameworkProxy, 131);

			applicationManager.stopBundle(osgiFrameworkProxy, 131);

			applicationManager.uninstallFeature(featuresServiceMBeanProxy,
					client.getFeatureName());

			applicationManager.removeRepository(featuresServiceMBeanProxy,
					client.getRepositoryURL());

			applicationManager.closeConnection(jmxc);
			
			Thread.sleep(5000);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}