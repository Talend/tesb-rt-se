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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.cxf.clustering.FailoverStrategy;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.message.Exchange;
import org.talend.esb.servicelocator.client.ServiceLocator;
import org.talend.esb.servicelocator.client.internal.ServiceLocatorException;

public class LocatorSelectionStrategy implements FailoverStrategy {

	private static final Logger LOG = Logger.getLogger(LocatorSelectionStrategy.class
			.getName());
	
	private ServiceLocator serviceLocator;
	
	private Random random = new Random();

	private Map<QName, String> primaryAddresses = new HashMap<QName, String>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getAlternateAddresses(Exchange exchange) {
		QName serviceName = getServiceName(exchange);
		List<String> alternateAddresses= getEndpoints(serviceName);
		synchronized (this) {
			primaryAddresses.remove(serviceName);
		}
		return alternateAddresses;
	}

	@Override
	public String selectAlternateAddress(List<String> alternates) {
		String alternateAddress = null;
		if (alternates != null && ! alternates.isEmpty()) {
			int index = random.nextInt(alternates.size());
			alternateAddress = alternates.remove(index);
		}
		return alternateAddress;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Endpoint> getAlternateEndpoints(Exchange exchange) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Endpoint selectAlternateEndpoint(List<Endpoint> alternates) {
		return null;
	}
	
	/**
	 * 
	 * @param exchange
	 * @return
	 */
	synchronized public String getPrimaryAddress(Exchange exchange) {
		QName serviceName = getServiceName(exchange);
		String primaryAddress = primaryAddresses.get(serviceName);

		if (primaryAddress == null) {
			List<String> availableAddresses = getEndpoints(serviceName);
			if (! availableAddresses.isEmpty()) {
				int index = random.nextInt(availableAddresses.size());
				primaryAddress = availableAddresses.get(index);
				primaryAddresses.put(serviceName, primaryAddress);
			}
		}
		return primaryAddress;
	}

	public void setServiceLocator(ServiceLocator serviceLocator) {
		this.serviceLocator = serviceLocator;
	}

	public ServiceLocator getServiceLocator() {
		return serviceLocator;
	}

	private List<String> getEndpoints(QName serviceName) {
		List<String> endpoints = Collections.emptyList();
		try {
			endpoints = serviceLocator.lookup(serviceName);
		} catch (ServiceLocatorException e) {
			if (LOG.isLoggable(Level.SEVERE)) {
				LOG.log(Level.SEVERE,
						"Can not refresh list of endpoints due to ServiceLocatorException", e);
			}
		} catch (InterruptedException e) {
			if (LOG.isLoggable(Level.SEVERE)) {
				LOG.log(Level.SEVERE,
						"Can not refresh list of endpoints due to InterruptedException", e);
			}
		}
		return endpoints;
	}
	
	private QName getServiceName(Exchange exchange) {
		return exchange.getEndpoint().getService().getName();
	}
}
