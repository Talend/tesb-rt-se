package org.talend.esb.sam.agent.interceptor;

import java.lang.ref.WeakReference;
import java.util.logging.Logger;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.ws.addressing.ContextUtils;
import org.talend.esb.sam.agent.event.MonitoringEventData;
import org.talend.esb.sam.agent.event.MonitoringEventDataImpl;

/**
 * Creates a flowId in the message header.
 * 
 * @author cschmuelling
 * 
 */
public abstract class AbstractFlowIdProducer<T extends Message> extends
		AbstractPhaseInterceptor<T> {
	protected static Logger logger = Logger
			.getLogger(AbstractFlowIdProducer.class.getName());

	public AbstractFlowIdProducer(String phase) {
		super(phase);
	}

	abstract public void processFlowId(T message, String flowId) throws Fault;

	public void handleMessage(T message) throws Fault {
		logger.info("FlowId Interceptor called. isOutbound: " + ContextUtils.isOutbound(message) + ", isRequestor: " + ContextUtils.isRequestor(message));
		
		String flowId = null;
		if (message.containsKey(PhaseInterceptorChain.PREVIOUS_MESSAGE)) {
			logger.info("PREVIOUS_MESSAGE FOUND!!!");
			@SuppressWarnings("unchecked")
                        WeakReference<Message> wrPreviousMessage = (WeakReference<Message>)message.get(PhaseInterceptorChain.PREVIOUS_MESSAGE);
			Message previousMessage = (Message)wrPreviousMessage.get();
			//flowId = FlowIdHelper.getFlowId(previousMessage);
			MonitoringEventData ed = (MonitoringEventData)previousMessage.get(MonitoringEventData.class);		
			if (ed != null) {
				flowId = ed.getFlowId();
				logger.info("flowId '" + flowId + "' found in previous message");
				MonitoringEventData ed2 = new MonitoringEventDataImpl();
				ed2.setFlowId(flowId);
				message.put(MonitoringEventData.class, ed2);
				logger.info("flowId '" + flowId + "' added to MonitoringEventData of current message");
				
			}
			else logger.info("MonitoringEventData not set. FlowId not found"); 
			
		} else {
			logger.info("PREVIOUS_MESSAGE not found");
			//flowId = FlowIdHelper.getFlowId(message);
			MonitoringEventData ed = (MonitoringEventData)message.get(MonitoringEventData.class);
			if (ed != null) {
				flowId = ed.getFlowId();
				logger.info("FlowId '" + flowId + "' found in MonitoringEventData");
			}
			else {
				// should be moved into a soap specific interceptor
				logger.info("FlowId not found in MonitoringEventData (null)");
				flowId = FlowIdHelper.getFlowIdFromSoapHeader(message);
				if (flowId != null) {
					logger.info("FlowId '" + flowId + "' found in SOAP Header and added to MonitoringEventData");
					ed = new MonitoringEventDataImpl();
					ed.setFlowId(flowId);
					message.put(MonitoringEventData.class, ed);
				}
				else logger.info("FlowId not found in SOAP Header");
			}
		}
	 
		// No flowId found. Generate one.
		if (flowId == null) {
			logger.info("Generate and add flowId");
			flowId = ContextUtils.generateUUID();
			MonitoringEventData ed = new MonitoringEventDataImpl();
			ed.setFlowId(flowId);
			message.put(MonitoringEventData.class, ed);
			logger.info("FlowId '" + flowId + "' added to MonitoringEventData");
		}
		
		processFlowId(message, flowId);
	}

}
