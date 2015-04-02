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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.cxf.common.logging.LogUtils;
import org.talend.esb.mep.requestcallback.feature.RequestCallbackFeature;

public class ConfigurationImpl extends AbstractConfiguration {

	private static final Logger LOGGER = LogUtils.getL7dLogger(ConfigurationImpl.class);
	private Map<String, Object> userMap = null;
	private Map<String, Object> dynamicMap = null;
	private Map<String, Object> staticMap = null;
	private Map<String, Object> mergedMap = null;
	private ChangeListener changeListener = null;
	private final QName configurationName;
	private final String configurationId;
	private final String alternateConfigId;

	public ConfigurationImpl(QName configurationName) {
		super();
		this.configurationName = configurationName;
		configurationId = asConfigIdentifier(configurationName);
		if (configurationName == null) {
			alternateConfigId = null;
		} else {
			final String cfgid = asConfigIdentifier(configurationName.getLocalPart());
			alternateConfigId = configurationId.equals(cfgid) ? null : cfgid;
		}
	}

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

	@Override
	public synchronized void updateDynamicConfiguration(
			Map<?, ?> updateMap, boolean replaceCurrent) {

		if (updateMap == null || updateMap.isEmpty()) {
			if (replaceCurrent) {
				mergedMap = null;
				dynamicMap = null;
				if (changeListener != null) {
					changeListener.changed(this);
				}
			}
			return;
		}
		mergedMap = null;
		Map<String, Object> dynMap = dynamicMap == null || replaceCurrent
				? new HashMap<String, Object>() : dynamicMap;
		for (Entry<?, ?> entry : updateMap.entrySet()) {
			dynMap.put(entry.getKey().toString(), entry.getValue());
		}
		dynamicMap = dynMap;
		if (changeListener != null) {
			changeListener.changed(this);
		}
	}

	@Override
	public synchronized void updateDynamicConfiguration(
			Dictionary<?, ?> updateDict, boolean replaceCurrent) {

		if (updateDict == null || updateDict.isEmpty()) {
			if (replaceCurrent) {
				mergedMap = null;
				dynamicMap = null;
				if (changeListener != null) {
					changeListener.changed(this);
				}
			}
			return;
		}
		mergedMap = null;
		Map<String, Object> dynMap = dynamicMap == null || replaceCurrent
				? new HashMap<String, Object>() : dynamicMap;
		for (Enumeration<?> keys = updateDict.keys(); keys.hasMoreElements(); ) {
			Object key = keys.nextElement();
			dynMap.put(key.toString(), updateDict.get(key));
		}
		dynamicMap = dynMap;
		if (changeListener != null) {
			changeListener.changed(this);
		}
	}

	@Override
	public synchronized void refreshStaticConfiguration() {
		staticMap = null;
		mergedMap = null;
		final String cfgFileName = configurationId + ".properties";
		final String altFileName = alternateConfigId == null
				? null : alternateConfigId + ".properties";
		Properties staticProps = null;
		Properties loadedProps = null;
		InputStream is = getClass().getClassLoader().getResourceAsStream(cfgFileName);
		loadedProps = loadProps(staticProps, is);
		if (loadedProps == null && altFileName != null) {
			is = getClass().getClassLoader().getResourceAsStream(altFileName);
			loadedProps = loadProps(staticProps, is);
		}
		if (loadedProps != null) {
			staticProps = loadedProps;
		}
		String sysprop = System.getProperty(
				RequestCallbackFeature.REQUEST_CALLBACK_CONFIGURATION_SYSTEM_PROPERTY);
		if (sysprop != null) {
			try {
				if (sysprop.startsWith("file:/") || sysprop.contains("://")) {
					String cfgURLName = sysprop.endsWith("/")
							? sysprop + cfgFileName : sysprop + "/" + cfgFileName;
					try {
						URL configURL = new URL(cfgURLName);
						is = configURL.openStream();
					} catch (Exception e) {
						if (LOGGER.isLoggable(Level.FINER)) {
							LOGGER.log(Level.FINER, "Exception caught: ", e);
						}
						is = null;
					}
					loadedProps = loadProps(staticProps, is);
					if (loadedProps == null && altFileName != null) {
						cfgURLName = sysprop.endsWith("/")
								? sysprop + altFileName : sysprop + "/" + altFileName;
						try {
							URL configURL = new URL(cfgURLName);
							is = configURL.openStream();
						} catch (Exception e) {
							if (LOGGER.isLoggable(Level.FINER)) {
								LOGGER.log(Level.FINER, "Exception caught: ", e);
							}
							is = null;
						}
						loadedProps = loadProps(staticProps, is);
					}
				} else {
					File configDir = new File(sysprop);
					if (configDir.canRead() && configDir.isDirectory()) {
						File cfgFile = new File(configDir, cfgFileName);
						if (cfgFile.isFile() && cfgFile.canRead()) {
							is = new FileInputStream(cfgFile);
							loadedProps = loadProps(staticProps, is);
						} else if (altFileName != null) {
							cfgFile = new File(configDir, altFileName);
							if (cfgFile.isFile() && cfgFile.canRead()) {
								is = new FileInputStream(cfgFile);
								loadedProps = loadProps(staticProps, is);
							}
						}
					}
				}
			} catch (Exception e) {
				if (LOGGER.isLoggable(Level.FINER)) {
					LOGGER.log(Level.FINER, "Exception caught: ", e);
				}
				loadedProps = null;
			}
			if (loadedProps != null) {
				staticProps = loadedProps;
			}
		}
		if (staticProps != null) {
			Map<String, Object> statMap = new HashMap<String, Object>();
			for (Entry<Object, Object> e : staticProps.entrySet()) {
				statMap.put(e.getKey().toString(), e.getValue());
			}
			staticMap = statMap;
		}
		if (changeListener != null) {
			changeListener.changed(this);
		}
	}

	@Override
	public synchronized void fillProperties(
			String prefix, Map<? super String, Object> properties) {

		if (mergedMap != null) {
			transferProperties(prefix, mergedMap, properties);
			return;
		}
		if (staticMap != null) {
			transferProperties(prefix, staticMap, properties);
		}
		if (dynamicMap != null) {
			transferProperties(prefix, dynamicMap, properties);
		}
		if (userMap != null) {
			transferProperties(prefix, userMap, properties);
		}
	}

	@Override
	public synchronized void fillExpandedProperties(
			String prefix, Map<? super String, Object> properties) {

		final Map<String, Object> source = getMergedMap();
		transferExpandedProperties(prefix, source, properties, source);
	}

	@Override
	public QName getConfigurationName() {
		return configurationName;
	}

	@Override
	public String getConfigurationIdentifier() {
		return configurationId;
	}

	@Override
	public String getAlternateConfigurationIdentifier() {
		return alternateConfigId;
	}

	@Override
	public ChangeListener getChangeListener() {
		return changeListener;
	}

	@Override
	public void setChangeListener(ChangeListener changeListener) {
		this.changeListener = changeListener;
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

	private static Properties loadProps(Properties props, InputStream is) {
		if (is == null) {
			return null;
		}
		try {
			Properties result = props == null ? new Properties() : props;
			result.load(is);
			return result;
		} catch (Exception e) {
			if (LOGGER.isLoggable(Level.FINER)) {
				LOGGER.log(Level.FINER, "Exception caught: ", e);
			}
			return null;
		} finally {
			try {
				is.close();
			} catch (Exception e) {
				if (LOGGER.isLoggable(Level.FINER)) {
					LOGGER.log(Level.FINER, "Exception caught: ", e);
				}
				// ignore
			}
		}
	}
}
