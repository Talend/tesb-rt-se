/*
 * #%L
 * Talend ESB :: Adapters :: HQ :: Talend Runtime Plugin
 * %%
 * Copyright (C) 2011 - 2013 Talend Inc.
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
package org.talend.esb.monitoring.hq;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;
import javax.management.remote.JMXConnector;

import org.hyperic.hq.product.Metric;
import org.hyperic.hq.product.MetricNotFoundException;
import org.hyperic.hq.product.MetricUnreachableException;
import org.hyperic.hq.product.MetricValue;
import org.hyperic.hq.product.PluginException;
import org.hyperic.hq.product.PluginManager;
import org.hyperic.hq.product.jmx.MxMeasurementPlugin;
import org.hyperic.hq.product.jmx.MxUtil;

import static org.talend.esb.monitoring.hq.HypericUtils.getMandatoryProperty;

public class KarafOSGiMeasurementPlugin extends MxMeasurementPlugin {

    public static final String EXPIRATION_TIMEOUT = "expiration.timeout";

    public static final String PROP_BUNDLES_OBJNAME = "KARAF_BUNDLES_OBJECT_NAME";
    public static final String PROP_FEATURES_OBJNAME = "KARAF_FEATURES_OBJECT_NAME";
    public static final String PROP_SERVICES_OBJNAME = "KARAF_SERVICES_OBJECT_NAME";

    // private final Logger log = Logger
    // .getLogger(KarafOSGiMeasurementPlugin.class.getName());

    private final ConcurrentHashMap<String, MetricValue> dataCache = new ConcurrentHashMap<String, MetricValue>(20);

    private long lastCollectionTime = 0;

    /**
     * The cached data is considered expired if it was collected more than
     * expirationPeriod msecs ago.
     */
    private long expirationTimeout = 60000;

    private ObjectName objnameBundles;
    private ObjectName objnameFeatures;
    private ObjectName objnameServices;

    @Override
    public void init(PluginManager manager) throws PluginException {
        super.init(manager);

        try {
            expirationTimeout = Long.valueOf(getMandatoryProperty(this, EXPIRATION_TIMEOUT));
        } catch (NumberFormatException e) {
            throw new PluginException(e);
        }

        try {
            objnameBundles = ObjectName.getInstance(getMandatoryProperty(this, PROP_BUNDLES_OBJNAME));
        } catch (PluginException e) {
            throw e;
        } catch (Exception e) {
            throw new PluginException(e);
        }

        try {
            objnameFeatures = ObjectName.getInstance(getMandatoryProperty(this, PROP_FEATURES_OBJNAME));
        } catch (PluginException e) {
            throw e;
        } catch (Exception e) {
            throw new PluginException(e);
        }

        try {
            objnameServices = ObjectName.getInstance(getMandatoryProperty(this, PROP_SERVICES_OBJNAME));
        } catch (PluginException e) {
            throw e;
        } catch (Exception e) {
            throw new PluginException(e);
        }
    }

    private void updateBundlesData(MBeanServerConnection connection)
            throws MetricUnreachableException, MetricNotFoundException {
        TabularData td = getBundlesData(connection);

        long bundlesTotal = td.size();

        long bundlesActive = 0;
        long bundlesResolved = 0;
        long bundlesInstalled = 0;

        for (Object k : td.values()) {
            if (!(k instanceof CompositeData)) {
                throw new IllegalArgumentException();
            }

            CompositeData cdata = (CompositeData) k;

            if (!cdata.containsKey("State")) {
                throw new IllegalArgumentException();
            }

            String state = (String) cdata.get("State");
            if (state.startsWith("A") || state.startsWith("a")) {
                bundlesActive++;
            } else if (state.startsWith("R") || state.startsWith("r")) {
                bundlesResolved++;
            } else if (state.startsWith("I") || state.startsWith("i")) {
                bundlesInstalled++;
            }
        }

        dataCache.put("#Bundles-Total", new MetricValue(bundlesTotal));
        dataCache.put("#Bundles-Active", new MetricValue(bundlesActive));
        dataCache.put("#Bundles-Resolved", new MetricValue(bundlesResolved));
        dataCache.put("#Bundles-Installed", new MetricValue(bundlesInstalled));
    }

    private void updateFeaturesData(MBeanServerConnection connection)
            throws MetricUnreachableException, MetricNotFoundException {
        TabularData tdFeatures = getFeaturesData(connection);

        long featuresTotal = tdFeatures.size();

        TabularData tdRepos = getRepositoriesData(connection);

        long repositoriesTotal = tdRepos.size();

        dataCache.put("#Features", new MetricValue(featuresTotal));
        dataCache.put("#FeatureRepositories", new MetricValue(repositoriesTotal));
    }

    private void updateServicesData(MBeanServerConnection connection)
            throws MetricUnreachableException, MetricNotFoundException {
        TabularData td = getServicesData(connection);

        long servicesTotal = td.size();

        dataCache.put("#Services", new MetricValue(servicesTotal));
    }

    private synchronized void updateCache(Properties connectionProps)
            throws MetricUnreachableException, PluginException {
        if ((System.currentTimeMillis() - lastCollectionTime) < expirationTimeout) {
            return;
        }

        JMXConnector jmxConnector = null;
        try {
            jmxConnector = MxUtil.getCachedMBeanConnector(connectionProps);
            MBeanServerConnection conn = jmxConnector.getMBeanServerConnection();

            updateBundlesData(conn);
            updateFeaturesData(conn);
            updateServicesData(conn);
        } catch (IOException e) {
            throw new MetricUnreachableException("Error during communication with remote MBean Server.", e);
        } catch (Exception e) {
            throw new PluginException("", e);
        } finally {
            // it's null-proof
            MxUtil.close(jmxConnector);
        }

        lastCollectionTime = System.currentTimeMillis();
    }

    @Override
    public MetricValue getValue(Metric metric)
            throws PluginException, MetricNotFoundException, MetricUnreachableException {
        if (metric.isAvail()) {
            return super.getValue(metric);
        }

        updateCache(metric.getProperties());

        if (!dataCache.containsKey(metric.getAttributeName())) {
            throw new PluginException("Unknown metric " + metric.toString());
        }

        return dataCache.get(metric.getAttributeName());
    }

    protected TabularData getBundlesData(MBeanServerConnection connection)
            throws MetricUnreachableException, MetricNotFoundException {
        return retreiveTabularMethodData(connection, objnameBundles, "list");
    }

    protected TabularData getServicesData(MBeanServerConnection connection)
            throws MetricUnreachableException, MetricNotFoundException {
        return retreiveTabularMethodData(connection, objnameServices, "list");
    }

    protected TabularData getFeaturesData(MBeanServerConnection connection)
            throws MetricUnreachableException, MetricNotFoundException {
        return retreiveTabularAttributeData(connection, objnameFeatures, "Features");
    }

    protected TabularData getRepositoriesData(MBeanServerConnection connection)
            throws MetricUnreachableException, MetricNotFoundException {
        return retreiveTabularAttributeData(connection, objnameFeatures, "Repositories");
    }

    protected final TabularData retreiveTabularAttributeData(MBeanServerConnection connection, ObjectName objName,
            String attributeName) throws MetricUnreachableException, MetricNotFoundException {
        try {
            return TabularData.class.cast(connection.getAttribute(objName, attributeName));
        } catch (IOException e) {
            throw new MetricUnreachableException("Unable to get attribute value for " + objName + ":" + attributeName,
                    e);
        } catch (Exception e) {
            throw new MetricNotFoundException("Unable to get attribute value for " + objName + ":" + attributeName, e);
        }
    }

    protected final TabularData retreiveTabularMethodData(MBeanServerConnection connection, ObjectName objName,
            String operationName) throws MetricUnreachableException, MetricNotFoundException {
        try {
            return TabularData.class.cast(connection.invoke(objName, operationName, null, null));
        } catch (IOException e) {
            throw new MetricUnreachableException(
                    "Unable to get operation return value for " + objName + "->" + operationName, e);
        } catch (Exception e) {
            throw new MetricNotFoundException(
                    "Unable to get operation return value for " + objName + "->" + operationName, e);
        }
    }

    protected final ObjectName getObjnameBundles() {
        return objnameBundles;
    }

    protected final ObjectName getObjnameServices() {
        return objnameServices;
    }

    protected final ObjectName getObjnameFeatures() {
        return objnameFeatures;
    }

}
