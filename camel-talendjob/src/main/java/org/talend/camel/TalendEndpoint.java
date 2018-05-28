/*
 * #%L
 * Talend ESB :: Camel Talend Job Component
 * %%
 * Copyright (C) 2011 - 2017 Talend Inc.
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

package org.talend.camel;

import java.util.Map;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.impl.DefaultEndpoint;

import routines.system.api.TalendESBJobFactory;
import routines.system.api.TalendJob;

/**
 * <p>
 * Represents a Talend endpoint.
 * </p>
 */
public class TalendEndpoint extends DefaultEndpoint {

    private String clazz;
    private String owner;
    private String context;
    private Map<String, String> endpointProperties;
    private String componentId = null;
    private boolean propagateHeader = true;
    private boolean stickyJob = false;

    public TalendEndpoint(String uri, String clazz, TalendComponent component) {
        super(uri, component);
        setOwnerAndClazz(clazz);
    }

    public Producer createProducer() throws Exception {
        return new TalendProducer(this);
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        throw new RuntimeCamelException("No support for exposing Camel as Talend job yet");
    }

    public boolean isSingleton() {
        return true;
    }

    public void setClazz(String clazz) {
        setOwnerAndClazz(clazz);
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    public boolean isPropagateHeader() {
        return propagateHeader;
    }

    public void setPropagateHeader(boolean propagateHeader) {
        this.propagateHeader = propagateHeader;
    }

    public boolean isStickyJob() {
        return stickyJob;
    }

    public void setStickyJob(boolean stickyJob) {
        this.stickyJob = stickyJob;
    }

    public TalendJob getJobInstance(boolean isMandatory) throws Exception {
        final JobResolver jobResolver = JobResolverHolder.getJobResolver();
        if (null != jobResolver) {
            TalendESBJobFactory talendESBJobFactory = jobResolver.getJobService(owner, clazz, isMandatory);
            if (null != talendESBJobFactory) {
                return talendESBJobFactory.newTalendESBJob();
            }
            if (!isMandatory) {
                return null;
            }
        }
        Class<?> jobType = this.getCamelContext().getClassResolver().resolveMandatoryClass(clazz);
        return (TalendJob) getCamelContext().getInjector().newInstance(jobType);
    }

    public Map<String, String> getEndpointProperties() {
        return endpointProperties;
    }

    public void setEndpointProperties(Map<String, String> endpointProperties) {
        this.endpointProperties = endpointProperties;
    }

    private void setOwnerAndClazz(String rawClazz) {
        if (rawClazz == null) {
            owner = null;
            clazz = null;
            return;
        }
        int ndx = rawClazz.lastIndexOf('/');
        if (ndx < 0) {
            owner = null;
            clazz = rawClazz;
            return;
        }
        owner = rawClazz.substring(0, ndx);
        clazz = rawClazz.substring(ndx + 1);
    }
}
