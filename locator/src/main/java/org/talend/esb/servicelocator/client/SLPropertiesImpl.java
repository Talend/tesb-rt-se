/*
 * #%L
 * Service Locator Client for CXF
 * %%
 * Copyright (C) 2011 Talend Inc.
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
package org.talend.esb.servicelocator.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SLPropertiesImpl implements SLProperties {
	
	private static final long serialVersionUID = -3527977700696163706L;
	
	private Map<String, Object> properties = new HashMap<String, Object>();

	public void addProperty(String name, String value) {
		properties.put(name, value);
	}

	public void addMultiProperty(String name, String... values) {
		List<String> valueList = new ArrayList<String>();
		for (String value : values) {
			valueList.add(value);
		}
		properties.put(name, valueList);		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<String> getPropertyNames() {
		return properties.keySet();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasProperty(String name) {
		return properties.containsKey(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getValue(String name) {
		Object rawValue =  properties.get(name);

		if (rawValue == null || rawValue instanceof String) {
			return (String) properties.get(name);
		} else {
			throw new IllegalArgumentException(
				"The property " + name + "is multivalued. Use the method getValues instead to retrieve the collection of values");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Collection<String> getValues(String name) {
		Object rawValue =  properties.get(name);
		
		if (rawValue instanceof String) {
			return Collections.singletonList((String)rawValue);
		} else {
			return (Collection<String>) rawValue;
		}
	}

	/* (non-Javadoc)
	 * @see org.talend.esb.servicelocator.SLProperties#includesValue(java.lang.String, java.lang.String)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public boolean includesValues(String name, String... values) {
		Object rawValue =  properties.get(name);

		if (rawValue == null) {
			return false;
		}

		Collection<? extends String> propValues = null;
		if (rawValue instanceof String) {
			propValues = Collections.singletonList((String) rawValue);
		} else {
			propValues = (Collection<? extends String>) rawValue;
		}
		return propValues.containsAll(Arrays.asList(values));
	}
}
