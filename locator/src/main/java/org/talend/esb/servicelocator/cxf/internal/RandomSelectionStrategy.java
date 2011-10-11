/*
 * #%L
 * Even Distribution Service Locator Selection Strategy
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
package org.talend.esb.servicelocator.cxf.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.xml.namespace.QName;

import org.apache.cxf.clustering.FailoverStrategy;
import org.apache.cxf.message.Exchange;

public class RandomSelectionStrategy extends LocatorSelectionStrategy implements FailoverStrategy {

    private int reloadAdressesCount = 10;

    private Map<QName, List<String>> availableAddressesMap = new HashMap<QName, List<String>>();

    private int reloadCounter;

    public void setReloadAdressesCount(int reloadAdressesCount) {
        this.reloadAdressesCount = reloadAdressesCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getAlternateAddresses(Exchange exchange) {
        QName serviceName = getServiceName(exchange);
        // force reload
        List<String> alternateAddresses = getRotatedAdresses(serviceName, true);
        return alternateAddresses;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized String getPrimaryAddress(Exchange exchange) {
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

    private synchronized List<String> getRotatedAdresses(QName serviceName, boolean forceReload) {
        List<String> availableAddresses = availableAddressesMap.get(serviceName);
        if (forceReload || isReloadAdresses() || availableAddresses == null || availableAddresses.isEmpty()) {
            availableAddresses = getEndpoints(serviceName);
        }
        if (!availableAddresses.isEmpty()) {
            availableAddresses = getRotatedList(availableAddresses);
            if (LOG.isLoggable(Level.FINE)) {
                LOG.log(Level.FINE, "List of endpoints for service " + serviceName + ": "
                        + availableAddresses);
            }
        } else {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.log(Level.FINE, "Received empty list of endpoints from locator for service "
                        + serviceName);
            }
        }
        availableAddressesMap.put(serviceName, availableAddresses);
        return availableAddresses;
    }

    private List<String> getRotatedList(List<String> strings) {
        int index = random.nextInt(strings.size());
        List<String> rotated = new ArrayList<String>();
        for (int i = 0; i < strings.size(); i++) {
            rotated.add(strings.get(index));
            index = (index + 1) % strings.size();
        }
        return rotated;
    }

    private synchronized boolean isReloadAdresses() {
        boolean isReloadAdresses = reloadCounter == 0;
        reloadCounter = (reloadCounter + 1) % reloadAdressesCount;
        return isReloadAdresses;
    }
}
