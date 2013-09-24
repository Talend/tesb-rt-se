/*
 * #%L
 * Service Locator Client for CXF
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
package org.talend.esb.servicelocator.cxf;

import java.util.Map;

import org.apache.cxf.feature.Feature;

/**
 * CXF feature to enable the locator client with an CXF service.
 * 
 */
public interface LocatorFeatureInterface extends Feature {

	/**
	 * Set endpoint properties on service side
	 * @param properties
	 */
	void setAvailableEndpointProperties(Map<String, String> properties);

	/**
	 * Set endpoint properties on client side for match
	 * @param properties
	 */
	void setRequiredEndpointProperties(Map<String, String> properties);

	/**
	 * Set selection strategy
	 * @param selectionStrategy
	 */
	void setSelectionStrategy(String selectionStrategy);

}
