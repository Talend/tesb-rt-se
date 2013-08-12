/*
 * #%L
 * Talend ESB :: Adapters :: HQ :: Common
 * %%
 * Copyright (C) 2011 - 2013 Talend Inc.
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
package org.talend.esb.monitoring.hq;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;

import org.apache.commons.logging.Log;
import org.hyperic.hq.product.Metric;
import org.hyperic.hq.product.MetricInvalidException;
import org.hyperic.hq.product.MetricNotFoundException;
import org.hyperic.hq.product.MetricUnreachableException;
import org.hyperic.hq.product.MetricValue;
import org.hyperic.hq.product.PluginException;
import org.hyperic.hq.product.PluginManager;
import org.hyperic.hq.product.jmx.MxMeasurementPlugin;
import org.hyperic.hq.product.jmx.MxUtil;

import static org.talend.esb.monitoring.hq.HypericUtils.getOptionalProperty;
import static org.talend.esb.monitoring.hq.HypericUtils.getMandatoryProperty;

/**
 * This class implements a measurement plug-in for a Hyperic, that provides
 * metric measurement for JMX services with a dynamic name. Provides also
 * caching for object names.
 * 
 * @author Eugene Tarasov
 */
public class DynamicMxFieldMeasurementPlugin extends MxMeasurementPlugin {

	private final Log log = getLog();

	/**
	 * Hyperic initializes the measurement plug-in as many times as many
	 * platforms it supports. We use this property to make sure we print
	 * initialization logs only for the first instance (because for all other
	 * instances it's the same).
	 */
	private static final AtomicBoolean isFirstInstance = new AtomicBoolean(true);

	private static final int DEFAULT_GC_INTERVAL = 24 * 60 * 60 * 1000;

	private static final String MSG_ERR_CONNECT = "Cannot find the new MBean name because of the connection problems.";
	private static final String MSG_ERR_NOTFOUND = "ObjectName has not been found.";
	private static final String MSG_ERR_MALFORMED = "Provided name has wrong format.";
	private static final String MSG_ERR_UNEXPECTED = "Unexpected problem during looking up an ObjectName.";

	private String targetDomain;

