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

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.hyperic.hq.product.PluginException;
import org.hyperic.hq.product.PluginManager;
import org.hyperic.hq.product.ServiceResource;
import org.hyperic.hq.product.jmx.MxServerDetector;
import org.hyperic.util.config.ConfigResponse;

import static org.talend.esb.monitoring.hq.HypericUtils.logInfo;
import static org.talend.esb.monitoring.hq.HypericUtils.getOptionalProperty;
import static org.talend.esb.monitoring.hq.HypericUtils.getMandatoryProperty;

/**
 * This class is a detector plug-in for Hyperic. It provides
 * discovery of JMX servers with dynamic object names.
 * 
 * @author Eugene Tarasov
 */
public class DynamicMxFieldServerDetector extends MxServerDetector {
	
	/**
	 * Logger for this class instance.
	 */
	private final Log log = getLog();
	
	/**
	 * Specifies a regular expression that its used to detect dynamic names. It must contain
	 * a single capturing group, that must specify the constant part of the dynamic name.
	 */
	private Pattern dynamicFieldPattern = null;
	
	/**
	 * A key for the dynamic name field.
	 */
	private String dynamicFieldName;
	
	/**
	 * Target format of the field value. {} is getting replaced with
	 * the text, captured from dynamic field pattern.
	 */
	private String targetFormat;
	
	/**
	 * A title of the application to discover.
	 */
	private String title;
	
	@Override
	public void init(PluginManager manager) throws PluginException {
		super.init(manager);
		
		title = getMandatoryProperty(this, TalendHqConstants.PROP_SERVER_TITLE);
		
		final String sDynField = getOptionalProperty(this, TalendHqConstants.PROP_DF_PATTERN);
		if (sDynField != null && !"".equals(sDynField)) {
			dynamicFieldPattern = Pattern.compile(sDynField);
			dynamicFieldName = getMandatoryProperty(this, TalendHqConstants.PROP_DF_NAME);
			targetFormat = getMandatoryProperty(this, TalendHqConstants.PROP_TARGET_FORMAT);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("rawtypes")
	protected List discoverServices(final ConfigResponse serverConfig) throws PluginException {
		logInfo(log, "Starting discovery of ", title, " services.");
		final List services = super.discoverServices(serverConfig);
		
		if (dynamicFieldPattern == null) {
			return services;
		}
		
		for (final Object obj : services) {
			if (obj instanceof ServiceResource) {
				final ServiceResource service = (ServiceResource) obj;
				
				final ConfigResponse config = service.getProductConfig();
				final String dfValue = config.getValue(dynamicFieldName);
				final Matcher m = dynamicFieldPattern.matcher(dfValue);
				
				if (m.matches() && m.groupCount() > 0) {
					final String constantFieldValue = m.group(1);
					config.setValue(dynamicFieldName, constantFieldValue);
					
					final String formatedFieldValue = formatFieldValue(targetFormat, constantFieldValue);
					
					final String name = formatAutoInventoryName(service.getType(),
							serverConfig, config, null);
					
					config.setValue(dynamicFieldName, formatedFieldValue);
					
					service.setName(name);
					service.setProductConfig(config);
					
					logInfo(log, "Found dynamic field value '", dfValue, "', converted it into '", formatedFieldValue, "'.");
				} else {
					logInfo(log, "Found static field value '", dfValue, "'.");
				}
			} else {
				log.fatal("Unexpected object of class " + obj.getClass().getName());
			}
		}
		logInfo(log, "Discovery of ", title, " services has finished. ", services.size(), " service(s) found.");
		return services;
	}
	
	protected String formatFieldValue(String targetFormat, String fieldValue) {
		return targetFormat.replace("{}", fieldValue);
	}
}
