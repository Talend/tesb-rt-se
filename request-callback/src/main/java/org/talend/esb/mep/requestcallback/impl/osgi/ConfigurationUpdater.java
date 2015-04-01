package org.talend.esb.mep.requestcallback.impl.osgi;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.talend.esb.mep.requestcallback.feature.Configuration;
import org.talend.esb.mep.requestcallback.feature.Configuration.PidMode;
import org.talend.esb.mep.requestcallback.feature.ConfigurationInitializer;
import org.talend.esb.mep.requestcallback.feature.ConfigurationInitializer.ConfigurationCreationListener;

public class ConfigurationUpdater implements ManagedService {

	private static final class Manager {

		private final List<ConfigurationUpdater> updaters =
				new LinkedList<ConfigurationUpdater>();
		private BundleContext bundleContext = null;

		public Manager() {
			super();
		}

		public synchronized boolean addUpdater(ConfigurationUpdater updater) {
			if (updaters.add(updater)) {
				if (bundleContext != null) {
					updater.register(bundleContext);
				}
				return true;
			}
			return false;
		}

		public synchronized boolean removeUpdater(ConfigurationUpdater updater) {
			if (updaters.remove(updater)) {
				updater.unregister();
				return true;
			}
			return false;
		}

		public synchronized void activateUpdaters(final BundleContext bundleContext) {
			if (bundleContext == null) {
				return;
			}
			deactivateUpdaters();
			for (ConfigurationUpdater updater : updaters) {
				updater.register(bundleContext);
			}
			this.bundleContext = bundleContext;
		}

		public synchronized void deactivateUpdaters() {
			if (bundleContext == null) {
				return;
			}
			for (ConfigurationUpdater updater : updaters) {
				updater.unregister();
			}
			bundleContext = null;
		}
	}

	private static final Manager MANAGER = new Manager();

	private final Configuration configuration;
	private String configIdentifier = null;
	private ServiceRegistration<?> registration = null;

	static {
		setupListener();
	}

	public ConfigurationUpdater(Configuration configuration) {
		super();
		this.configuration = configuration;
	}

	@Override
	public synchronized void updated(@SuppressWarnings("rawtypes") Dictionary properties)
			throws ConfigurationException {
		if (properties == null) {
			return;
		}
		configuration.updateDynamicConfiguration(properties, true);
	}

	public synchronized void register(BundleContext bundleContext) {
		if (registration != null) {
			registration.unregister();
		}
		configIdentifier = resolveConfigIdentifier();
		registration = register(bundleContext, configIdentifier);
	}

	public synchronized void unregister() {
		if (registration != null) {
			registration.unregister();
			registration = null;
		}
	}

	public static boolean addUpdater(ConfigurationUpdater updater) {
		return MANAGER.addUpdater(updater);
	}

	public static boolean removeUpdater(ConfigurationUpdater updater) {
		return MANAGER.removeUpdater(updater);
	}

	public static void activateUpdaters(BundleContext bundleContext) {
		MANAGER.activateUpdaters(bundleContext);
	}

	public static void deactivateUpdaters() {
		MANAGER.deactivateUpdaters();
	}

	private String resolveConfigIdentifier() {
		if (configuration.getAlternateConfigurationIdentifier() == null) {
			return configuration.getConfigurationIdentifier();
		}
		final Configuration baseConfig = ConfigurationInitializer.resolveConfiguration(null);
		String key = configuration.getConfigurationName().toString() + ".pidMode";
		PidMode pidMode = baseConfig.getPidModeProperty(key);
		if (pidMode == null) {
			pidMode = baseConfig.getPidModeProperty("default.pidMode");
		}
		if (pidMode == PidMode.FULL_NAME) {
			return configuration.getConfigurationIdentifier();
		}
		return configuration.getAlternateConfigurationIdentifier();
	}

	private ServiceRegistration<?> register(
			BundleContext bundleContext, String servicePid) {
		final Hashtable<String, Object> properties =
				new Hashtable<String, Object>();
		properties.put(Constants.SERVICE_PID, servicePid);
		return bundleContext.registerService(
				ManagedService.class.getName(), this, properties);
	}

	private static void registerUpdaterFor(Configuration configuration) {
		ConfigurationUpdater updater = new ConfigurationUpdater(configuration);
		MANAGER.addUpdater(updater);
	}

	private static void setupListener() {
		ConfigurationInitializer.addConfigurationCreationListener(
				new ConfigurationCreationListener() {
					@Override
					public void configurationCreated(Configuration configuration) {
						registerUpdaterFor(configuration);
					}
				}, true);
	}
}
