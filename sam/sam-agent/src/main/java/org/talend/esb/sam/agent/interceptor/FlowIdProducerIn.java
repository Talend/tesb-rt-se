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
package org.talend.esb.sam.agent.interceptor;

import java.util.logging.Logger;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageUtils;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.ws.addressing.ContextUtils;


public class FlowIdProducerIn<T extends Message> extends AbstractPhaseInterceptor<T> {
	
	protected static Logger logger = Logger.getLogger(FlowIdProducerIn.class.getName());
	
	public FlowIdProducerIn(){
		super(Phase.PRE_INVOKE);
	}
	
	public FlowIdProducerIn(String phase){
		super(phase);
	}
	
	public void handleMessage(T message) throws Fault {
		logger.finest("FlowIdProducerIn Interceptor called. isOutbound: " + MessageUtils.isOutbound(message) + ", isRequestor: " + MessageUtils.isRequestor(message));
		
		if (MessageUtils.isRequestor(message)) {
			handleResponseIn(message);
		}
		else {
			handleRequestIn(message);
		}
		
	}
	
	
	protected void handleResponseIn(T message) throws Fault {
		logger.fine("handleResponseIn");
		
		Message reqMsg = message.getExchange().getOutMessage();
		if (reqMsg == null) {
			logger.warning("getOutMessage is null");
			return;
		}
		
		//MonitoringEventData edReq = (MonitoringEventData)reqMsg.get(MonitoringEventData.class);
		FlowId reqFid = FlowIdHelper.getFlowId(reqMsg, false);
		if (reqFid == null) {
			logger.warning("OutMessage must contain FlowId");
			return;
		}
		
		String flowId = reqFid.getFlowId();
		if (flowId == null) {
			logger.warning("flowId in OutMessage must not be null");
			return;
		}
		else {
			logger.info("flowId in OutMessage is: " + flowId);	
		}
		
		logger.fine("Check flowId in response message");
		
		FlowId fId = FlowIdHelper.getFlowId(message);
		flowId = fId.getFlowId();
		
		if (flowId != null) {
			logger.fine("FlowId '" + flowId + "' found in FlowId");
		}	
		else {
			
			logger.info("FlowId not found in FlowId");
		}
	}
	
	
	protected void handleRequestIn(T message) throws Fault {
		logger.fine("handleRequestIn");
		
		
		FlowId fId = FlowIdHelper.getFlowId(message);
		String flowId = fId.getFlowId();
		if (flowId != null) {
			logger.info("FlowId '" + flowId + "' found in FlowId");
		}
		else {
			logger.fine("FlowId not found in FlowId");
		}
		if (flowId == null) {
			logger.fine("Generate flowId");
			flowId = ContextUtils.generateUUID();
			fId.setFlowId(flowId);
			logger.info("Generated flowId '" + flowId + "' stored in FlowId");
		}
		
		
	}
	

}
