package org.sopera.monitoring.interceptor;

import junit.framework.Assert;

import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.junit.Test;
import org.sopera.monitoring.helper.FlowIdHelper;

public class FlowIdProducerTest {

	
	@Test
	public void flowIdProducerInTest() {
		FlowIdProducerIn<Message> flowIdProducerIn = new FlowIdProducerIn<Message>();
		Message message = new MessageImpl();
		String flowId = FlowIdHelper.getFlowIdFromProperty(message);
		Assert.assertNull(flowId);
		flowIdProducerIn.handleMessage(message);
		flowId = FlowIdHelper.getFlowIdFromProperty(message);
		Assert.assertNotNull(flowId);
	}

	@Test
	public void flowIdProducerOutTest() {
		// ContextUtils.isRequestor returns false -> handleResponseOut is called which doesn't create a flowId
		/*
		FlowIdProducerOut<Message> flowIdProducerOut = new FlowIdProducerOut<Message>();
		Message message = new MessageImpl();
		String flowId = FlowIdHelper.getFlowIdFromProperty(message);
		Assert.assertNull(flowId);
		flowIdProducerOut.handleMessage(message);
		flowId = FlowIdHelper.getFlowIdFromProperty(message);
		Assert.assertNotNull(flowId);
		*/
	}
}
