/*
 * #%L
 * Service Locator :: Proxy
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
package org.talend.esb.locator.proxy.service;

import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import javax.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;

import org.talend.esb.servicelocator.client.ServiceLocator;
import org.talend.esb.servicelocator.client.ServiceLocatorException;
import org.talend.esb.locator.proxy.service.types.EndpointReferenceListType;

public class LocatorProxyServiceImpl implements LocatorProxyService {

	private static final Logger LOG = Logger
			.getLogger(LocatorProxyServiceImpl.class.getPackage().getName());

	private ServiceLocator locatorClient;

	private Random random = new Random();

	public void setServiceLocator(ServiceLocator locatorClient) {
		this.locatorClient = locatorClient;
		if (LOG.isLoggable(Level.FINE)) {
			LOG.log(Level.FINE, "Locator client was set for proxy service.");
		}
	}

	@Override
	public void registerEndpoint(QName serviceName, String endpointURL)
			throws InterruptedExceptionFault, ServiceLocatorFault {
		try {
			locatorClient.register(serviceName, endpointURL);
		} catch (ServiceLocatorException e) {
			throw new InterruptedExceptionFault("", e);
		} catch (InterruptedException e) {
			throw new ServiceLocatorFault("", e);
		}
	}

	@Override
	public boolean unregisterEnpoint(QName serviceName, String endpointURL)
			throws InterruptedExceptionFault, ServiceLocatorFault {
		try {
			locatorClient.unregister(serviceName, endpointURL);
		} catch (ServiceLocatorException e) {
			throw new InterruptedExceptionFault("", e);
		} catch (InterruptedException e) {
			throw new ServiceLocatorFault("", e);
		}
		return true;
	}

	@Override
	public W3CEndpointReference lookupEndpoint(QName serviceName)
			throws InterruptedExceptionFault, ServiceLocatorFault {
		List<String> names = null;
		String adress;
		try {
			names = locatorClient.getEndpointNames(serviceName);
		} catch (ServiceLocatorException e) {
			throw new InterruptedExceptionFault("", e);
		} catch (InterruptedException e) {
			throw new ServiceLocatorFault("", e);
		}
		if (names != null && !names.isEmpty()) {
			names = getRotatedList(names);
			adress = names.get(0);
		} else {
			if (LOG.isLoggable(Level.WARNING)) {
				LOG.log(Level.WARNING, "lookup Endpoint " + serviceName
						+ " failed, service is not known.");
			}
			return null;
		}
		return buildEndpoint(serviceName, adress);
	}

	@Override
	public EndpointReferenceListType lookupEndpoints(QName serviceName) throws InterruptedExceptionFault, ServiceLocatorFault {
		List<String> names = null;
		List<W3CEndpointReference> endpointRefList = Collections.emptyList();
		String adress;
		try {
			names = locatorClient.getEndpointNames(serviceName);
		} catch (ServiceLocatorException e) {
			throw new InterruptedExceptionFault("", e);
		} catch (InterruptedException e) {
			throw new ServiceLocatorFault("", e);
		}
		if (names != null && !names.isEmpty()) {
			for (int i = 0; i < names.size(); i++) {
				adress = names.get(i);
				endpointRefList.add(buildEndpoint(serviceName, adress));
			}
			adress = names.get(0);
		} else {
			if (LOG.isLoggable(Level.WARNING)) {
				LOG.log(Level.WARNING, "lookup Endpoint " + serviceName
						+ " failed, service is not known.");
			}
			return null;
		}
		return (EndpointReferenceListType)endpointRefList;
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

	private W3CEndpointReference buildEndpoint(QName serviceName, String adress) {
		W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
		builder.serviceName(serviceName);
		builder.address(adress);
		return builder.build();
	}
}
