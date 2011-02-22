package org.talend.esb.locator;

import org.apache.cxf.clustering.FailoverTargetSelector;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.service.model.EndpointInfo;

public class LocatorTargetSelector extends FailoverTargetSelector {
	
	private LocatorFailoverStrategy strategy = new LocatorFailoverStrategy();

	public LocatorTargetSelector(LocatorFailoverStrategy strategy) {
		setLocatorFailoverStrategy(strategy);
	}
	
	public LocatorTargetSelector() {
	}

	@Override
	public synchronized void prepare(Message message) {
		Exchange exchange = message.getExchange();
        EndpointInfo ei = endpoint.getEndpointInfo();
        if (ei.getAddress().startsWith("locator://")) {
        	String physAddress = strategy.getPrimaryAddress(exchange);

        	ei.setAddress(physAddress);

        	message.put(Message.ENDPOINT_ADDRESS, physAddress);
        }
		super.prepare(message);
	}
	
	public void setLocatorFailoverStrategy(LocatorFailoverStrategy strategy) {
		this.strategy = strategy;
		setStrategy(strategy);
	}

}
