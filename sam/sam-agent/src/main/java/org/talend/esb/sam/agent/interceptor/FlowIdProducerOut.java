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

import java.lang.ref.WeakReference;
import java.util.logging.Logger;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageUtils;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.ws.addressing.ContextUtils;


public class FlowIdProducerOut<T extends Message> extends AbstractPhaseInterceptor<T> {
	
	protected static Logger logger = Logger.getLogger(FlowIdProducerOut.class.getName());
	
	public FlowIdProducerOut(){
		super(Phase.USER_LOGICAL);
	}
	
	public FlowIdProducerOut(String phase){
		super(phase);
	}
	
	public void handleMessage(T message) throws Fault {
		logger.finest("FlowIdProducerOut Interceptor called. isOutbound: " + MessageUtils.isOutbound(message) + ", isRequestor: " + MessageUtils.isRequestor(message));
		
		if (MessageUtils.isRequestor(message)) {
			handleRequestOut(message);
		}
		else {
			handleResponseOut(message);
		}
		
	}
	
	
	protected void handleResponseOut(T message) throws Fault {
		logger.info("handleResponseOut");
		
		Message reqMsg = message.getExchange().getInMessage();
		if (reqMsg == null) {
			logger.warning("getInMessage is null");
			return;
		}
		
		FlowId reqFid = FlowIdHelper.getFlowId(reqMsg, false);
		//MonitoringEventData edReq = (MonitoringEventData)reqMsg.get(MonitoringEventData.class);
		if (reqFid == null) {
			logger.warning("InMessage must contain FlowId");
			return;
		}
		
		String flowId = reqFid.getFlowId();
		if (flowId == null) {
			logger.warning("flowId in InMessage must not be null");
			return;
		}
		
		FlowId fId = FlowIdHelper.getFlowId(message);
		fId.setFlowId(flowId);	
		
	}
	
	
	protected void handleRequestOut(T message) throws Fault {
		logger.fine("handleRequestIn");
		
		String flowId = null;
		if (message.containsKey(PhaseInterceptorChain.PREVIOUS_MESSAGE)) {
			// Web Service consumer is acting as an intermediary
			logger.info("PREVIOUS_MESSAGE FOUND!!!");
			@SuppressWarnings("unchecked")
                        WeakReference<Message> wrPreviousMessage = (WeakReference<Message>)message.get(PhaseInterceptorChain.PREVIOUS_MESSAGE);
			Message previousMessage = (Message)wrPreviousMessage.get();
			//MonitoringEventData ed = (MonitoringEventData)previousMessage.get(MonitoringEventData.class);		
			FlowId fId = FlowIdHelper.getFlowId(previousMessage, false);
			if (fId != null) {
				flowId = fId.getFlowId();
				logger.fine("flowId '" + flowId + "' found in previous message");
				FlowId fId2 = FlowIdHelper.getFlowId(message);
				fId2.setFlowId(flowId);
				logger.info("flowId '" + flowId + "' added to FlowId of current message");
				
			}
			else logger.warning("FlowId not set. FlowId not found. Is monitoring enabled for published web services?"); 
			
		} else {
			// Web Service consumer is a native client
			logger.info("PREVIOUS_MESSAGE not found");
			FlowId fId = FlowIdHelper.getFlowId(message);
			flowId = fId.getFlowId();
			if (flowId != null) {
				logger.fine("FlowId '" + flowId + "' found in FlowId");
			}
		}
	 
		// No flowId found. Generate one.
		if (flowId == null) {
			logger.fine("Generate and add flowId");
			flowId = ContextUtils.generateUUID();
			FlowId fId = FlowIdHelper.getFlowId(message);
			fId.setFlowId(flowId);
			logger.info("FlowId '" + flowId + "' added to FlowId");
		}	
		
	}
	
	
}
