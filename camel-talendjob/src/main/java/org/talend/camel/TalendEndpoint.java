/*
 * #%L
 * Talend ESB :: Camel Talend Job Component
 * %%
 * Copyright (C) 2011 - 2014 Talend Inc.
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
    private String context;
    private Map<String, String> endpointProperties;

    private boolean propagateHeader = true;

    public TalendEndpoint(String uri, String clazz, TalendComponent component) {
        super(uri, component);
        this.setClazz(clazz);
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

    public final void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getContext() {
        return context;
    }

    public TalendJob getJobInstance() throws Exception {
        final TalendJob jobInstance;
        final JobResolver jobResolver = JobResolverHolder.getJobResolver();
        TalendESBJobFactory talendESBJobFactory = null;
        if (null != jobResolver) {
            talendESBJobFactory = jobResolver.getJobService(clazz);
        }
        if (null != talendESBJobFactory) {
            jobInstance = talendESBJobFactory.newTalendESBJob();
        } else {
            Class<?> jobType = this.getCamelContext().getClassResolver().resolveMandatoryClass(clazz);
            jobInstance = TalendJob.class.cast(getCamelContext().getInjector().newInstance(jobType));
        }
        return jobInstance;
    }

    public void setPropagateHeader(boolean propagateHeader) {
        this.propagateHeader = propagateHeader;
    }

    public boolean isPropagateHeader() {
        return propagateHeader;
    }

    public Map<String, String> getEndpointProperties() {
        return endpointProperties;
    }

    public void setEndpointProperties(Map<String, String> endpointProperties) {
        this.endpointProperties = endpointProperties;
    }

}
