/*
 * #%L
 * Service Locator Client for CXF
 * %%
 * Copyright (c) 2006-2021 Talend Inc. - www.talend.com
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

/**
 * Implementation of {@link SLProperties} backed by a {@link Map}. In addition to the methods exposed in
 * <code>SLProperties<code> it also offers the methods {@link #addProperty(String, Iterable)} and
 *  {@link #addProperty(String, String...) to actually add properties.
 */
public class SLPropertiesImpl implements SLProperties {

    public static final SLProperties EMPTY_PROPERTIES = new SLPropertiesImpl();

    private static final long serialVersionUID = -3527977700696163706L;

    //A linked hash map is used because it preserves ordering when iterating over the entries.
    //So ordering is the same also when serialized to XML and deserialized from.
    private Map<String, Collection<String>> properties = new LinkedHashMap<String, Collection<String>>();

    /**
     * Add a property with the given name and the given list of values to this Properties object. Name
     * and values are trimmed before the property is added.
     *
     * @param name the name of the property, must not be <code>null</code>.
     * @param values the values of the property, must no be <code>null</code>,
     *               none of the  values must be <code>null</code>
     */
    public void addProperty(String name, String... values) {
        List<String> valueList = new ArrayList<String>();
        for (String value : values) {
            valueList.add(value.trim());
        }
        properties.put(name.trim(), valueList);
    }

    /**
     * Add a property with the given name and the given collection of values to this Properties object. Name
     * and values are trimmed before the property is added.
     *
     * @param name the name of the property, must not be <code>null</code>.
     * @param values the values of the property, must no be <code>null</code>,
     *               none of the  values must be <code>null</code>
     */
    public void addProperty(String name, Iterable<String> values) {
        List<String> valueList = new ArrayList<String>();
        for (String value : values) {
            valueList.add(value.trim());
        }
        properties.put(name.trim(), valueList);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean includesValues(String name, Collection<String> values) {
        Collection<String> propValues = properties.get(name);

        if (propValues == null) {
            return false;
        }

        return propValues.containsAll(values);
    }
}
