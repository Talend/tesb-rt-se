/*
 * #%L
 * STS :: Config
 * %%
 * Copyright (C) 2011 - 2012 Talend Inc.
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
package org.talend.esb.sts.config;

import java.util.Collection;
import java.util.Dictionary;
import java.util.Properties;

import org.apache.cxf.Bus;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

public class StsConfigurator implements ManagedService {

	private static final String SERVICE_PID = "org.talend.esb.sts.server";
	private static final String LOGGING_PROPERTY = "useMessageLogging";
	
	private Bus bus;
	private BundleContext bundleContext;
	private ServiceRegistration stsConfiguratorServiceRegistration;
	
	StsConfigurator(Bus bus) {
		this.bus = bus;
       bundleContext = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
        stsConfiguratorServiceRegistration = registerManagedService(bundleContext, ManagedService.class, 
                this, SERVICE_PID);		
        //System.out.println("\nCXF bus = " + bus.getId());
        //System.out.println("\nBundle Context = " + bundleContext);   
	}
	
	public void shutDown() {
		if (stsConfiguratorServiceRegistration != null) {
			stsConfiguratorServiceRegistration.unregister();
		}
	}
	
	@Override
	public void updated(Dictionary props) throws ConfigurationException {
		Dictionary<String, String> properties = CastUtils.cast(props);
		String useMessageLogging = properties
				.get(LOGGING_PROPERTY);
		//System.out.println("\nUse Logging = " + useMessageLogging);
		setMessageLogging(useMessageLogging != null && useMessageLogging.equalsIgnoreCase("true"));
		//setMessageLoggingAll(useMessageLogging != null && useMessageLogging.equalsIgnoreCase("true"));
	}
	
	private ServiceRegistration registerManagedService(
			BundleContext context, Class<?> serviceClass, Object service,
			String servicePid) {
		Properties props = new Properties();
		props.put(Constants.SERVICE_PID, servicePid);
		return context.registerService(serviceClass.getName(), service, props);
	}

	private void setMessageLogging(boolean logMessages) {
		setMessageLogging(logMessages, bus);	
	}
	
	private void setMessageLogging(boolean logMessages, Bus bus) {
		if (logMessages) {
			if (!hasLoggingFeature(bus))
				addMessageLogging(bus);
		} else {
			if (hasLoggingFeature(bus))
				removeMessageLogging(bus);
		}	
	}

	private boolean hasLoggingFeature(Bus bus) {
		Collection<Feature> features = bus.getFeatures();
		for (Feature feature: features) {
			if (feature instanceof LoggingFeature)
				return true;
		}
		return false;
	}

	private void addMessageLogging(Bus bus) {
		LoggingFeature logFeature = new LoggingFeature();
		logFeature.initialize(bus);
		bus.getFeatures().add(logFeature);
	}
	
	private void removeMessageLogging(Bus bus) {
		Collection<Feature> features = bus.getFeatures();
		Feature logFeature = null;
		Interceptor inLogInterceptor = null;
		Interceptor outLogInterceptor = null;
		for (Feature feature: features) {
			if (feature instanceof LoggingFeature) {
				logFeature = feature;
				break;
			}
		}
		if (logFeature != null) {
			features.remove(logFeature);
		}
		for (Interceptor interceptor: bus.getInInterceptors()) {
			if (interceptor instanceof LoggingInInterceptor) {
				inLogInterceptor = interceptor;
				break;			
			}
		}
		for (Interceptor interceptor: bus.getOutInterceptors()) {
			if (interceptor instanceof LoggingOutInterceptor) {
				outLogInterceptor = interceptor;
				break;			
			}
		}
		if (inLogInterceptor != null) {
			bus.getInInterceptors().remove(inLogInterceptor);
			//System.out.println("\nRemove in Interceptor = " + inLogInterceptor.getClass().getName());
		}
		if (outLogInterceptor != null) {
			bus.getOutInterceptors().remove(outLogInterceptor);
			//System.out.println("\nRemove out Interceptor = " + inLogInterceptor.getClass().getName());
		}
	}
	
}
