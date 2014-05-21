package org.talend.esb.mep.requestcallback.impl.osgi;

import java.util.Dictionary;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.talend.esb.mep.requestcallback.feature.CallContext;

public class ConfigurationUpdater implements ManagedService {

	@Override
	public void updated(@SuppressWarnings("rawtypes") Dictionary properties)
			throws ConfigurationException {
		CallContext.getConfiguration().updateDynamicConfiguration(properties);
		
	}

}
