/*******************************************************************************
*
* Copyright (c) 2011 Talend Inc. - www.talend.com
* All rights reserved.
*
* This program and the accompanying materials are made available
* under the terms of the Apache License v2.0
* which accompanies this distribution, and is available at
* http://www.apache.org/licenses/LICENSE-2.0
*
*******************************************************************************/
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
        	}
        }
		super.prepare(message);
	}
	
	public void setLocatorFailoverStrategy(LocatorSelectionStrategy strategy) {
		this.strategy = strategy;
		setStrategy(strategy);
	}

}
