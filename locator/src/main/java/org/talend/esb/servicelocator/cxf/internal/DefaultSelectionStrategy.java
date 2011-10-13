/*
 * #%L
 * Default Service Locator Selection Strategy
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.xml.namespace.QName;

import org.apache.cxf.clustering.FailoverStrategy;
import org.apache.cxf.message.Exchange;

public class DefaultSelectionStrategy extends LocatorSelectionStrategy implements FailoverStrategy {

    private Map<QName, String> primaryAddresses = new HashMap<QName, String>();

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getAlternateAddresses(Exchange exchange) {
        QName serviceName = getServiceName(exchange);
        List<String> alternateAddresses = getEndpoints(serviceName);
        synchronized (this) {
            primaryAddresses.remove(serviceName);
        }
        return alternateAddresses;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized String getPrimaryAddress(Exchange exchange) {
        QName serviceName = getServiceName(exchange);
        String primaryAddress = primaryAddresses.get(serviceName);

        if (primaryAddress == null) {
            List<String> availableAddresses = getEndpoints(serviceName);
            if (!availableAddresses.isEmpty()) {
                int index = random.nextInt(availableAddresses.size());
                primaryAddress = availableAddresses.get(index);
                primaryAddresses.put(serviceName, primaryAddress);
            }
        }
        if (LOG.isLoggable(Level.INFO)) {
            LOG.log(Level.INFO, "Get address for service " + serviceName + " using strategy "
                    + this.getClass().getName() + " selecting from " + primaryAddresses.entrySet()
                    + " selected = " + primaryAddress);
        }
        return primaryAddress;
    }

}
