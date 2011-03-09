package org.sopera.monitoring.interceptor;

import java.util.logging.Logger;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageUtils;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.ws.addressing.ContextUtils;
import org.sopera.monitoring.event.MonitoringEventData;
import org.sopera.monitoring.helper.FlowIdHelper;


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
		MonitoringEventData edReq = FlowIdHelper.getMonitoringEventData(reqMsg, false);
		if (edReq == null) {
			logger.warning("OutMessage must contain MonitoringEventData");
			return;
		}
		
		String flowId = edReq.getFlowId();
		if (flowId == null) {
			logger.warning("flowId in OutMessage must not be null");
			return;
		}
		else {
			logger.info("flowId in OutMessage is: " + flowId);	
		}
		
		logger.fine("Check flowId in response message");
		
		MonitoringEventData ed = FlowIdHelper.getMonitoringEventData(message);
		flowId = ed.getFlowId();
		
		if (flowId != null) {
			logger.fine("FlowId '" + flowId + "' found in MonitoringEventData");
		}	
		else {
			
			logger.info("FlowId not found in MonitoringEventData");
		}
	}
	
	
	protected void handleRequestIn(T message) throws Fault {
		logger.fine("handleRequestIn");
		
		
		MonitoringEventData ed = FlowIdHelper.getMonitoringEventData(message);
		String flowId = ed.getFlowId();
		if (flowId != null) {
			logger.info("FlowId '" + flowId + "' found in MonitoringEventData");
		}
		else {
			logger.fine("FlowId not found in MonitoringEventData");
		}
		if (flowId == null) {
			logger.fine("Generate flowId");
			flowId = ContextUtils.generateUUID();
			ed.setFlowId(flowId);
			logger.info("Generated flowId '" + flowId + "' stored in MonitoringEventData");
		}
		
		
	}
	

}
