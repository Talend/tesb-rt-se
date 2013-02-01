/*
 * #%L
 * Camel Talend Job Component
 * %%
 * Copyright (C) 2011 - 2012 Talend Inc.
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

import java.lang.reflect.Method;

import org.apache.camel.Consumer;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.impl.DefaultEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import routines.system.api.TalendESBJobFactory;
import routines.system.api.TalendJob;

/**
 * <p>
 * Represents a Talend endpoint.
 * </p>
 */
public class TalendEndpoint extends DefaultEndpoint {

    private static final transient Logger LOG = LoggerFactory.getLogger(TalendEndpoint.class);

    private String clazz;
    private String context;
    private TalendJob jobInstance;
    private Method setExchangeMethod;

    private boolean propagateHeader = false;

    public TalendEndpoint(String uri, String clazz, TalendComponent component) {
        super(uri, component);
        this.setClazz(clazz);
    }

    public TalendEndpoint(String endpointUri) {
        super(endpointUri);
    }

    public Producer createProducer() throws Exception {
        instantiateJob();
        return new TalendProducer(this);
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        throw new RuntimeCamelException("No support for exposing Camel as Talend job yet");
    }

    public boolean isSingleton() {
        return true;
    }

    protected void instantiateJob() throws ClassNotFoundException, SecurityException, NoSuchMethodException {
        if (jobInstance == null) {
            Class<?> jobType = this.getCamelContext().getClassResolver().resolveMandatoryClass(clazz);
            TalendESBJobFactory talendESBJobFactory = Activator.getJobService(TalendESBJobFactory.class, jobType.getSimpleName());
            if (null != talendESBJobFactory) {
                jobInstance = talendESBJobFactory.newTalendESBJob();
            } else {
                jobInstance = TalendJob.class.cast(getCamelContext().getInjector().newInstance(jobType));
            }
            try{
                setExchangeMethod = jobType.getMethod("setExchange", new Class[]{Exchange.class});
            }catch (NoSuchMethodException e) {
                LOG.debug("No setExchange(exchange) method found in Job, the message data will be ignored");
                setExchangeMethod = null;
            }
        }
        
    }

    public final void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getClazz() {
        return clazz;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getContext() {
        return context;
    }

    public TalendJob getJobInstance() {
        return jobInstance;
    }

    public Method getSetExchangeMethod() {
        return setExchangeMethod;
    }
    
    public void setPropagateHeader(boolean propagateHeader) {
		this.propagateHeader = propagateHeader;
	}
    
    public boolean isPropagateHeader() {
		return propagateHeader;
	}
}
