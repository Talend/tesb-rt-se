package org.talend.esb.mep.requestcallback.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.talend.esb.mep.requestcallback.feature.Configuration;
import org.talend.esb.mep.requestcallback.feature.RequestCallbackFeature;

public class ConfigurationImpl implements Map<String, Object>, Configuration {

	private Map<String, Object> userMap = null;
	private Map<String, Object> dynamicMap = null;
	private Map<String, Object> staticMap = null;
	private Map<String, Object> mergedMap = null;

	@Override
	public synchronized int size() {
		return getMergedMap().size();
	}

	@Override
	public synchronized boolean isEmpty() {
		if (mergedMap != null) {
			return mergedMap.isEmpty();
		}
		if (staticMap != null && !staticMap.isEmpty()) {
			return false;
		}
		if (dynamicMap != null && !dynamicMap.isEmpty()) {
			return false;
		}
		if (userMap != null && !userMap.isEmpty()) {
			return false;
		}
		return true;
	}

	@Override
	public synchronized boolean containsKey(Object key) {
		if (mergedMap != null) {
			return mergedMap.containsKey(key);
		}
		if (staticMap != null && staticMap.containsKey(key)) {
			return true;
		}
		if (dynamicMap != null && dynamicMap.containsKey(key)) {
			return true;
		}
		if (userMap != null && userMap.containsKey(key)) {
			return true;
		}
		return false;
	}

	@Override
	public synchronized boolean containsValue(Object value) {
		return getMergedMap().containsValue(value);
	}

	@Override
	public synchronized Object get(Object key) {
		if (mergedMap != null) {
			return mergedMap.get(key);
		}
		if (userMap != null) {
			final Object res = userMap.get(key);
			if (res != null) {
				return res;
			}
		}
		if (dynamicMap != null) {
			final Object res = dynamicMap.get(key);
			if (res != null) {
				return res;
			}
		}
		if (staticMap != null) {
			final Object res = staticMap.get(key);
			if (res != null) {
				return res;
			}
		}
		return null;
	}

	@Override
	public synchronized Object put(String key, Object value) {
		if (userMap == null) {
			userMap = new HashMap<String, Object>();
		}
		mergedMap = null;
		return userMap.put(key, value);
	}

	@Override
	public synchronized Object remove(Object key) {
		if (userMap == null) {
			return null;
		}
		final Object result = userMap.remove(key);
		if (result != null) {
			mergedMap = null;
			if (userMap.isEmpty()) {
				userMap = null;
			}
		}
		return result;
	}

	@Override
	public synchronized void putAll(Map<? extends String, ? extends Object> m) {
		if (userMap == null) {
			userMap = new HashMap<String, Object>();
		}
		mergedMap = null;
		userMap.putAll(m);
	}

	@Override
	public synchronized void clear() {
		if (userMap != null) {
			userMap = null;
			mergedMap = null;
		}
	}

	@Override
	public synchronized Set<String> keySet() {
		return getMergedMap().keySet();
	}

	@Override
	public synchronized Collection<Object> values() {
		return getMergedMap().values();
	}

	@Override
	public synchronized Set<Entry<String, Object>> entrySet() {
		return getMergedMap().entrySet();
	}

	public synchronized void updateDynamicConfiguration(
			Map<?, ?> updateMap, boolean replaceCurrent) {
		mergedMap = null;
		if (updateMap == null || updateMap.isEmpty()) {
			if (replaceCurrent) {
				dynamicMap = null;
			}
			return;
		}
		Map<String, Object> dynMap = dynamicMap == null || replaceCurrent
				? new HashMap<String, Object>() : dynamicMap;
		for (Entry<?, ?> entry : updateMap.entrySet()) {
			dynMap.put(entry.getKey().toString(), entry.getValue());
		}
		dynamicMap = dynMap;
	}

	public synchronized void updateDynamicConfiguration(
			Dictionary<?, ?> updateDict, boolean replaceCurrent) {
		mergedMap = null;
		if (updateDict == null || updateDict.isEmpty()) {
			if (replaceCurrent) {
				dynamicMap = null;
			}
			return;
		}
		Map<String, Object> dynMap = dynamicMap == null || replaceCurrent
				? new HashMap<String, Object>() : dynamicMap;
		for (Enumeration<?> keys = updateDict.keys(); keys.hasMoreElements(); ) {
			Object key = keys.nextElement();
			dynMap.put(key.toString(), updateDict.get(key));
		}
		dynamicMap = dynMap;
	}

	public synchronized void refreshStaticConfiguration() {
		staticMap = null;
		mergedMap = null;
		Properties staticProps = null;
		InputStream is = getClass().getClassLoader().getResourceAsStream(
				RequestCallbackFeature.REQUEST_CALLBACK_CONFIGURATION_RESOURCE);
		if (is != null) {
			try {
				staticProps = new Properties();
				staticProps.load(is);
			} catch (Exception e) {
				staticProps = null;
			} finally {
				try {
					is.close();
				} catch (Exception e) {
					// ignore
				}
				is = null;
			}
		}
		String sysprop = System.getProperty(
				RequestCallbackFeature.REQUEST_CALLBACK_CONFIGURATION_SYSTEM_PROPERTY);
		if (sysprop != null) {
			Properties props = staticProps;
			try {
				if (sysprop.startsWith("file:/") || sysprop.contains("://")) {
					URL configURL = new URL(sysprop);
					is = configURL.openStream();
					if (props == null) {
						props = new Properties();
					}
					props.load(is);
				} else {
					File configFile = new File(sysprop);
					if (configFile.canRead()) {
						is = new FileInputStream(configFile);
						props.load(is);
					}
				}
			} catch (Exception e) {
				props = null;
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (Exception e) {
						// ignore
					}
					is = null;
				}
			}
			if (staticProps == null) {
				staticProps = props;
			}
		}
		if (staticProps != null) {
			Map<String, Object> statMap = new HashMap<String, Object>();
			for (Entry<Object, Object> e : staticProps.entrySet()) {
				statMap.put(e.getKey().toString(), e.getValue());
			}
			staticMap = statMap;
		}
	}

	private Map<String, Object> getMergedMap() {
		if (mergedMap == null) {
			Map<String, Object> result = new HashMap<String, Object>();
			if (staticMap != null) {
				result.putAll(staticMap);
			}
			if (dynamicMap != null) {
				result.putAll(dynamicMap);
			}
			if (userMap != null) {
				result.putAll(userMap);
			}
			mergedMap = Collections.unmodifiableMap(result);
		}
		return mergedMap;
	}
}
