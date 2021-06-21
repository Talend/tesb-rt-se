/*
 * #%L
 * TESB :: Examples :: Karaf-jmx
 * %%
 * Copyright (c) 2006-2021 Talend Inc. - www.talend.com
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.talend.esb.examples;

import java.io.IOException;
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
    private String bundleName;

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

    public String getBundleName() {
        return bundleName;
    }

    public void setBundleName(String bundleName) {
        this.bundleName = bundleName;
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

            long bundleNumber = applicationManager
                    .startBundle(osgiFrameworkProxy, client.getBundleName());

            applicationManager.stopBundle(osgiFrameworkProxy, bundleNumber);
            waitForEnterPressed();

            applicationManager.uninstallFeature(featuresServiceMBeanProxy,
                    client.getFeatureName());

            applicationManager.removeRepository(featuresServiceMBeanProxy,
                    client.getRepositoryURL());

            sleep(5000);

            applicationManager.closeConnection(jmxc);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void echo(String msg) {
        System.out.println(msg);
    }

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void waitForEnterPressed() {
        try {
            echo("\nPress <Enter> to continue...");
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
