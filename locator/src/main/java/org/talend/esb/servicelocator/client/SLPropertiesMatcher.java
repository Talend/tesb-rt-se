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

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Checks whether a  {@link SLProperties} has the specified values with specific values.
 *
 */
public class SLPropertiesMatcher {

    public static final SLPropertiesMatcher ALL_MATCHER = new SLPropertiesMatcher();

    private List<Map.Entry<String, String>> matchers = new ArrayList<Map.Entry<String, String>>();

    /**
     * Add an assertion to this matcher where a {@link SLProperties} must have a property with the given name
     * which at least contains the given value. Name and value are trimmed before matching is done.
     *
     * @param name the name of the property which must be present, must not be <code>null</code>
     * @param value the value the property must at least contain, must not be <code>null</code>
     */
    public void addAssertion(String name, String value) {
        matchers.add(new AbstractMap.SimpleEntry<String, String>(name.trim(), value.trim()));
    }

    /**
     * Checks if the given {@link SLProperties} fulfills all assertions specified for this matcher.
     *
     * @param properties the properties to be checked
     * @return <code>true</code> iff if all assertions are fulfilled
     */
    public boolean isMatching(SLProperties properties) {
        for (Map.Entry<String, String> matcher : matchers) {
            if (!properties.includesValues(matcher.getKey(), matcher.getValue())) {
                return false;
            }
        }
        return true;
    }

    public String getAssertionsAsString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (Map.Entry<String, String> matcher : matchers) {
            sb.append(matcher.getKey());
            sb.append("=");
            sb.append(matcher.getValue());
            sb.append(",");
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
    	sb.append("matcher = " + this.hashCode());
        for (Map.Entry<String, String> matcher : matchers) {
        	sb.append(matcher.getKey());
        	sb.append(" -> ");
        	sb.append(matcher.getValue());
        	sb.append("\n");
        }
        return sb.toString();
    }

}
