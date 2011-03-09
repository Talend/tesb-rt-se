package org.talend.esb.sam.agent.interceptor;

import java.util.logging.Logger;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageUtils;
import org.talend.esb.sam.agent.event.MonitoringEventData;

public class FlowIdProcessor  {
	
	protected static Logger logger = Logger.getLogger(FlowIdProcessor.class.getName());
	
	private MessageContextCodec codec;
	
	public FlowIdProcessor(MessageContextCodec codec) {
		this.codec = codec;
	}
	
	public void processMessage(Message message) throws Fault {
		logger.finest("MessageContextProcessor called. isOutbound: " + MessageUtils.isOutbound(message) + ", isRequestor: " + MessageUtils.isRequestor(message));
		
		if (MessageUtils.isOutbound(message)) {
			MonitoringEventData ed = (MonitoringEventData)message.get(MonitoringEventData.class);
			if (ed == null) {
				logger.warning("MonitoringEventData must not be null");
				return;	
			}
			
			String flowId = ed.getFlowId();
			if (flowId == null) {
				logger.warning("Flowid must not be null");
				return;	
			}
			
			codec.writeFlowId(message, flowId);
		}
		else {
			//check whether flowId already stored in MonitoringEventData by lower level interceptor
			MonitoringEventData ed = FlowIdHelper.getMonitoringEventData(message);
			String existingFlowId = ed.getFlowId();
			
			//Read FlowId from soap header
			String flowId = codec.readFlowId(message);
			if (flowId == null) {
				logger.fine("No flowId found in MessageContextCodec [" + codec.getClass().getName() + "]");
				return;				
			}
			
			//check whether flowid matches
			if (existingFlowId != null) {
				
				if (!existingFlowId.equals(flowId)) {
					logger.warning("FlowId in MonitoringEventData mismatches with FlowId of MessageContextCodec [" + codec.getClass().getName() + "]. Ignored.");
				}
				else {
					logger.fine("FlowId in MonitoringEventData matches with FlowId of MessageContextCodec [" + codec.getClass().getName() + "].");
				}
			}
			else if (flowId != null) {
				ed.setFlowId(flowId);
				logger.fine("FlowId stored in MonitoringEventData");
			}
			
			if (MessageUtils.isRequestor(message)) {
				//check exchange match
				Message reqMsg = message.getExchange().getOutMessage();
				MonitoringEventData reqEd = (MonitoringEventData)reqMsg.get(MonitoringEventData.class);
				if (reqEd == null) {
					logger.warning("MonitoringEventData of Out message must not be null");
				}
				else {
					String reqFlowId = reqEd.getFlowId();
					if (!reqFlowId.equals(flowId)) {
						logger.warning("Sent and received flowid do not match");	
					}
				}
			}
		}
	}
	
}
