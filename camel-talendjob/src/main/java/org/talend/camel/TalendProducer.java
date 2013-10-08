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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.impl.DefaultProducer;
import org.apache.camel.util.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import routines.system.api.TalendJob;

/**
 * <p>
 * The Talend producer.
 * </p>
 */
public class TalendProducer extends DefaultProducer {

    private static final transient Logger LOG = LoggerFactory.getLogger(TalendProducer.class);

    public TalendProducer(TalendEndpoint endpoint) {
        super(endpoint);
    }

	public void process(Exchange exchange) throws Exception {
		TalendJob jobInstance = ((TalendEndpoint) getEndpoint())
				.getJobInstance();
		String context = ((TalendEndpoint) getEndpoint()).getContext();
		Method setExchangeMethod = ((TalendEndpoint) getEndpoint())
				.getSetExchangeMethod();
		Map<String, String> propertiesMap = getEndpoint()
				.getCamelContext().getProperties();

		Collection<String> args = new ArrayList<String>();
		if (context != null) {
			args.add("--context=" + context);
		}

		if (((TalendEndpoint)getEndpoint()).isPropagateHeader()) {		
			populateTalendContextParamsWithCamelHeaders(exchange, args);
		}

		addTalendContextParamsFromCTalendJobContext(propertiesMap, args);
		invokeTalendJob(jobInstance, args.toArray(new String[args.size()]), setExchangeMethod, exchange);
	}

	private static void addTalendContextParamsFromCTalendJobContext(
			Map<String, String> propertiesMap, Collection<String> args) {
		if (propertiesMap != null) {
			for (Map.Entry<String, String> entry : propertiesMap.entrySet()) {
				args.add("--context_param " + entry.getKey() + '=' + entry.getValue());
			}
		}
	}

    private static void populateTalendContextParamsWithCamelHeaders(Exchange exchange, Collection<String> args) {
        Map<String, Object> headers = exchange.getIn().getHeaders();
        for (Map.Entry<String, Object> header : headers.entrySet()) {
            Object headerValue = header.getValue();
            if (headerValue != null) {
                String headerStringValue = exchange.getContext().getTypeConverter().convertTo(String.class, exchange, headerValue);
                args.add("--context_param " + header.getKey() + '=' + headerStringValue);
            }
        }
    }

    private void invokeTalendJob(TalendJob jobInstance, String[] args, Method setExchangeMethod, Exchange exchange) {
        if(setExchangeMethod != null){
            LOG.debug("Pass the exchange from router to Job");
            ObjectHelper.invokeMethod(setExchangeMethod, jobInstance, exchange);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Invoking Talend job '" + jobInstance.getClass().getCanonicalName() 
                    + ".runJob(String[] args)' with args: " + Arrays.toString(args));
        }

        ClassLoader oldContextCL = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(jobInstance.getClass().getClassLoader());
            int result = jobInstance.runJobInTOS(args);
            if (result != 0) {
                throw new RuntimeCamelException("Execution of Talend job '" 
                        + jobInstance.getClass().getCanonicalName() + "' with args: "
                        + Arrays.toString(args) + "' failed, see stderr for details"); // Talend logs errors using System.err.println
            }
        } finally {
            Thread.currentThread().setContextClassLoader(oldContextCL);
        }
    }

}
