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

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.talend.esb.servicelocator.TestValues.NAME_1;
import static org.talend.esb.servicelocator.TestValues.NAME_1_NOT_TRIMMED;
import static org.talend.esb.servicelocator.TestValues.NAME_2;
import static org.talend.esb.servicelocator.TestValues.NAME_3;
import static org.talend.esb.servicelocator.TestValues.VALUE_1;
import static org.talend.esb.servicelocator.TestValues.VALUE_1_NOT_TRIMMED;
import static org.talend.esb.servicelocator.TestValues.VALUE_2;
import static org.talend.esb.servicelocator.TestValues.VALUE_2_NOT_TRIMMED;
import static org.talend.esb.servicelocator.TestValues.VALUE_3;

import java.util.Arrays;
import java.util.Collection;

//import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(value=Parameterized.class)
public class SLPropertiesImplTest {

    private SLPropertiesImpl properties;;

    @Parameters
    public static Collection<String[]> getTestProperties() {
        return Arrays.asList(new String[][]{
            {NAME_1, NAME_2, VALUE_1, VALUE_2, VALUE_3},
            {NAME_1_NOT_TRIMMED, NAME_2, VALUE_1_NOT_TRIMMED, VALUE_2_NOT_TRIMMED, VALUE_3}
        });
    }

    public SLPropertiesImplTest(String name1, String name2, String val1, String val2, String val3) {
        properties = new SLPropertiesImpl();

        properties.addProperty(name1, val1, val2);
        properties.addProperty(name2, val2, val3);
    }

    @Test
    public void propertiesOfEmpty() {
        properties = new SLPropertiesImpl();

        assertFalse(properties.hasProperty(NAME_1));
        assertNull(properties.getValues(NAME_1));
    }

    @Test
    public void hasProperty() {
        assertTrue(properties.hasProperty(NAME_1));
        assertTrue(properties.hasProperty(NAME_2));
    }

    @Test
    public void getValues() {
        assertThat(properties.getValues(NAME_2), containsInAnyOrder(VALUE_2, VALUE_3));
        assertThat(properties.getValues(NAME_1), containsInAnyOrder(VALUE_1, VALUE_2));
    }

    @Test
    public void getSingleValueAsMultiValue() {
        assertThat(properties.getValues(NAME_1), hasItem(VALUE_1));
    }

    @Test
    public void includesValues() {
        assertTrue(properties.includesValues(NAME_1, VALUE_1));
        assertTrue(properties.includesValues(NAME_1, VALUE_2));
        assertTrue(properties.includesValues(NAME_2, VALUE_3, VALUE_2));

        assertFalse(properties.includesValues(NAME_1, VALUE_3));
        assertFalse(properties.includesValues(NAME_2, VALUE_1, VALUE_2));
        assertFalse(properties.includesValues(NAME_3, VALUE_2));
    }

    @Test
    public void valuesAddedAreTrimmed() {
        properties = new SLPropertiesImpl();

        properties.addProperty(NAME_1, VALUE_1_NOT_TRIMMED, VALUE_2);
        properties.addProperty(NAME_2, VALUE_2_NOT_TRIMMED, VALUE_3);


    }

    @Test
    public void getPropertyNames() {
        assertThat(properties.getPropertyNames(), containsInAnyOrder(NAME_1, NAME_2));
    }
}
