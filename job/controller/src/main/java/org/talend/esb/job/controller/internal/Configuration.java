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

    public static final String CONTEXT_PROP = "context";

    public static final String CONTEXT_OPT = "--context=";

    public static final String CONTEXT_PARAM_OPT = "--context_param=";

    public static final String[] EMPTY_ARGUMENTS = new String[0];

    public static final long DEFAULT_TIMEOUT = 3000;

    public static final String TIME_OUT_PROPERTY = "org.talend.esb.job.controller.configuration.timeout";

    private static final Logger LOG = Logger.getLogger(Configuration.class.getName());

    private static final String[] DEFAULT_FILTER = new String[0];

    private long timeout;

    private List<String> argumentList = new ArrayList<String>();

    private final CountDownLatch configAvailable = new CountDownLatch(1);

    private final String[] filter;

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
     * @param filter  list of property keys that are filtered out
     */
    public Configuration(String[] filter) {
        this.filter =  filter;
        initTimeout();
    }

    /**
     * A <code>Configuration</code> object backed by the given properties from ConfigurationAdmin.
     *
     * @param properties the properties from ConfigurationAdmin, may be <code>null</code>.
     * @param filter  list of property keys that are filtered out
     * @throws ConfigurationException thrown if the property values are not of type String
     */
    public Configuration(Dictionary<?, ?> properties, String[] filter) throws ConfigurationException {
        this.filter = filter;
        setProperties(properties);
        initTimeout();
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
            for (Enumeration<?> keysEnum = properties.keys(); keysEnum.hasMoreElements(); ) {
                String key = (String) keysEnum.nextElement();
                Object val = properties.get(key);
                if (!(val instanceof String)) {
                    throw new ConfigurationException(key, "Value is not of type String.");
                }
                addToArguments(key, (String) val, newArgumentList);
            }
        }
        argumentList = newArgumentList;
        configAvailable.countDown();
    }

    /**
     * Set the time to wait in the {@link #awaitArguments()} method for the properties to be set.
     *
     * @param timeout time to wait in milliseconds.  
     */
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    private void addToArguments(String key, String value,  List<String> argList) {
        if (key.equals(CONTEXT_PROP)) {
            argList.add(CONTEXT_OPT + value);
            LOG.fine("Context " + value + " added to the argument list.");
        } else {
            if (!isInFilter(key)) {
                argList.add(CONTEXT_PARAM_OPT + key + "=" + value);
                LOG.fine("Parameter " + key + " with value " + value + " added to the argument list.");
            } else {
                LOG.fine("Propertey " + key + " filltered out.");
            }
        }
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
        String[] args = null;
        if (configAvailable.await(timeout, TimeUnit.MILLISECONDS)) {
            List<String> currentArgumentList = argumentList;
            args = currentArgumentList.toArray(new String[currentArgumentList.size()]);
        } else {
            args = EMPTY_ARGUMENTS;
            LOG.warning("ConfigAdmin did not pass any properties yet, returning an empty argumentlist.");
        }
        return args;
    }

    private void initTimeout() {
        timeout = Long.getLong(TIME_OUT_PROPERTY, DEFAULT_TIMEOUT);
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
