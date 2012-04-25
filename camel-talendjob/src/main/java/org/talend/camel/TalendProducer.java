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
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.impl.DefaultProducer;
import org.apache.camel.util.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * The Talend producer.
 * </p>
 */
public class TalendProducer extends DefaultProducer {

	private static final transient Logger LOG = LoggerFactory.getLogger(TalendProducer.class);
	private static final String[] EMPTY_STRING_ARRAY = {};

	public TalendProducer(TalendEndpoint endpoint) {
		super(endpoint);
	}

	public void process(Exchange exchange) throws Exception {
		Object jobInstance = ((TalendEndpoint) getEndpoint()).getJobInstance();
		Method jobMethod = ((TalendEndpoint) getEndpoint()).getJobMethod();
		String context = ((TalendEndpoint) getEndpoint()).getContext();

		List<String> args = new ArrayList<String>();
		if (context != null) {
			args.add("--context=" + context);
		}
		populateTalendContextParamsWithCamelHeaders(exchange, args);
		invokeTalendJob(jobInstance, jobMethod, args);
	}

	private void populateTalendContextParamsWithCamelHeaders(Exchange exchange, List<String> args) {
		Map<String, Object> headers = exchange.getIn().getHeaders();
		for (Map.Entry<String, Object> entry : headers.entrySet()) {
			String headerKey = entry.getKey();
			Object headerValue = entry.getValue();
			if (headerValue != null) {
				String headerStringValue = exchange.getContext().getTypeConverter().convertTo(String.class, exchange, headerValue);
				args.add("--context_param " + headerKey + "=" + headerStringValue);
			}
		}
	}

	private void invokeTalendJob(Object jobInstance, Method jobMethod, List<String> args) {
		LOG.debug("Invoking Talend job '" + jobInstance.getClass().getCanonicalName() 
				+ ".runJob(String[] args)' with args: " + args.toString());
		
		Object result = ObjectHelper.invokeMethod(jobMethod, jobInstance, new Object[] { args.toArray(EMPTY_STRING_ARRAY) });
		if (result instanceof Integer && ((Integer) result).intValue() != 0) {
			throw new RuntimeCamelException("Execution of Talend job '" 
					+ jobInstance.getClass().getCanonicalName() + "' with args: "
					+ args.toString() + "' failed, see stderr for details"); // Talend logs errors using System.err.println
		}
	}
}
