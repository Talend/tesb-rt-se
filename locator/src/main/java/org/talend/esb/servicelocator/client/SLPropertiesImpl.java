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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SLPropertiesImpl implements SLProperties {

    public static final SLProperties EMPTY_PROPERTIES = new SLPropertiesImpl();
    
    private static final long serialVersionUID = -3527977700696163706L;

    private Map<String, Collection<String>> properties = new LinkedHashMap<String, Collection<String>>();

    public void addProperty(String name, String... values) {
        List<String> valueList = new ArrayList<String>();
        for (String value : values) {
            valueList.add(value);
        }
        properties.put(name, valueList);
    }

    public void addProperty(String name, Collection<String> values) {
        List<String> valueList = new ArrayList<String>(values);
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
    public Collection<String> getValues(String name) {
        return properties.get(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean includesValues(String name, String... values) {
        return includesValues(name, Arrays.asList(values));
    }

    @Override
    public boolean includesValues(String name, Collection<String> values) {
        Collection<String> propValues = properties.get(name);

        if (propValues == null) {
            return false;
        }

        return propValues.containsAll(values);
    }

}
