package org.talend.esb.mep.requestcallback.impl.osgi;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedService;
import org.talend.esb.mep.requestcallback.feature.RequestCallbackFeature;

public class Activator implements BundleActivator {

	ServiceRegistration serviceRegistration = null;

	@Override
	public void start(BundleContext context) throws Exception {
		final Hashtable<String, Object> properties = new Hashtable<String, Object>();
		properties.put(Constants.SERVICE_PID,
				RequestCallbackFeature.REQUEST_CALLBACK_CONFIGURATION_OSGI_PROPERTY);
		serviceRegistration = context.registerService(
				ManagedService.class.getName(), new ConfigurationUpdater(), properties);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		if (serviceRegistration != null) {
			serviceRegistration.unregister();
			serviceRegistration = null;
		}
	}

}
