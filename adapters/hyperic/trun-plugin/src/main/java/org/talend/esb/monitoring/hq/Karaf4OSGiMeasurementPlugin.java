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

import javax.management.MBeanServerConnection;
import javax.management.openmbean.TabularData;

import org.hyperic.hq.product.MetricNotFoundException;
import org.hyperic.hq.product.MetricUnreachableException;

public class Karaf4OSGiMeasurementPlugin extends KarafOSGiMeasurementPlugin {

    @Override
    protected TabularData getBundlesData(MBeanServerConnection connection)
            throws MetricUnreachableException, MetricNotFoundException {
        return super.retreiveTabularAttributeData(connection, getObjnameBundles(), "Bundles");
    }

    @Override
    protected TabularData getServicesData(MBeanServerConnection connection)
            throws MetricUnreachableException, MetricNotFoundException {
        return super.retreiveTabularAttributeData(connection, getObjnameServices(), "Services");
    }
}
