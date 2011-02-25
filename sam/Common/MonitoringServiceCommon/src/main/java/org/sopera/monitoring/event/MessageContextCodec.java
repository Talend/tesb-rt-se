package org.sopera.monitoring.event;

import org.apache.cxf.message.Message;

public interface MessageContextCodec {

	/**
	 * Decode the <b>flowId</b>.
	 * @return current value of FlowId
	*/
	String readFlowId(Message message);
	
	/**
	 * Encode the <b>flowId</b>.
	 * @param flowId new value for FlowId
	*/
	void writeFlowId(Message message, String flowId);
	
}
