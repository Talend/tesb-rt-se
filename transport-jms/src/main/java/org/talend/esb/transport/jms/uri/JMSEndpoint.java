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

import java.util.HashMap;
import java.util.Map;

import javax.jms.Message;

import org.apache.cxf.transport.jms.spec.JMSSpecConstants;
import org.apache.cxf.transport.jms.uri.JMSEndpointType;
import org.apache.cxf.transport.jms.uri.JMSURIConstants;

/**
 * 
 */
public class JMSEndpoint extends JMSEndpointType {
    Map<String, String> jndiParameters = new HashMap<String, String>();
    Map<String, String> parameters = new HashMap<String, String>();
    
    
    /**
     * URI parameters
     * Will be filled from URI query parameters with matching names
     */
    private String conduitIdSelectorPrefix;
    private String durableSubscriptionName;
    private String jndiTransactionManagerName;
    private long receiveTimeout = 60000L;
    private boolean sessionTransacted;
    private String targetService;
    private boolean useConduitIdSelector = true;


    /**
     * @param uri
     * @param subject
     */
    public JMSEndpoint(String endpointUri, String jmsVariant, String destinationName) {
        this.endpointUri = endpointUri;
        this.jmsVariant = jmsVariant;
        this.destinationName = destinationName;
        
    }
    public JMSEndpoint() {
        jmsVariant = JMSURIConstants.QUEUE;
    }

    public String getRequestURI() {
        StringBuilder requestUri = new StringBuilder("jms:");
        if (jmsVariant == JMSURIConstants.JNDI_TOPIC) {
            requestUri.append("jndi");
        } else {
            requestUri.append(jmsVariant);
        }
        requestUri.append(":" + destinationName);
        boolean first = true;
        for (String key : parameters.keySet()) {
            // now we just skip the MESSAGE_TYPE_PARAMETER_NAME 
            // and TARGETSERVICE_PARAMETER_NAME
            if (JMSSpecConstants.TARGETSERVICE_PARAMETER_NAME.equals(key) 
                || JMSURIConstants.MESSAGE_TYPE_PARAMETER_NAME.equals(key)) {
                continue;
            }
            String value = parameters.get(key);
            if (first) {
                requestUri.append("?" + key + "=" + value);
                first = false;
            } else {
                requestUri.append("&" + key + "=" + value);
            }
        }
        return requestUri.toString();
    }

    /**
     * @param key
     * @param value
     */
    public void putJndiParameter(String key, String value) {
        jndiParameters.put(key, value);
    }

    public void putParameter(String key, String value) {
        parameters.put(key, value);
    }

    /**
     * @param targetserviceParameterName
     * @return
     */
    public String getParameter(String key) {
        return parameters.get(key);
    }

    public Map<String, String> getJndiParameters() {
        return jndiParameters;
    }

    /**
     * @return
     */
    public Map<String, String> getParameters() {
        return parameters;
    }
    
    public String getDestinationName() {
        return destinationName;
    }
    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }
    public boolean isSetDeliveryMode() {
        return deliveryMode != null;
    }
    
    public long getTimeToLive() {
        return timeToLive;
    }
    public void setTimeToLive(long timeToLive) {
        this.timeToLive = timeToLive;
    }
    public void setTimeToLive(String timeToLive) {
        this.timeToLive = Long.valueOf(timeToLive);
    }
    public boolean isSetPriority() {
        return priority != null;
    }
    public int getPriority() {
        return priority == null ?  Message.DEFAULT_PRIORITY : priority;
    }
    public void setPriority(int priority) {
        this.priority = priority;
    }
    public void setPriority(String priority) {
        this.priority = Integer.valueOf(priority);
    }
    public String getReplyToName() {
        return replyToName;
    }
    public void setReplyToName(String replyToName) {
        this.replyToName = replyToName;
    }
    public String getTopicReplyToName() {
        return topicReplyToName;
    }
    public void setTopicReplyToName(String topicReplyToName) {
        this.topicReplyToName = topicReplyToName;
    }
    public String getJndiConnectionFactoryName() {
        return jndiConnectionFactoryName;
    }
    public void setJndiConnectionFactoryName(String jndiConnectionFactoryName) {
        this.jndiConnectionFactoryName = jndiConnectionFactoryName;
    }
    public String getJndiInitialContextFactory() {
        return jndiInitialContextFactory;
    }
    public void setJndiInitialContextFactory(String jndiInitialContextFactory) {
        this.jndiInitialContextFactory = jndiInitialContextFactory;
    }
    public String getJndiURL() {
        return jndiURL;
    }
    public void setJndiURL(String jndiURL) {
        this.jndiURL = jndiURL;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getDurableSubscriptionName() {
        return durableSubscriptionName;
    }

    public void setDurableSubscriptionName(String durableSubscriptionName) {
        this.durableSubscriptionName = durableSubscriptionName;
    }

    public long getReceiveTimeout() {
        return receiveTimeout;
    }

    public void setReceiveTimeout(long receiveTimeout) {
        this.receiveTimeout = receiveTimeout;
    }
    
    public void setReceiveTimeout(String receiveTimeout) {
        this.receiveTimeout = Long.valueOf(receiveTimeout);
    }
    public String getTargetService() {
        return targetService;
    }
    public void setTargetService(String targetService) {
        this.targetService = targetService;
    }
    public boolean isSessionTransacted() {
        return sessionTransacted;
    }
    public void setSessionTransacted(boolean sessionTransacted) {
        this.sessionTransacted = sessionTransacted;
    }
    public void setSessionTransacted(String sessionTransacted) {
        this.sessionTransacted = Boolean.valueOf(sessionTransacted);
    }
    public String getConduitIdSelectorPrefix() {
        return conduitIdSelectorPrefix;
    }
    public void setConduitIdSelectorPrefix(String conduitIdSelectorPrefix) {
        this.conduitIdSelectorPrefix = conduitIdSelectorPrefix;
    }
    public boolean isUseConduitIdSelector() {
        return useConduitIdSelector;
    }
    
    public void setUseConduitIdSelector(String useConduitIdSelectorSt) {
        this.useConduitIdSelector = Boolean.valueOf(useConduitIdSelectorSt);
    }
    
    public void setUseConduitIdSelector(boolean useConduitIdSelector) {
        this.useConduitIdSelector = useConduitIdSelector;
    }
    
    public String getJndiTransactionManagerName() {
        return jndiTransactionManagerName;
    }

    public void setJndiTransactionManagerName(String jndiTransactionManagerName) {
        this.jndiTransactionManagerName = jndiTransactionManagerName;
    }   
    
}
