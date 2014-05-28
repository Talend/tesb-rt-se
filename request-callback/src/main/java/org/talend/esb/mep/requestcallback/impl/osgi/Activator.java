package org.talend.esb.mep.requestcallback.impl.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		ConfigurationUpdater.activateUpdaters(context);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		ConfigurationUpdater.deactivateUpdaters();
	}
}
