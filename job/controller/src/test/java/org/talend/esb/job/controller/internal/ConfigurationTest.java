/*
 * #%L
 * Talend :: ESB :: Job :: Controller
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
package org.talend.esb.job.controller.internal;

import java.util.Dictionary;
import java.util.Hashtable;

import org.junit.Test;
import org.osgi.service.cm.ConfigurationException;

import static org.hamcrest.collection.IsArrayContainingInAnyOrder.arrayContainingInAnyOrder;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class ConfigurationTest {

    public static final String KEY_1 = "key1";

    public static final String KEY_2 = "key2";
    
    public static final String VALUE_1 = "value";

    public static final String VALUE_2 = "value2";

    public static final String CONTEXT_KEY = "context";

    public static final String CONTEXT_VALLUE = "contextValue";

    @Test
    public void nullPropertiesProvidesEmptyArgumentList() throws Exception {

        Configuration configuration =  new Configuration(null);
        
        String[] args = configuration.getArguments();
        assertArrayEquals(new String[0], args);
    }

    @Test
    public void nonStringPropertyValueResultsInConfigurationException() {
        Dictionary<String, Object> properties = new Hashtable<String, Object>();
        properties.put(KEY_1, 1);

        try {
            new Configuration(properties);
            fail("A ConfigurationException should have been thrown.");
        } catch (ConfigurationException e) { }
    }

    @Test
    public void configurationPropertysResultsInContextArgument() throws Exception {
        Dictionary<String, String> properties = new Hashtable<String, String>();
        properties.put(CONTEXT_KEY, CONTEXT_VALLUE);

        Configuration configuration =  new Configuration(properties);
        
        String[] args = configuration.getArguments();
        assertArrayEquals(new String[]{"--context=" + CONTEXT_VALLUE}, args);
    }

    @Test
    public void nonSpecialPropertyResultsInContextParamArgument() throws Exception {
        Dictionary<String, String> properties = new Hashtable<String, String>();
        properties.put(KEY_1, VALUE_1);
        properties.put(KEY_2, VALUE_2);

        Configuration configuration =  new Configuration(properties);
        
        String[] args = configuration.getArguments();
        String[] expectedArgs = new String[]{"--context_param=" + KEY_1 + "=" + VALUE_1,
            "--context_param=" + KEY_2 + "=" + VALUE_2};
        assertThat(args, arrayContainingInAnyOrder(expectedArgs));
    }

    @Test
    public void propertyInFilterNotInArgumentList() throws Exception {
        String[] filter = new String[] {KEY_1};
        Dictionary<String, String> properties = new Hashtable<String, String>();
        properties.put(KEY_1, "value1");
        properties.put(KEY_2, "value2");

        Configuration configuration =  new Configuration(properties, filter);
        
        String[] args = configuration.getArguments();
        String[] expectedArgs = new String[]{"--context_param=" + KEY_2 + "=" + VALUE_2};
        assertThat(args, arrayContainingInAnyOrder(expectedArgs));
    }
}
