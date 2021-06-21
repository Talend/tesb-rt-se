/*
 * #%L
 * Abstract base class for Service Locator Selection Strategy
 * %%
 * Copyright (c) 2006-2021 Talend Inc. - www.talend.com
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

import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.cxf.clustering.FailoverStrategy;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.message.Exchange;

import org.talend.esb.servicelocator.client.SLPropertiesMatcher;
import org.talend.esb.servicelocator.client.ServiceLocator;

public abstract class LocatorSelectionStrategy implements FailoverStrategy {

	protected static final Logger LOG = Logger
			.getLogger(LocatorSelectionStrategy.class.getName());

	protected LocatorCache locatorCache = new LocatorCache();

	private Random random = new Random();

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.apache.cxf.clustering.FailoverStrategy#selectAlternateAddress(java
	 * .util.List)
	 */
	@Override
	public String selectAlternateAddress(List<String> alternates) {
		String alternateAddress = null;
		if (alternates != null && !alternates.isEmpty()) {
			int index = random.nextInt(alternates.size());
			alternateAddress = alternates.remove(index);
		}
		LOG.log(Level.INFO, "selectAlternateAddress " + " alternates = "
				+ alternates + " alternateAddress = " + alternateAddress);

		return alternateAddress;
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
		return locatorCache.getFailoverEndpoints(getServiceName(exchange));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.apache.cxf.clustering.FailoverStrategy#getAlternateEndpoints(org.
	 * apache.cxf.message.Exchange)
	 */
	@Override
	public List<Endpoint> getAlternateEndpoints(Exchange exchange) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.apache.cxf.clustering.FailoverStrategy#selectAlternateEndpoint(java
	 * .util.List)
	 */
	@Override
	public Endpoint selectAlternateEndpoint(List<Endpoint> alternates) {
		return null;
	}

	/**
	 * @param exchange
	 * @return
	 */
	public abstract String getPrimaryAddress(Exchange exchange);

	synchronized public void setMatcher(SLPropertiesMatcher propertiesMatcher) {
		if (propertiesMatcher != null) {
			locatorCache.setMatcher(propertiesMatcher);
		}
	}

	public void setServiceLocator(ServiceLocator serviceLocator) {
		locatorCache.setServiceLocator(serviceLocator);
	}

	public void setReloadAddressesCount(int reloadAddressesCount) {
		locatorCache.setReloadCount(reloadAddressesCount);
	}

	protected QName getServiceName(Exchange exchange) {
		return exchange.getEndpoint().getService().getName();
	}

}
