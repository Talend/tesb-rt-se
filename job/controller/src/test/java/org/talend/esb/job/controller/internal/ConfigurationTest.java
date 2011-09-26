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

    @Test
    public void nullPropertiesProvidesEmptyArgumentList() throws Exception {

        Configuration configuration =  new Configuration(null);
        
        String[] args = configuration.getArguments();
        assertArrayEquals(new String[0], args);
    }

    @Test
    public void nonStringPropertyValueResultsInConfigurationException() {
        Dictionary<String, Object> properties = new Hashtable<String, Object>();
        properties.put("key1", 1);

        try {
            new Configuration(properties);
            fail("A ConfigurationException should have been thrown.");
        } catch(ConfigurationException e) {}
    }

    @Test
    public void configurationPropertysResultsInContextArgument() throws Exception{
        Dictionary<String, String> properties = new Hashtable<String,String>();
        properties.put("context", "contextValue");

        Configuration configuration =  new Configuration(properties);
        
        String[] args = configuration.getArguments();
        assertArrayEquals(new String[]{"--context=contextValue"}, args);
    }

    @Test
    public void NonSpecialPropertyResultsInContextParamArgument() throws Exception{
        Dictionary<String, String> properties = new Hashtable<String,String>();
        properties.put("key1", "value1");
        properties.put("key2", "value2");

        Configuration configuration =  new Configuration(properties);
        
        String[] args = configuration.getArguments();
        String[] expectedArgs = new String[]{"--context_param" + "key1" + "=" + "value1", "--context_param" + "key2" + "=" + "value2"};
        assertThat(args, arrayContainingInAnyOrder(expectedArgs));
    }
}
