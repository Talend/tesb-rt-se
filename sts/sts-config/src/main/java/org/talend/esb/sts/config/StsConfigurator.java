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
import org.apache.cxf.Bus;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;

public class StsConfigurator {

	private String useMessageLogging = null; 
	
	private Bus bus;
	
	public StsConfigurator(Bus bus) {
		this.bus = bus;
	}
	
	public void init() {
		setMessageLogging(useMessageLogging != null && useMessageLogging.equalsIgnoreCase("true"));
	}
	
	public void setUseMessageLogging(String useMessageLogging){
		this.useMessageLogging = useMessageLogging;
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
