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
package org.talend.esb.sam.agent.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.namespace.QName;

import org.apache.cxf.message.Message;

public class FlowIdHelper {
	public static final String FLOW_ID_KEY = "FlowId";
	public static final QName FLOW_ID_QNAME = new QName(
			"http://www.talend.com/esb/sam/flowId/v1", "flowId");

	/**
	 * Get FlowId from message
	 * 
	 * @param message
	 * @return flowId or null if not set
	 */
	public static String getFlowId(Message message) {
		String flowId = (String) message.get(FLOW_ID_KEY);

		return flowId;
	}

	public static void setFlowId(Message message, String flowId) {
		message.put(FLOW_ID_KEY, flowId);
	}

}
