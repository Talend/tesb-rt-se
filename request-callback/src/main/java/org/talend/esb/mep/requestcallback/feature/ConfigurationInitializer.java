package org.talend.esb.mep.requestcallback.feature;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.talend.esb.mep.requestcallback.impl.ConfigurationImpl;

public final class ConfigurationInitializer {

	public interface ConfigurationCreationListener {
		void configurationCreated(Configuration configuration);
	}

	private static final class Manager {

		private final Map<QName, Configuration> configs =
				new HashMap<QName, Configuration>();
		private final List<ConfigurationCreationListener> listeners =
				new LinkedList<ConfigurationCreationListener>();

		public Manager() {
			super();
		}

		public synchronized Configuration resolveConfiguration(QName serviceName) {
			final QName key = serviceName == null ? NULL_NAME : serviceName;
			Configuration config = configs.get(key);
			if (config == null) {
				config = createConfiguration(serviceName);
				configs.put(key, config);
			}
			return config;
		}

		public Map<QName, Configuration> getConfigurations() {
			return Collections.unmodifiableMap(configs);
		}

		public synchronized boolean addConfigurationCreationListener(
				ConfigurationCreationListener listener,
				boolean triggerForExisting) {
			if (listeners.add(listener)) {
				if (triggerForExisting) {
					for (Configuration config : configs.values()) {
						listener.configurationCreated(config);
					}
				}
				return true;
			}
			return false;
		}

		public synchronized boolean removeConfigurationCreationListener(
				ConfigurationCreationListener listener) {
			return listeners.remove(listener);
		}

		private Configuration createConfiguration(QName serviceName) {
			ConfigurationImpl result = new ConfigurationImpl(serviceName);
			result.refreshStaticConfiguration();
			for (ConfigurationCreationListener listener : listeners) {
				listener.configurationCreated(result);
			}
			return result;
		}
	}

	private static final QName NULL_NAME = new QName("!NULL!");
	private static final Manager MANAGER = new Manager();

	static {
		MANAGER.resolveConfiguration(null);
	}

	private ConfigurationInitializer() {
		super();
	}

	public static Configuration resolveConfiguration(QName serviceName) {
		return MANAGER.resolveConfiguration(serviceName);
	}

	public Map<QName, Configuration> getConfigurations() {
		return MANAGER.getConfigurations();
	}

	public static boolean addConfigurationCreationListener(
			ConfigurationCreationListener listener,
			boolean triggerForExisting) {
		return MANAGER.addConfigurationCreationListener(
				listener, triggerForExisting);
	}

	public static boolean removeConfigurationCreationListener(
			ConfigurationCreationListener listener) {
		return MANAGER.removeConfigurationCreationListener(listener);
	}
}
