package org.talend.esb.locator;

import java.util.List;

import org.apache.cxf.clustering.RandomStrategy;

public class LocatorFailoverStrategy extends RandomStrategy {

	private EndpointRetriever endpointSelector;

	public LocatorFailoverStrategy(EndpointRetriever endpointSelector) {
		super();
		this.endpointSelector = endpointSelector;
		if (endpointSelector != null) {
			setAlternateAddresses(endpointSelector.getEndpointsList());
		}
	}

	public LocatorFailoverStrategy() {
		super();
	}

	@Override
	public String selectAlternateAddress(List<String> alternates) {
		if (alternates.isEmpty()) {
			if (endpointSelector != null) {
				endpointSelector.refreshEndpointsList();
				if (!endpointSelector.isEmptyList()) {
					List<String> newAlternates = endpointSelector
							.getEndpointsList();
					setAlternateAddresses(newAlternates);
					return super.selectAlternateAddress(newAlternates);

				}
			}
		}
		return super.selectAlternateAddress(alternates);
	}

	public void setEndpointSelector(EndpointRetriever endpointSelector) {
		this.endpointSelector = endpointSelector;
	}

}
