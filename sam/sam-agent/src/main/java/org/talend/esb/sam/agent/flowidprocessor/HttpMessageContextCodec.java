/*
 * #%L
 * Service Activity Monitoring :: Agent
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
package org.talend.esb.sam.agent.flowidprocessor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.message.Message;


public class HttpMessageContextCodec implements MessageContextCodec {
	public static final String FLOWID_HTTP_HEADER_NAME = "flowid";
	protected static Logger logger = Logger.getLogger(HttpMessageContextCodec.class.getName());
	
	public HttpMessageContextCodec() {
	}
	
	public String readFlowId(Message message) {
		String flowId = null;
		Map<String, List<String>> headers = CastUtils.cast((Map<?, ?>)message.get(Message.PROTOCOL_HEADERS));
		if (headers != null) {
			List<String> flowIds = headers.get(FLOWID_HTTP_HEADER_NAME);
			if (flowIds != null && flowIds.size() > 0) {
				flowId = flowIds.get(0);
				logger.info("HTTP header '" + FLOWID_HTTP_HEADER_NAME + "' found: " + flowId);
			}
			else {
				logger.fine("No HTTP header '" + FLOWID_HTTP_HEADER_NAME + "' found");
			}
		}
		return flowId;
	}

	public void writeFlowId(Message message, String flowId) {
		Map<String, List<String>> headers = CastUtils.cast((Map<?, ?>)message.get(Message.PROTOCOL_HEADERS));
		if (headers != null) {
			headers.put(FLOWID_HTTP_HEADER_NAME, Arrays.asList(new String[]{flowId}));
			logger.info("HTTP header '" + FLOWID_HTTP_HEADER_NAME + "' set to: " + flowId);
		}
	}

}
