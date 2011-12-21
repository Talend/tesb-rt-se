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
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.osgi.service.cm.ConfigurationException;

/**
 * A <code>Configuration</code> represents parameter settings for a Talend job that is provided by
 * the OSGi Configuration Admin Service. The parameters may be retrieved as an array of option
 * arguments the way they are expected by the Talend job.
 */
public final class Configuration {

    private static final Logger LOG = Logger.getLogger(Configuration.class.getName());

    private static final String TIME_OUT_PROPERTY = "org.talend.esb.job.controller.configuration.timeout";

    private static final String CONTEXT_PROP = "context";

    private static final String CONTEXT_OPT = "--context=";

    private static final String CONTEXT_PARAM_OPT = "--context_param=";

    private static final String[] EMPTY_ARGUMENTS = new String[0];

    private static final long DEFAULT_TIMEOUT = 3000;

    private static final String[] DEFAULT_FILTER = new String[0];

    private long timeout;

    private List<String> argumentList;

    private final CountDownLatch configAvailable = new CountDownLatch(1);

    private final List<String> filter;

    /**
     * A <code>Configuration</code> object with no properties set.
     */
    public Configuration() {
        this(DEFAULT_FILTER);
    }

    /**
     * A <code>Configuration</code> object backed by the given properties from ConfigurationAdmin.
     *
     * @param properties the properties from ConfigurationAdmin, may be <code>null</code>.
     * @throws ConfigurationException thrown if the property values are not of type String
     */
    public Configuration(Dictionary<?, ?> properties) throws ConfigurationException {
        this(properties, DEFAULT_FILTER);
    }

    /**
     * A <code>Configuration</code> object backed by the given properties from ConfigurationAdmin.
     *
     * @param properties the properties from ConfigurationAdmin, may be <code>null</code>.
     * @param filter  list of property keys that are filtered out
     * @throws ConfigurationException thrown if the property values are not of type String
     */
    public Configuration(Dictionary<?, ?> properties, String[] filter) throws ConfigurationException {
        this(filter);
        setProperties(properties);
    }

    /**
     * A <code>Configuration</code> object backed by the given properties from ConfigurationAdmin.
     *
     * @param filter  list of property keys that are filtered out
     */
    public Configuration(String[] filter) {
        this.filter = Arrays.asList(filter);
        initTimeout();
    }

    /**
     * Set the time to wait in the {@link #awaitArguments()} method for the properties to be set.
     *
     * @param timeout time to wait in milliseconds.  
     */
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    /**
     * Back this <code>Configuration</code>  by the given properties from ConfigurationAdmin.
     *
     * @param properties the properties from ConfigurationAdmin, may be <code>null</code>.
     * @throws ConfigurationException thrown if the property values are not of type String
     */
    public void setProperties(Dictionary<?, ?> properties) throws ConfigurationException {
        List<String> newArgumentList = new ArrayList<String>();

        if (properties != null) {
            for (Enumeration<?> keysEnum = properties.keys(); keysEnum.hasMoreElements();) {
                String key = (String) keysEnum.nextElement();
                Object val = properties.get(key);
                if (val instanceof String) {
                    String strval = convertArgument(key, (String)val);
                    if (strval != null) {
                        newArgumentList.add(strval);
                    }
                } else {
                    throw new ConfigurationException(key, "Value is not of type String.");
                }
            }
        }
        argumentList = newArgumentList;
        configAvailable.countDown();
    }

    /**
     * Get the configuration properties as argument list as expected by the Talend job. If the properties
     * were not yet set, wait the time specified by the {@link #setTimeout(long) timeout property} and return
     * empty argument list if properties still not specified. If <code>timeout <= 0</code> the method
     * immediately returns.
     *
     * @return the argument list, never <code>null</code>
     */
    public String[] awaitArguments() throws InterruptedException {
        if (configAvailable.await(timeout, TimeUnit.MILLISECONDS)) {
            return argumentList.toArray(new String[argumentList.size()]);
        } else {
            LOG.warning("ConfigAdmin did not pass any properties yet, returning an empty argument list.");
            return EMPTY_ARGUMENTS;
        }
    }

    private void initTimeout() {
        timeout = Long.getLong(TIME_OUT_PROPERTY, DEFAULT_TIMEOUT);
    }

    private String convertArgument(String key, String value) {
        if (key.equals(CONTEXT_PROP)) {
            LOG.fine("Context " + value + " added to the argument list.");
            return CONTEXT_OPT + value;
        } else {
            if (!filter.contains(key)) {
                LOG.fine("Parameter " + key + " with value " + value + " added to the argument list.");
                return CONTEXT_PARAM_OPT + key + "=" + value;
            } else {
                LOG.fine("Propertey " + key + " filltered out.");
                return null;
            }
        }
    }

}
