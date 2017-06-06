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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.impl.DefaultProducer;
import org.apache.camel.util.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import routines.system.api.TalendESBJobBean;
import routines.system.api.TalendJob;

/**
 * <p>
 * The Talend producer.
 * </p>
 */
public class TalendProducer extends DefaultProducer {

    private static class JobWrapper implements TalendESBJobBean {

        private final TalendJob job;

        public JobWrapper(TalendJob job) {
            super();
            this.job = job;
        }

        @Override
        public void prepareJob(String[] args) {
            // not supported with old-style jobs
        }

        @Override
        public void discardJob() {
            // not supported with old-style jobs
        }

        @Override
        public void runPreparedJob(Map<String, Object> exchangeData, String[] args) {
            setExchangeInJob((Exchange) exchangeData.get("exchange"));
            int success = job.runJobInTOS(args);
            if (success != 0) {
                signalJobFailure(args);
            }
        }

        @Override
        public void runSingleUseJob(Map<String, Object> exchangeData, String[] args) {
            setExchangeInJob((Exchange) exchangeData.get("exchange"));
            int success = job.runJobInTOS(args);
            if (success != 0) {
                signalJobFailure(args);
            }
        }

        @Override
        public Class<?> getJobClass() {
            return job.getClass();
        }

        private void setExchangeInJob(Exchange exchange) {
            try {
                final Method setExchangeMethod =
                        job.getClass().getMethod("setExchange", new Class[]{Exchange.class});
                LOG.debug("Pass the exchange from route to Job");
                ObjectHelper.invokeMethod(setExchangeMethod, job, exchange);
            } catch (NoSuchMethodException e) {
                LOG.debug("No setExchange(exchange) method found in Job, the message data will be ignored");
            }
        }

        private void signalJobFailure(String[] args) {
            throw new RuntimeCamelException("Execution of Talend job '" 
                    + job.getClass().getCanonicalName() + "' with args: "
                    + (args == null ? "none" : Arrays.toString(args))
                    + "' failed, see stderr for details. ");
        }
   }

    private static final transient Logger LOG = LoggerFactory.getLogger(TalendProducer.class);

    private TalendESBJobBean jobInstance;
    private final boolean stickyJob;
    private final boolean propagateHeader;

    public TalendProducer(TalendEndpoint endpoint) {
        super(endpoint);
        stickyJob = endpoint.isStickyJob();
        propagateHeader = endpoint.isPropagateHeader();
    }

    public void process(Exchange exchange) throws Exception {
        invokeTalendJob(exchange);
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        if (stickyJob) {
            if (jobInstance == null) {
                jobInstance = createJobInstance();
            }
            jobInstance.prepareJob(prepareArgs(null));
        }
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
        if (stickyJob && jobInstance != null) {
            jobInstance.discardJob();
            jobInstance = null;
        }
    }

    private String[] prepareArgs(Exchange exchange) {
        final TalendEndpoint talendEndpoint = (TalendEndpoint) getEndpoint();
        final String context = talendEndpoint.getContext();
        final Collection<String> args = new ArrayList<String>();
        if (context != null) {
            args.add("--context=" + context);
        }
        if (propagateHeader && exchange != null) {
            getParamsFromHeaders(exchange, args);
        }
        getParamsFromProperties(getEndpoint().getCamelContext().getProperties(), args);
        getParamsFromProperties(talendEndpoint.getEndpointProperties(), args);
        return args.toArray(new String[args.size()]);
    }

    private String[] prepareHeaderArgs(Exchange exchange) {
        if (!propagateHeader || exchange == null) {
            return null;
        }
        final Collection<String> args = new ArrayList<String>();
        getParamsFromHeaders(exchange, args);
        return args.toArray(new String[args.size()]);
    }

    private static void getParamsFromProperties(Map<String, String> propertiesMap, Collection<String> args) {
        if (propertiesMap != null) {
            for (Map.Entry<String, String> entry : propertiesMap.entrySet()) {
                args.add("--context_param " + entry.getKey() + '=' + entry.getValue());
            }
        }
    }

    private static void getParamsFromHeaders(
            Exchange exchange, Collection<String> args) {
        Map<String, Object> headers = exchange.getIn().getHeaders();
        for (Map.Entry<String, Object> header : headers.entrySet()) {
            Object headerValue = header.getValue();
            if (headerValue != null) {
                String headerStringValue = exchange.getContext().getTypeConverter()
                        .convertTo(String.class, exchange, headerValue);
                args.add("--context_param " + header.getKey() + '=' + headerStringValue);
            }
        }
    }

    private void invokeTalendJob(Exchange exchange) throws Exception {
        final TalendESBJobBean jobBean = stickyJob ? jobInstance : createJobInstance();
        if (jobBean == null) {
            throw new IllegalStateException("Job instance not initialized for invocation. ");
        }
        final Thread currentThread = Thread.currentThread();
        final ClassLoader oldCtxClassLoader = currentThread.getContextClassLoader();
        try {
            currentThread.setContextClassLoader(jobBean.getJobClass().getClassLoader());
            if (stickyJob) {
                String[] args = prepareHeaderArgs(exchange);
                logJobInvocation(jobBean, args);
                jobBean.runPreparedJob(Collections.singletonMap("exchange", (Object) exchange), args);
            } else {
                String[] args = prepareArgs(exchange);
                logJobInvocation(jobBean, args);
                jobBean.runSingleUseJob(Collections.singletonMap("exchange", (Object) exchange), args);
            }
        } finally {
            currentThread.setContextClassLoader(oldCtxClassLoader);
        }
    }

    private TalendESBJobBean createJobInstance() throws Exception {
        final TalendJob job = ((TalendEndpoint) getEndpoint()).getJobInstance();
        TalendESBJobBean jobBean = null;
        LOG.debug("Getting new job instance.");
        try {
            final Field esbJobBeanField = job.getClass().getField("esbJobBean");
            jobBean = (TalendESBJobBean) esbJobBeanField.get(job);
        } catch (NoSuchFieldException e) {
            LOG.debug("Reflective retrieval of Job access bean failed, assuming old-style job. ", e);
        }
        return jobBean == null ? new JobWrapper(job) : jobBean;
    }

    private void logJobInvocation(TalendESBJobBean job, String[] args) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Invoking Talend job '" + job.getJobClass().getCanonicalName() 
                    + ".runJob(String[] args)' with args: "
                    + (args == null ? "none" : Arrays.toString(args)));
        }
    }
}
