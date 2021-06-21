/*
 * #%L
 * Even Distribution Service Locator Selection Strategy
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.talend.esb.servicelocator.client.SLPropertiesMatcher;
import org.talend.esb.servicelocator.client.ServiceLocator;
import org.talend.esb.servicelocator.client.ServiceLocatorException;

public class LocatorCache {

	private static Map<String, List<String>> cachedAddresses = new HashMap<String, List<String>>();

	private static Map<String, Integer> lastIndex = new HashMap<String, Integer>();

	private static Map<String, Integer> cacheCounter = new HashMap<String, Integer>();

	private ServiceLocator serviceLocator;

	private SLPropertiesMatcher matcher = SLPropertiesMatcher.ALL_MATCHER;

	private int reloadCount = 10;

	private Random random = new Random();

	private String strategyId = "";

	static final Logger LOG = Logger.getLogger(LocatorCache.class.getName());

	public void setMatcher(SLPropertiesMatcher matcher) {
		this.matcher = matcher;
	}

	public void setServiceLocator(ServiceLocator serviceLocator) {
		this.serviceLocator = serviceLocator;
	}

	public void setReloadCount(int reloadCount) {
		this.reloadCount = reloadCount;
	}

	public void setStrategyId(String strategyId) {
		this.strategyId = strategyId;
	}

	synchronized String getPrimaryAddressSame(QName serviceName) {
		List<String> endpoints = getEndpoints(serviceName, false);
		if (endpoints == null || endpoints.isEmpty())
			return null;
		String key = getPrimaryAddressKey(serviceName);
		if (!lastIndex.containsKey(key)
				|| lastIndex.get(key) >= endpoints.size()) {
			lastIndex.put(key, random.nextInt(endpoints.size()));
		}
		String primaryAddress = endpoints.get(lastIndex.get(key));
		cacheCounter.put(key, 0); // cache never expires for this strategy
		if (LOG.isLoggable(Level.INFO)) {
			LOG.log(Level.INFO, "Get same primary address for service "
					+ serviceName + " selecting from " + endpoints
					+ " selected = " + primaryAddress);
		}
		return primaryAddress;
	}

	synchronized String getPrimaryAddressNext(QName serviceName) {
		List<String> endpoints = getEndpoints(serviceName, false);
		if (endpoints == null || endpoints.isEmpty())
			return null;
		String key = getPrimaryAddressKey(serviceName);
		if (!lastIndex.containsKey(key)) {
			lastIndex.put(key, random.nextInt(endpoints.size()));
		} else {
			lastIndex.put(key, (lastIndex.get(key) + 1) % endpoints.size());
		}
		String primaryAddress = endpoints.get(lastIndex.get(key));
		if (LOG.isLoggable(Level.INFO)) {
			LOG.log(Level.INFO, "Get next primary address for service "
					+ serviceName + " selecting from " + endpoints
					+ " selected = " + primaryAddress);
		}
		return primaryAddress;
	}

	synchronized String getPrimaryAddressRandom(QName serviceName) {
		List<String> endpoints = getEndpoints(serviceName, false);
		if (endpoints == null || endpoints.isEmpty())
			return null;
		String key = getPrimaryAddressKey(serviceName);
		lastIndex.put(key, random.nextInt(endpoints.size()));
		String primaryAddress = endpoints.get(lastIndex.get(key));
		if (LOG.isLoggable(Level.INFO)) {
			LOG.log(Level.INFO, "Get random primary address for service "
					+ serviceName + " selecting from " + endpoints
					+ " selected = " + primaryAddress);
		}
		return primaryAddress;
	}

	synchronized List<String> getFailoverEndpoints(QName serviceName) {
		List<String> endpoints = getEndpoints(serviceName, true);
		return new ArrayList<String>(endpoints);
	}

	private synchronized List<String> getEndpoints(QName serviceName,
			boolean isFailover) {
		List<String> endpoints = Collections.emptyList();
		String key = getPrimaryAddressKey(serviceName);
		if (isFailover || !cacheCounter.containsKey(key)
				|| cacheCounter.get(key) >= reloadCount) {
			endpoints = getLocatorEndpoints(serviceName);
			if (endpoints != null && !endpoints.isEmpty()) {
				cachedAddresses.put(key, endpoints);
				cacheCounter.put(key, 1);
			}
		} else {
			cacheCounter.put(key, cacheCounter.get(key) + 1);
			endpoints = cachedAddresses.get(key);
		}
		return endpoints;
	}

	private String getPrimaryAddressKey(QName serviceName) {
		return strategyId + "." + matcher == null ? serviceName.toString()
				: serviceName.toString() + "."
						+ matcher.getAssertionsAsString();
	}

	synchronized private List<String> getLocatorEndpoints(QName serviceName) {
		List<String> endpoints = Collections.emptyList();
		try {
			endpoints = serviceLocator.lookup(serviceName, matcher);
			if (LOG.isLoggable(Level.INFO)) {
				LOG.log(Level.INFO, "serviceLocator.lookup "
						+ " serviceName = " + serviceName + " matcher = "
						+ matcher.getAssertionsAsString() + " endpoints "
						+ endpoints);
			}
		} catch (ServiceLocatorException e) {
			if (LOG.isLoggable(Level.SEVERE)) {
				LOG.log(Level.SEVERE,
						"Can not refresh list of endpoints due to ServiceLocatorException",
						e);
			}
		} catch (InterruptedException e) {
			if (LOG.isLoggable(Level.SEVERE)) {
				LOG.log(Level.SEVERE,
						"Can not refresh list of endpoints due to InterruptedException",
						e);
			}
		}
		return endpoints;
	}
}
