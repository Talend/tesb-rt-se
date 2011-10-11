/*
 * #%L
 * Service Locator Client for CXF
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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.cxf.clustering.FailoverTargetSelector;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.service.model.EndpointInfo;

public class LocatorTargetSelector extends FailoverTargetSelector {

    private static final Logger LOG = Logger.getLogger(LocatorTargetSelector.class.getPackage().getName());

    private static final String LOCATOR_PROTOCOL = "locator://";

    private boolean locatorProtocol;

    private LocatorSelectionStrategy strategy = new DefaultSelectionStrategy();

    public LocatorTargetSelector(LocatorSelectionStrategy strategy) {
        setLocatorSelectionStrategy(strategy);
    }

    public LocatorTargetSelector() {
    }

    @Override
    public synchronized void prepare(Message message) {
        Exchange exchange = message.getExchange();
        EndpointInfo ei = endpoint.getEndpointInfo();
        if (locatorProtocol || ei.getAddress().startsWith(LOCATOR_PROTOCOL)) {
            if (LOG.isLoggable(Level.INFO)) {
                LOG.log(Level.INFO, "Found address with locator protocol, mapping it to physical address.");
                LOG.log(Level.INFO, "Using strategy " + strategy.getClass().getName() + ".");
            }

            String physAddress = strategy.getPrimaryAddress(exchange);

            if (physAddress != null) {
                ei.setAddress(physAddress);
                locatorProtocol = true;
                message.put(Message.ENDPOINT_ADDRESS, physAddress);
            } else {
                if (LOG.isLoggable(Level.SEVERE)) {
                    LOG.log(Level.SEVERE, "Failed to map logical locator address to physical address.");
                }
                throw new IllegalStateException("No endpoint found in Service Locator for service "
                        + endpoint.getService().getName());
            }
        }
        super.prepare(message);
    }

    public void setLocatorSelectionStrategy(LocatorSelectionStrategy strategy) {
        this.strategy = strategy;
        setStrategy(strategy);
    }

}
