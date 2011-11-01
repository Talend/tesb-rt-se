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

import java.util.Hashtable;
import java.util.Map;
import java.util.Queue;

import javax.xml.namespace.QName;

import org.apache.cxf.Bus;
import org.apache.cxf.common.injection.NoJSR250Annotations;
import org.apache.neethi.Policy;
import org.talend.esb.job.controller.ESBEndpointConstants;
import org.talend.esb.job.controller.ESBEndpointConstants.EsbSecurity;
import org.talend.esb.job.controller.ESBEndpointConstants.OperationStyle;
import org.talend.esb.sam.agent.feature.EventFeature;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.servicelocator.cxf.LocatorFeature;

import routines.system.api.ESBConsumer;
import routines.system.api.ESBEndpointInfo;
import routines.system.api.ESBEndpointRegistry;

@NoJSR250Annotations(unlessNull = "bus") 
public class RuntimeESBEndpointRegistry implements ESBEndpointRegistry {

    private Bus bus;
    private Queue<Event> samQueue;
    private Policy policyToken;
    private Policy policySaml;
    private Hashtable<String, String> clientProperties;
    private Hashtable<String, String> stsProperties;
    private boolean logging;

    @javax.annotation.Resource
    public void setBus(Bus bus) {
        this.bus = bus;
    }

    public void setSamQueue(Queue<Event> samQueue) {
        this.samQueue = samQueue;
    }

    public void setPolicyToken(Policy policyToken) {
        this.policyToken = policyToken;
    }

    public void setPolicySaml(Policy policySaml) {
        this.policySaml = policySaml;
    }

    public void setClientProperties(Hashtable<String, String> clientProperties) {
        this.clientProperties = clientProperties;
    }

    public void setStsProperties(Hashtable<String, String> stsProperties) {
        this.stsProperties = stsProperties;
    }

    public void setLogging(boolean logging) {
        this.logging = logging;
    }

    @Override
    public ESBConsumer createConsumer(ESBEndpointInfo endpoint) {
        final Map<String, Object> props = endpoint.getEndpointProperties();

        final QName serviceName = QName.valueOf((String) props
                .get(ESBEndpointConstants.SERVICE_NAME));
        final QName portName = QName.valueOf((String) props
                .get(ESBEndpointConstants.PORT_NAME));
        final String operationName = (String) props
                .get(ESBEndpointConstants.DEFAULT_OPERATION_NAME);

        final String publishedEndpointUrl = (String) props
                .get(ESBEndpointConstants.PUBLISHED_ENDPOINT_URL);
        boolean useServiceLocator = ((Boolean) props
                .get(ESBEndpointConstants.USE_SERVICE_LOCATOR)).booleanValue();
        boolean useServiceActivityMonitor = ((Boolean) props
                .get(ESBEndpointConstants.USE_SERVICE_ACTIVITY_MONITOR))
                .booleanValue();

        LocatorFeature slFeature = null;
        if (useServiceLocator) {
            slFeature = new LocatorFeature();
            //pass SL custom properties to Consumer
            Object slProps = props.get(ESBEndpointConstants.REQUEST_SL_PROPS);
            if (slProps != null) {
                slFeature.setRequiredEndpointProperties((Map<String, String>)slProps);
            }
        }

        final EsbSecurity esbSecurity = EsbSecurity.fromString((String) props
                .get(ESBEndpointConstants.ESB_SECURITY));
        Policy policy = null;
        if (EsbSecurity.TOKEN == esbSecurity) {
            policy = policyToken;
        } else if (EsbSecurity.SAML == esbSecurity) {
            policy = policySaml;
        }

        final SecurityArguments securityArguments = new SecurityArguments(
                esbSecurity,
                policy,
                (String) props.get(ESBEndpointConstants.USERNAME),
                (String) props.get(ESBEndpointConstants.PASSWORD),
                clientProperties,
                stsProperties);
        return new RuntimeESBConsumer(
                serviceName, portName, operationName, publishedEndpointUrl,
                OperationStyle.isRequestResponse((String) props
                        .get(ESBEndpointConstants.COMMUNICATION_STYLE)),
                slFeature,
                useServiceActivityMonitor ? createEventFeature() : null,
                securityArguments,
                bus,
                logging);
    }

    private EventFeature createEventFeature() {
        EventFeature eventFeature = new EventFeature();
        eventFeature.setQueue(samQueue);
        return eventFeature;
    }

}
