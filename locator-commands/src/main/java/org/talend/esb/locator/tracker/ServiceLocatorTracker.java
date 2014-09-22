/*
 * #%L
 * Service Locator Monitor
 * %%
 * Copyright (C) 2011 - 2014 Talend Inc.
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

package org.talend.esb.locator.tracker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.esb.servicelocator.client.ServiceLocator;
import org.talend.esb.servicelocator.client.ServiceLocatorException;

public class ServiceLocatorTracker {

    static final int INTERVAL = 30; // Check for Service updates every 30 seconds

    private static final Logger LOG = LoggerFactory.getLogger(ServiceLocatorTracker.class);

    private static ServiceLocatorTracker serviceLocatorTracker;

    /**
     * Mapping Service Name to List of Service Endpoints.
     */
    Map<String, List<String>> endpointMap = new HashMap<String, List<String>>();

    /**
     * Mapping local part of service name to full QName
     */
    Map<String, QName> serviceMap = new HashMap<String, QName>();

    ServiceLocator sl;

    private ServiceLocatorTracker(ServiceLocator serviceLocator) {
        this.sl = serviceLocator;
        startMonitoringServiceList();
    }

    public synchronized static ServiceLocatorTracker getInstance(ServiceLocator serviceLocator) {
        if (serviceLocatorTracker == null) {
            serviceLocatorTracker = new ServiceLocatorTracker(serviceLocator);
        }
        return serviceLocatorTracker;
    }

    private void startMonitoringServiceList() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleWithFixedDelay(new Runnable() {

            @Override
            public void run() {
                updateServiceListe();
            }
        }, 2, INTERVAL, TimeUnit.SECONDS);
    }

    public void updateServiceListe() {
        try {

            List<QName> services = sl.getServices();
            synchronized (this) {
                serviceMap.clear();
                endpointMap.clear();
                for (QName service : services) {
                    serviceMap.put(service.getLocalPart(), service);
                    endpointMap.put(service.toString(), sl.getEndpointNames(service));
                }
            }
        } catch (InterruptedException e) {
        } catch (ServiceLocatorException e) {
            LOG.warn(e.getMessage());
        }
    }

    public List<String> getEndpoints(String serviceName) {
        List<String> result = new ArrayList<String>();
        if (serviceName == null || serviceName.length() == 0) {
            return result;
        }
        serviceName = serviceName.replaceAll("\"", "");
        synchronized (this) {
            QName service = serviceMap.get(serviceName);
            List<String> endpoints = service != null
                ? endpointMap.get(service.toString())
                : endpointMap.get(serviceName);
            if (endpoints != null) {
                result.addAll(endpoints);
            }
        }
        return result;
    }

    public Set<String> getServiceNames(boolean includeNamespace) {
        synchronized (this) {
            if (includeNamespace) {
                return endpointMap.keySet();
            } else {
                return serviceMap.keySet();
            }
        }
    }

    public Collection<QName> getServiceQNames() {
        synchronized (this) {
            return serviceMap.values();
        }
    }

    public QName getServiceName(String serviceName) {
        if (serviceName == null) {
            return null;
        } else {
            synchronized (this) {
                QName result = serviceMap.get(serviceName);
                if (result != null) {
                    return result;
                } else {
                    return QName.valueOf(serviceName);
                }
            }
        }
    }
}
