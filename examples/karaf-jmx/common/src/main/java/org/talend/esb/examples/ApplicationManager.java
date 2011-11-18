/*
 * #%L
 * TESB :: Examples :: Karaf-jmx
 * %%
 * Copyright (C) 2011 Talend Inc.
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