	private final TrivialCache cache = new TrivialCache();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(PluginManager manager) throws PluginException {
		super.init(manager);

		targetDomain = getMandatoryProperty(this,
				TalendHqConstants.PROP_TARGET_DOMAIN) + ":";

		boolean firstInstance = isFirstInstance.getAndSet(false);

		final String gcIntervalStr = getOptionalProperty(this,
				TalendHqConstants.PROP_GC_INTERVAL);
		if (gcIntervalStr == null) {
			logInfoOnlyFirstInstance(
					firstInstance,
					"No explicit cache periodical cleanup interval specified, scheduling it to happen every ",
					DEFAULT_GC_INTERVAL, " ms by default.");
			cache.scheduleGc(DEFAULT_GC_INTERVAL);
		} else {
			try {
				final long interval = Long.valueOf(gcIntervalStr);
				if (interval > 0) {
					logInfoOnlyFirstInstance(
							firstInstance,
							"Scheduling cache periodical cleanup to happen every ",
							interval, " ms.");
					cache.scheduleGc(interval);
				} else {
					logInfoOnlyFirstInstance(firstInstance,
							"Cache periodical cleanup is switched off.");
				}
			} catch (NumberFormatException e) {
				throw new PluginException(
						"Cannot read cache gc interval value.", e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void shutdown() throws PluginException {
		super.shutdown();
		cache.clear();
	}

	/**
	 * Looks up for a ObjectName based on a given pattern ObjectName.
	 * 
	 * @param patternObjectName
	 *            a pattern object name
	 * @param connectionProps
	 *            connection properties
	 * @return Returns the found ObjectName, otherwise throws an exception
	 * @throws MetricNotFoundException
	 *             is thrown when no metric was found using specified pattern
	 * @throws MetricUnreachableException
	 *             is thrown when it is impossible to find the object name
	 *             because of connection problems
	 * @throws MetricInvalidException
	 *             is thrown if patter format is wrong
	 * @throws PluginException
	 *             if some unexpected problem happened
	 */
	private String findObjectName(final String patternObjectName,
			final Properties connectionProps) throws MetricNotFoundException,
			MetricUnreachableException, MetricInvalidException, PluginException {

		ObjectName patternOn;
		try {
			patternOn = new ObjectName(patternObjectName);
		} catch (MalformedObjectNameException e) {
			throw new MetricInvalidException(MSG_ERR_MALFORMED, e);
		}

		JMXConnector jmxConnector = null;
		try {
			jmxConnector = MxUtil.getCachedMBeanConnector(connectionProps);
			final MBeanServerConnection conn = jmxConnector
					.getMBeanServerConnection();
			final Set<ObjectName> result = conn.queryNames(patternOn, null);

			if (result.iterator().hasNext()) {
				return result.iterator().next().getCanonicalName();
			}
		} catch (IOException e) {
			log.debug(MSG_ERR_CONNECT, e);
			throw new MetricUnreachableException(MSG_ERR_CONNECT, e);
		} catch (Exception e) {
			log.debug(MSG_ERR_UNEXPECTED, e);
			throw new PluginException(MSG_ERR_UNEXPECTED, e);
		} finally {
			// it's null-proof
			MxUtil.close(jmxConnector);
		}

		throw new MetricNotFoundException(MSG_ERR_NOTFOUND);
	}

	/**
	 * {@inheritDoc}
	 */
	public MetricValue getValue(final Metric metric) throws PluginException,
			MetricNotFoundException, MetricUnreachableException,
			MetricInvalidException {

		final String origObjectName = metric.getObjectName();
		final String oldObjectName = Metric.decode(origObjectName);

		// no lookup for non-supported domain
		if (!oldObjectName.startsWith(targetDomain)) {
			return super.getValue(metric);
		}

		// a simple test for static object name
		if (!oldObjectName.contains("*")) {
			return super.getValue(metric);
		}

		boolean fromCache;
		String newObjectName;

		if (cache.contains(oldObjectName)) {
			newObjectName = cache.get(oldObjectName);
			fromCache = true;
		} else {
			try {
				newObjectName = findObjectName(oldObjectName,
						metric.getProperties());
				fromCache = false;
				cache.put(oldObjectName, newObjectName);
			} catch (MetricUnreachableException e) {
				if (metric.isAvail()) {
					return new MetricValue(Metric.AVAIL_DOWN);
				}
				throw e;
			}
		}

		try {
			metric.setObjectName(newObjectName);

			try {
				return super.getValue(metric);
			} catch (MetricNotFoundException e) {
				if (!(e.getCause() instanceof InstanceNotFoundException)
						|| !fromCache) {
					throw e;
				}

				cache.invalidate(oldObjectName);

				try {
					newObjectName = findObjectName(oldObjectName,
							metric.getProperties());
				} catch (MetricUnreachableException e2) {
					if (metric.isAvail()) {
						return new MetricValue(Metric.AVAIL_DOWN);
					}
					throw e2;
				}

				cache.put(oldObjectName, newObjectName);
				metric.setObjectName(newObjectName);

				return super.getValue(metric);
			}
		} finally {
			// hyperic caches metrics, so we must restore original object name
			// but only if object name was changed
			if (metric.getObjectName() != origObjectName) {
				metric.setObjectName(origObjectName);
			}
		}
	}

	/**
	 * This class implements cache for keeping discovered dynamic object names.
	 * It provides a simple GC-like daemon for cleaning unused dynamic names.
	 * It's methods are synchronized.
	 */
	static final class TrivialCache {
		private volatile Map<String, Entry> store = new HashMap<String, Entry>();
		private final Object lock = new Object();
		private final Timer gcTimer = new Timer();

		/**
		 * Stops the GC and clears the cache store. The cache cannot be used
		 * after shutting it down.
		 */
		public void shutdown() {
			gcTimer.cancel();
			gcTimer.purge();
			store.clear();
			store = null;
		}

		/**
		 * @param key
		 *            cached entry key
		 * @return Stored value, or null if no entry with specified key exists
		 */
		public String get(String key) {
			final Entry result = store.get(key);
			if (result == null) {
				return null;
			}
			if (result.outdated) {
				synchronized (lock) {
					result.outdated = false;
				}
			}
			return result.value;
		}

		/**
		 * Checks whether anything is cached with the specified key.
		 * 
		 * @param key
		 *            a key to check
		 * @return true if there is a cache entry with specified key, false
		 *         otherwise
		 * @throws NullPointerException
		 *             if key is null
		 */
		public boolean contains(final String key) throws NullPointerException {
			if (key == null) {
				throw new NullPointerException("A key cannot be null.");
			}
			return store.containsKey(key);
		}

		/**
		 * @return count of entries in the cache
		 */
		public int size() {
			return store.size();
		}

		/**
		 * Puts a new entry into the cache. If an entry with specified key
		 * already exists, it will be overwritten.
		 * 
		 * @param key
		 *            a key of the entry
		 * @param value
		 *            value to store in the cache
		 */
		public void put(final String key, final String value) {
			synchronized (lock) {
				store.put(key, new Entry(value, false));
			}
		}

		/**
		 * Invalidates (removes) an entry with specified key. If there is no an
		 * entry with specified key, then nothing happens.
		 * 
		 * @param key
		 *            a key of an entry to invalidate
		 */
		public void invalidate(final String key) {
			synchronized (lock) {
				store.remove(key);
			}
		}

		/**
		 * Removes all entries for the cache.
		 */
		public void clear() {
			synchronized (lock) {
				store.clear();
			}
		}

		/**
		 * Schedules a GC-like daemon that removes unused entries from the
		 * cache.
		 * 
		 * @param period
		 *            a period of time (in milliseconds) between checks
		 */
		public void scheduleGc(long period) {
			gcTimer.schedule(new GcTask(), period, period);
		}

		// stops garbage collector
		void stopGc() {
			gcTimer.cancel();
		}

		/**
		 * Garbage Collector task.
		 */
		private class GcTask extends TimerTask {
			@Override
			public void run() {
				final Map<String, Entry> newStore = new HashMap<String, Entry>();

				synchronized (lock) {
					if (store.isEmpty()) {
						return;
					}

					for (String key : store.keySet()) {
						final Entry entry = store.get(key);

						if (!entry.outdated) {
							newStore.put(key, new Entry(entry.value, true));
						}
					}

					final Map<String, Entry> oldStore = store;
					store = newStore;
					oldStore.clear();
				}
			}
		}

		/**
		 * This class represents a cache entry.
		 */
		private static class Entry {
			final String value;
			volatile boolean outdated;

			public Entry(final String value, boolean outdated) {
				this.value = value;
				this.outdated = outdated;
			}
		}

	}

	// logging helper method, to prevent unnecessary string concatenation
	private void logInfo(final Object o1, final Object... info) {
		if (!log.isInfoEnabled()) {
			return;
		}

		if (info.length == 0) {
			log.info(o1.toString());
			return;
		}

		final StringBuilder sb = new StringBuilder(o1.toString());
		for (final Object o : info) {
			sb.append(o.toString());
		}

		log.info(sb.toString());
	}

	private void logInfoOnlyFirstInstance(boolean firstInstance,
			final Object o1, final Object... info) {
		if (firstInstance) {
			logInfo(o1, info);
		}
	}
}
