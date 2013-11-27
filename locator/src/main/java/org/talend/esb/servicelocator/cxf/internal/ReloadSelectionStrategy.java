/*
 * #%L
 * Abstract base class for Service Locator Selection Strategy
 * %%
 * Copyright (C) 2011 - 2012 Talend Inc.
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
package org.talend.esb.servicelocator.cxf.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.xml.namespace.QName;

import org.apache.cxf.message.Exchange;

abstract class ReloadSelectionStrategy extends LocatorSelectionStrategy {

    private int reloadAdressesCount = 10;

    static class ServiceEndpoints {

        private List<String> endpoints;

        private int reloadCounter;

        public List<String> getEndpoints() {
            return endpoints;
        }

        public void setEndpoints(List<String> endpoints) {
            this.endpoints = endpoints;
        }

        public int getReloadCounter() {
            return reloadCounter;
        }

        public void setReloadCounter(int reloadCounter) {
            this.reloadCounter = reloadCounter;
        }
    }

    private static Map<QName, ServiceEndpoints> availableAddressesMap = new HashMap<QName, ServiceEndpoints>();

    public void setReloadAdressesCount(int reloadAdressesCount) {
        this.reloadAdressesCount = reloadAdressesCount;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.cxf.clustering.FailoverStrategy#getAlternateAddresses(org.
     * apache.cxf.message.Exchange)
     */
    @Override
    public List<String> getAlternateAddresses(Exchange exchange) {
        // force reload
        return getRotatedAdresses(getServiceName(exchange), true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.esb.servicelocator.cxf.internal.LocatorSelectionStrategy#
     * getPrimaryAddress(org.apache.cxf.message.Exchange)
     */
    @Override
    public String getPrimaryAddress(Exchange exchange) {
        QName serviceName = getServiceName(exchange);
        String primaryAddress = null;
        List<String> availableAddresses = getRotatedAdresses(serviceName, false);
        if (!availableAddresses.isEmpty()) {
            primaryAddress = availableAddresses.get(0);
        }
        if (LOG.isLoggable(Level.INFO)) {
            LOG.log(Level.INFO, "Get address for service " + serviceName + " using strategy "
                    + this.getClass().getName() + " selecting from " + availableAddresses + " selected = "
                    + primaryAddress);
        }
        return primaryAddress;
    }

    protected abstract List<String> getRotatedList(List<String> strings);

    /**
     * Retrieves a list of endpoint adresses. The list is reloaded if either
     * forceReload is true or the reloadCounter reaches its limit
     * reloadAdressesCount. The cached (or reloaded) list of endpoint adresses
     * is rotated.
     * 
     * @param serviceName
     * @param forceReload
     * @return
     */
    private synchronized List<String> getRotatedAdresses(QName serviceName, boolean forceReload) {
        List<String> availableAddresses = null;
        synchronized (availableAddressesMap) {
            ServiceEndpoints serviceEndpoints = availableAddressesMap.get(serviceName);
            if (serviceEndpoints == null) {
                serviceEndpoints = new ServiceEndpoints();
            }
            availableAddresses = serviceEndpoints.getEndpoints();
            if (forceReload || isReloadAdresses(serviceEndpoints) || availableAddresses == null
                    || availableAddresses.isEmpty()) {
                availableAddresses = getEndpoints(serviceName);
            }
            if (availableAddresses != null && !availableAddresses.isEmpty()) {
                if (availableAddresses.size() > 1) {
                    availableAddresses = getRotatedList(availableAddresses);
                }
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.log(Level.FINE, "Strategy = " + this.hashCode() + " List of endpoints for service "
                            + serviceName + ": " + availableAddresses);
                }
            } else {
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.log(Level.FINE, "Strategy = " + this.hashCode()
                            + "Received empty list of endpoints from locator for service " + serviceName);
                }
            }
            serviceEndpoints.setEndpoints(availableAddresses);
            availableAddressesMap.put(serviceName, serviceEndpoints);
            // need clone because selectAlternateAddress modifies the list
        }
        return new ArrayList<String>(availableAddresses);
    }

    private synchronized boolean isReloadAdresses(ServiceEndpoints serviceEndpoints) {
        boolean isReloadAdresses = serviceEndpoints.getReloadCounter() == 0;
        serviceEndpoints.setReloadCounter((serviceEndpoints.getReloadCounter() + 1) % reloadAdressesCount);
        return isReloadAdresses;
    }

}
