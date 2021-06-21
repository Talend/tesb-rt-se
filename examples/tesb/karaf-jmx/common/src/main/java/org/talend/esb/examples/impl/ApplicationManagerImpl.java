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
package org.talend.esb.examples.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Set;

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

        echo("\nCreate an RMI connector client and "
                + "connect it to the RMI connector server");
        JMXServiceURL url = new JMXServiceURL(serviceUrl);
        JMXConnector jmxc = JMXConnectorFactory.connect(url, environment);
        return jmxc;
    }

    public void closeConnection(JMXConnector jmxc) throws IOException {
        echo("\n>>> Close connection <<<");
        jmxc.close();
    }

    public MBeanServerConnection getMBeanServerConnection(JMXConnector jmxc)
            throws IOException {
        echo("\nGet an MBeanServerConnection");
        MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
        return mbsc;
    }

    public FeaturesServiceMBean createFeaturesServiceMBeanProxy(
            MBeanServerConnection mbsc) throws MalformedObjectNameException,
            NullPointerException, InstanceNotFoundException, IOException {

        echo("\n>>> Create FeatureService MBean <<<");
        ObjectName mbeanName = new ObjectName(
                "org.apache.karaf:type=feature,name=trun");
        FeaturesServiceMBean featuresServiceMBeanProxy = JMX.newMBeanProxy(
                mbsc, mbeanName, FeaturesServiceMBean.class, true);

        echo("\n>>> Add notification listener to FeatureService MBean <<<");
        mbsc.addNotificationListener(mbeanName, clientListener, null, null);

        return featuresServiceMBeanProxy;
    }

    public FrameworkMBean createOsgiFrameworkMBeanProxy(
            MBeanServerConnection mbsc) throws MalformedObjectNameException,
            NullPointerException, IOException {
        echo("\n>>> Create Framework MBean <<<");
        ObjectName mbeanName = new ObjectName(
                "osgi.core:type=framework,version=1.7,framework=org.eclipse.osgi,*");
        Set<ObjectName> allObjectNames = mbsc.queryNames(mbeanName, null);
        for (ObjectName obj : allObjectNames) {
            mbeanName = new ObjectName(obj.getCanonicalName());
            break;
        }
        FrameworkMBean osgiFrameworkProxy = JMX.newMBeanProxy(mbsc, mbeanName,
                FrameworkMBean.class, false);
        return osgiFrameworkProxy;
    }

    public void addRepository(FeaturesServiceMBean featuresServiceMBeanProxy,
            String url) throws Exception {
        echo("\n>>> Perform addRepository on FeaturesService MBean <<<");
        featuresServiceMBeanProxy.addRepository(url);
    }

    public void removeRepository(
            FeaturesServiceMBean featuresServiceMBeanProxy, String url)
            throws Exception {
        echo("\n>>> Perform removeRepository on FeaturesService MBean <<<");
        featuresServiceMBeanProxy.removeRepository(url);
    }

    public void installFeature(FeaturesServiceMBean featuresServiceMBeanProxy,
            String featureName) throws Exception {
        echo("\n>>> Perform installFeature on FeaturesService MBean <<<");
        featuresServiceMBeanProxy.installFeature(featureName);
    }

    public void uninstallFeature(
            FeaturesServiceMBean featuresServiceMBeanProxy, String featureName)
            throws Exception {
        echo("\n>>> Perform uninstallFeature on FeaturesService MBean <<<");
        featuresServiceMBeanProxy.uninstallFeature(featureName);
    }

    public long startBundle(FrameworkMBean osgiFrameworkProxy, String bundleName)
            throws Exception {
        echo("\n>>> Perform startBundle on Framework MBean <<<");
        long bundleNumber = osgiFrameworkProxy.installBundle(bundleName);
        osgiFrameworkProxy.startBundle(bundleNumber);
        return bundleNumber;
    }

    public void stopBundle(FrameworkMBean osgiFrameworkProxy, long bundleNumber)
            throws Exception {
        echo("\n>>> Perform stopBundle on Framework MBean <<<");
        osgiFrameworkProxy.stopBundle(bundleNumber);
    }

    public ClientListener getClientListener() {
        return clientListener;
    }

    public void setClientListener(ClientListener clientListener) {
        this.clientListener = clientListener;
    }

    private static void echo(String msg) {
        System.out.println(msg);
    }

}
