/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.talend.esb.transport.jms.uri;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.transport.jms.uri.JMSURIConstants;
import org.apache.cxf.transport.jms.uri.ResolveEndpointFailedException;
import org.apache.cxf.transport.jms.uri.URISupport;
import org.apache.cxf.transport.jms.uri.UnsafeUriCharactersEncoder;

/**
 * 
 */
public final class JMSEndpointParser {
    private static final Logger LOG = LogUtils.getL7dLogger(JMSEndpointParser.class);

    public static final String JNDI_TOPIC_PREFIX = "jndi-topic:";
    public static final String JNDI_PARAMETER_NAME_PREFIX = "jndi-";

    private JMSEndpointParser() {
    }

    public static JMSEndpoint createEndpoint(String uri) throws Exception {
        // encode URI string to the unsafe URI characters
        URI u = new URI(UnsafeUriCharactersEncoder.encode(uri));
        String path = u.getSchemeSpecificPart();

        // lets trim off any query arguments
        if (path.startsWith("//")) {
            path = path.substring(2);
        }
        int idx = path.indexOf('?');
        if (idx > 0) {
            path = path.substring(0, idx);
        }
        Map<String, String> parameters = URISupport.parseParameters(u);

        validateURI(uri, path, parameters);

        LOG.log(Level.FINE, "Creating endpoint uri=[" + uri + "], path=[" + path
                            + "], parameters=[" + parameters + "]");
        JMSEndpoint endpoint = createEndpoint(uri, path);
        if (endpoint == null) {
            return null;
        }

        if (parameters != null) {
            configureProperties(endpoint, parameters);
        }

        return endpoint;
    }

    /**
     * @param endpoint
     * @param parameters
     */
    private static void configureProperties(JMSEndpoint endpoint, Map<String, String> params) {
        for (String key : params.keySet()) {
            Object value = params.get(key);
            if (value == null || value.equals("")) {
                continue;
            }
            if (trySetProperty(endpoint, key, value)) {
                continue;
            }
            if (!(value instanceof String)) {
                continue;
            }
            String valueSt = (String)value;
            if (key.startsWith(JNDI_PARAMETER_NAME_PREFIX)) {
                key = key.substring(5);
                endpoint.putJndiParameter(key, valueSt);
            } else {
            	endpoint.putParameter(key, valueSt);
            }
        }
        
        if (endpoint.getReplyToName() != null && endpoint.getTopicReplyToName() != null) {
            throw new IllegalArgumentException(
                "The replyToName and topicReplyToName should not be defined at the same time.");
        }
    }
    
    private static boolean trySetProperty(JMSEndpoint endpoint, String name, Object value) {
        try {
            Method method = endpoint.getClass().getMethod(getPropSetterName(name), value.getClass());
            method.invoke(endpoint, value);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error setting property " + name + ":" + e.getMessage(), e);
        }
    }
    
    private static String getPropSetterName(String name) {
        String first = name.substring(0, 1);
        String rest = name.substring(1);
        return "set" + first.toUpperCase() + rest;
    }

    /**
     * Strategy for validation of the uri when creating the endpoint.
     * 
     * @param uri the uri - the uri the end user provided untouched
     * @param path the path - part after the scheme
     * @param parameters the parameters, an empty map if no parameters given
     * @throws ResolveEndpointFailedException should be thrown if the URI validation failed
     */
    protected static void validateURI(String uri, String path, Map<String, String> parameters)
        throws ResolveEndpointFailedException {
        // check for uri containing & but no ? marker
        if (uri.contains("&") && !uri.contains("?")) {
            throw new ResolveEndpointFailedException(
                                                     uri,
                                                     "Invalid uri syntax: no ? marker however the uri "
                                                         + "has & parameter separators. "
                                                         + "Check the uri if its missing a ? marker.");

        }

        // check for uri containing double && markers
        if (uri.contains("&&")) {
            throw new ResolveEndpointFailedException(uri,
                                                     "Invalid uri syntax: Double && marker found. "
                                                         + "Check the uri and remove the "
                                                         + "duplicate & marker.");
        }
    }

    /**
     * A factory method allowing derived components to create a new endpoint from the given URI, remaining
     * path and optional parameters
     * 
     * @param uri the full URI of the endpoint
     * @param remaining the remaining part of the URI without the query parameters or component prefix
     * @param parameters the optional parameters passed in
     * @return a newly created endpoint or null if the endpoint cannot be created based on the inputs
     */
    protected static JMSEndpoint createEndpoint(String uri, String remaining) throws Exception {
        boolean isQueue = false;
        boolean isTopic = false;
        boolean isJndi = false;
        boolean isJndiTopic = false;
        if (remaining.startsWith(JMSURIConstants.QUEUE_PREFIX)) {
            remaining = removeStartingCharacters(remaining.substring(JMSURIConstants.QUEUE_PREFIX
                .length()), '/');
            isQueue = true;
        } else if (remaining.startsWith(JMSURIConstants.TOPIC_PREFIX)) {
            remaining = removeStartingCharacters(remaining.substring(JMSURIConstants.TOPIC_PREFIX
                .length()), '/');
            isTopic = true;
        } else if (remaining.startsWith(JMSURIConstants.JNDI_PREFIX)) {
            remaining = removeStartingCharacters(remaining.substring(JMSURIConstants.JNDI_PREFIX
                .length()), '/');
            isJndi = true;
        } else if (remaining.startsWith(JNDI_TOPIC_PREFIX)) {
            remaining = removeStartingCharacters(remaining.substring(JNDI_TOPIC_PREFIX
                .length()), '/');
            isJndiTopic = true;
        } else {
            throw new Exception("Unknow JMS Variant");
        }

        final String subject = convertPathToActualDestination(remaining);

        // lets make sure we copy the configuration as each endpoint can
        // customize its own version
        // JMSConfiguration newConfiguration = getConfiguration().copy();
        JMSEndpoint endpoint = null;
        if (isQueue) {
            endpoint = new JMSQueueEndpoint(uri, subject);
        } else if (isTopic) {
            endpoint = new JMSTopicEndpoint(uri, subject);
        } else if (isJndi) {
            endpoint = new JMSJNDIEndpoint(uri, subject);
        } else if (isJndiTopic) {
            endpoint = new JMSJNDITopicEndpoint(uri, subject);
        }
        return endpoint;
    }

    /**
     * A strategy method allowing the URI destination to be translated into the actual JMS destination name
     * (say by looking up in JNDI or something)
     */
    protected static String convertPathToActualDestination(String path) {
        return path;
    }

    public static JMSURIConstants getConfiguration() {
        return null;
    }

    // Some helper methods
    // -------------------------------------------------------------------------
    /**
     * Removes any starting characters on the given text which match the given character
     * 
     * @param text the string
     * @param ch the initial characters to remove
     * @return either the original string or the new substring
     */
    public static String removeStartingCharacters(String text, char ch) {
        int idx = 0;
        while (text.charAt(idx) == ch) {
            idx++;
        }
        if (idx > 0) {
            return text.substring(idx);
        }
        return text;
    }
}
