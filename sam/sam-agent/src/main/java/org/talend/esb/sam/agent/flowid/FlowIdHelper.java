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
package org.talend.esb.sam.agent.flowid;

import javax.xml.namespace.QName;

import org.apache.cxf.message.Message;


public class FlowIdHelper {

	public static final QName FLOW_ID_QNAME = new QName(
			"http://www.sopera.com/monitoring/flowId/v1", "flowId");

	
	/**
	 * Get FlowId from message
	 * 
	 * @param message
	 * @return new instance of FlowId if there is none.
	 */
	public static FlowId getOrCreateFlowId(Message message) {
		FlowId fId = (FlowId)message.get(FlowId.class);
		if (fId == null) {
			fId = new FlowId();
			message.put(FlowId.class, fId);	
		}
		return fId;
	}
	
	/**
	 * Get FlowId from message
	 * 
	 * @param message
	 * @return flowId or null if not set
	 */
	public static FlowId getFlowId(Message message) {
		return (FlowId)message.get(FlowId.class);
	}
	
	public static String getFlowIdAsString(Message message) {
		FlowId fId = (FlowId)message.get(FlowId.class);
		if (fId == null) {
			return null;
		} else {
			return fId.getFlowId();
		}
	}

}
