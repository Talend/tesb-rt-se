package org.talend.esb.sam.agent.interceptor;

import java.util.logging.Logger;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageUtils;

public class FlowIdProcessor  {
	
	protected static Logger logger = Logger.getLogger(FlowIdProcessor.class.getName());
	
	private MessageContextCodec codec;
	
	public FlowIdProcessor(MessageContextCodec codec) {
		this.codec = codec;
	}
	
	public void processMessage(Message message) throws Fault {
		logger.finest("MessageContextProcessor called. isOutbound: " + MessageUtils.isOutbound(message) + ", isRequestor: " + MessageUtils.isRequestor(message));
		
		if (MessageUtils.isOutbound(message)) {
			FlowId fId = (FlowId)message.get(FlowId.class);
			if (fId == null) {
				logger.warning("FlowId must not be null");
				return;	
			}
			
			String flowId = fId.getFlowId();
			if (flowId == null) {
				logger.warning("flowid must not be null");
				return;	
			}
			
			codec.writeFlowId(message, flowId);
		}
		else {
			//check whether flowId already stored in FlowId by lower level interceptor
			FlowId fId = FlowIdHelper.getFlowId(message);
			String existingFlowId = fId.getFlowId();
			
			//Read FlowId from soap header
			String flowId = codec.readFlowId(message);
			if (flowId == null) {
				logger.fine("No flowId found in MessageContextCodec [" + codec.getClass().getName() + "]");
				return;				
			}
			
			//check whether flowid matches
			if (existingFlowId != null) {
				
				if (!existingFlowId.equals(flowId)) {
					logger.warning("FlowId mismatches with FlowId of MessageContextCodec [" + codec.getClass().getName() + "]. Ignored.");
				}
				else {
					logger.fine("FlowId matches with FlowId of MessageContextCodec [" + codec.getClass().getName() + "].");
				}
			}
			else if (flowId != null) {
				fId.setFlowId(flowId);
				logger.fine("FlowId stored.");
			}
			
			if (MessageUtils.isRequestor(message)) {
				//check exchange match
				Message reqMsg = message.getExchange().getOutMessage();
				FlowId reqFId = (FlowId)reqMsg.get(FlowId.class);
				if (reqFId == null) {
					logger.warning("FlowId of Out message must not be null");
				}
				else {
					String reqFlowId = reqFId.getFlowId();
					if (!reqFlowId.equals(flowId)) {
						logger.warning("Sent and received flowid do not match");	
					}
				}
			}
		}
	}
	
}
