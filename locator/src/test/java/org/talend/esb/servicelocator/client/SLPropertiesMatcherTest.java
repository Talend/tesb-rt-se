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

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.talend.esb.servicelocator.TestValues.NAME_1;
import static org.talend.esb.servicelocator.TestValues.NAME_2;
import static org.talend.esb.servicelocator.TestValues.NAME_3;
import static org.talend.esb.servicelocator.TestValues.VALUE_1;
import static org.talend.esb.servicelocator.TestValues.VALUE_2;
import static org.talend.esb.servicelocator.TestValues.VALUE_3;

public class SLPropertiesMatcherTest {

    private SLPropertiesImpl properties;

    private SLPropertiesMatcher matcher;

    @Before
    public void setUp() {
        properties = new SLPropertiesImpl();
        properties.addProperty(NAME_1, VALUE_1, VALUE_2);
        properties.addProperty(NAME_2, VALUE_3);
        
        matcher = new SLPropertiesMatcher();
    }
    
    @Test
    public void emptyMatcher() {
        assertTrue(matcher.isMatching(properties));
    }

    @Test
    public void addAssertion() {
        matcher.addAssertion(NAME_1, VALUE_1);
        assertTrue(matcher.isMatching(properties));
    }

    @Test
    public void addAssertionKeyNotMatching() {
        SLPropertiesMatcher matcher = new SLPropertiesMatcher();
        matcher.addAssertion(NAME_3, VALUE_1);
        assertFalse(matcher.isMatching(properties));
    }

    @Test
    public void addAssertionKeyMatchingValueNotMatching() {
        matcher.addAssertion(NAME_1, VALUE_3);
        assertFalse(matcher.isMatching(properties));
    }

    @Test
    public void add2AssertionsFirstNotMatching() {
        matcher.addAssertion(NAME_1, VALUE_3);
        matcher.addAssertion(NAME_2, VALUE_3);
        assertFalse(matcher.isMatching(properties));
    }

    @Test
    public void add2AssertionsSameKeyOneNotMatching() {
        matcher.addAssertion(NAME_1, VALUE_3);
        matcher.addAssertion(NAME_1, VALUE_1);
        assertFalse(matcher.isMatching(properties));
    }
}
