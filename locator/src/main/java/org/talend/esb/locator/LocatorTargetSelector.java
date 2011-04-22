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
package org.talend.esb.locator;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.cxf.clustering.FailoverTargetSelector;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.service.model.EndpointInfo;

public class LocatorTargetSelector extends FailoverTargetSelector {
	
	private static final Logger LOG = Logger.getLogger(LocatorTargetSelector.class
			.getPackage().getName());

	private static final String LOCATOR_PROTOCOL = "locator://";
	
	private LocatorSelectionStrategy strategy = new LocatorSelectionStrategy();

	public LocatorTargetSelector(LocatorSelectionStrategy strategy) {
		setLocatorFailoverStrategy(strategy);
	}
	
	public LocatorTargetSelector() {
	}

	@Override
	public synchronized void prepare(Message message) {
		Exchange exchange = message.getExchange();
        EndpointInfo ei = endpoint.getEndpointInfo();
        if (ei.getAddress().startsWith(LOCATOR_PROTOCOL)) {
        	if (LOG.isLoggable(Level.INFO)) {
    			LOG.log(Level.INFO, "Found address with locator protocol, mapping it to physical address.");
    		}

        	String physAddress = strategy.getPrimaryAddress(exchange);

        	if (physAddress != null) {
        		ei.setAddress(physAddress);
        		message.put(Message.ENDPOINT_ADDRESS, physAddress);
        	} else {
            	if (LOG.isLoggable(Level.SEVERE)) {
            		LOG.log(Level.SEVERE, "Failed to map logical locator address to physical address.");
        		}
            	throw new IllegalStateException("No endpoint found in Service Locator for service " + endpoint.getService().getName());
        	}
        }
		super.prepare(message);
	}
	
	public void setLocatorFailoverStrategy(LocatorSelectionStrategy strategy) {
		this.strategy = strategy;
		setStrategy(strategy);
	}

}
