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

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;

import org.osgi.service.cm.ConfigurationException;

/**
 * A <code>Configuration</code> represents parameter settings for a Talend job that is provided by
 * the OSGi Configuration Admin Service. The parameters may be retrieved as an array of option
 * arguments the way they are expected by the Talend job.
 */
public class Configuration {
    
    public static final Logger LOG =
        Logger.getLogger(Configuration.class.getName());

    public static final String CONTEXT_PROP = "context";

    public static final String CONTEXT_OPT = "--context=";

    public static final String CONTEXT_PARAM_OPT = "--context_param=";
    
    List<String> argumentList = new ArrayList<String>();
    
    private String[] filter;

    /**
     * A <code>Configuration</code> object backed by the given properties from ConfigurationAdmin.
     *
     * @param properties the properties from ConfigurationAdmin, nay be <code>null</code>.
     * @throws ConfigurationException thrown if the property values are not of type String
     */
    public Configuration(Dictionary<?, ?> properties) throws ConfigurationException {
        this(properties, new String[0]);
    }

    /**
     * A <code>Configuration</code> object backed by the given properties from ConfigurationAdmin.
     *
     * @param properties the properties from ConfigurationAdmin, nay be <code>null</code>.
     * @param filter  list of property keys that are filtered out
     * @throws ConfigurationException thrown if the property values are not of type String
     */
    public Configuration(Dictionary<?, ?> properties, String[] filter) throws ConfigurationException {
        this.filter = filter;

        if (properties != null) {
            Enumeration<?> keysEnum = properties.keys();
            while (keysEnum.hasMoreElements()) {
                String key = (String) keysEnum.nextElement();
                Object val = properties.get(key);
                if (!(val instanceof String)) {
                    throw new ConfigurationException(key, "Value is not of type String.");
                }
                addToArguments(key, (String) val);
            }
        }
    }
    
    private void addToArguments(String key, String value) {
        if (key.equals(CONTEXT_PROP)) {
            argumentList.add(CONTEXT_OPT + value);
            LOG.fine("Context " + value + " added to the argument list.");
        } else {
            if (!isInFilter(key)) {
                argumentList.add(CONTEXT_PARAM_OPT + key + "=" + value);
                LOG.fine("Parameter " + key + " with value " + value + " added to the argument list.");
            } else {
                LOG.fine("Propertey " + key + " filltered out.");                
            }
        }

    }
    /**
     * Get the configuration properties as argument list as expected by the Talend job.
     *
     * @return the argument list, never <code>null</code>
     */
    public String[] getArguments() {
        return argumentList.toArray(new String[argumentList.size()]);
    }
    
    private boolean isInFilter(String key) {
        for (String entry : filter) {
            if (entry.equals(key)) {
                return true;
            }
        }
        return false;
    }
}
