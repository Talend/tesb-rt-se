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
import org.talend.esb.sam.agent.event.MonitoringEventData;


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
		
		MonitoringEventData edReq = FlowIdHelper.getMonitoringEventData(reqMsg, false);
		//MonitoringEventData edReq = (MonitoringEventData)reqMsg.get(MonitoringEventData.class);
		if (edReq == null) {
			logger.warning("InMessage must contain MonitoringEventData");
			return;
		}
		
		String flowId = edReq.getFlowId();
		if (flowId == null) {
			logger.warning("flowId in InMessage must not be null");
			return;
		}
		
		MonitoringEventData ed = FlowIdHelper.getMonitoringEventData(message);
		ed.setFlowId(flowId);	
		
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
			MonitoringEventData ed = FlowIdHelper.getMonitoringEventData(previousMessage, false);
			if (ed != null) {
				flowId = ed.getFlowId();
				logger.fine("flowId '" + flowId + "' found in previous message");
				MonitoringEventData ed2 = FlowIdHelper.getMonitoringEventData(message);
				ed2.setFlowId(flowId);
				logger.info("flowId '" + flowId + "' added to MonitoringEventData of current message");
				
			}
			else logger.warning("MonitoringEventData not set. FlowId not found. Is monitoring enabled for published web services?"); 
			
		} else {
			// Web Service consumer is a native client
			logger.info("PREVIOUS_MESSAGE not found");
			MonitoringEventData ed = FlowIdHelper.getMonitoringEventData(message);
			flowId = ed.getFlowId();
			if (flowId != null) {
				logger.fine("FlowId '" + flowId + "' found in MonitoringEventData");
			}
		}
	 
		// No flowId found. Generate one.
		if (flowId == null) {
			logger.fine("Generate and add flowId");
			flowId = ContextUtils.generateUUID();
			MonitoringEventData ed = FlowIdHelper.getMonitoringEventData(message);
			ed.setFlowId(flowId);
			logger.info("FlowId '" + flowId + "' added to MonitoringEventData");
		}	
		
	}
	
	
}
