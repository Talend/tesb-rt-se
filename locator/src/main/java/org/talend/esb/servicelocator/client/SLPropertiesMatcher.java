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

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SLPropertiesMatcher {

    public static final SLPropertiesMatcher ALL_MATCHER = new SLPropertiesMatcher();

    private List<Map.Entry<String, String>> matchers = new ArrayList<Map.Entry<String, String>>();

    public void addAssertion(String name, String value) {
        matchers.add(new AbstractMap.SimpleEntry<String, String>(name, value));
    }

    public boolean isMatching(SLProperties properties) {
        for (Map.Entry<String, String> matcher : matchers) {
            if (!properties.includesValues(matcher.getKey(), matcher.getValue())) {
                return false;
            }
        }
        return true;
    }

}
