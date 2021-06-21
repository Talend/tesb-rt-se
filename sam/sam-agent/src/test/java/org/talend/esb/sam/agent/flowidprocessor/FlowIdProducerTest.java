/*
 * #%L
 * Service Activity Monitoring :: Agent
 * %%
 * Copyright (c) 2006-2021 Talend Inc. - www.talend.com
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
package org.talend.esb.sam.agent.flowidprocessor;

import junit.framework.Assert;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.ExchangeImpl;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.easymock.EasyMock;
import org.junit.Test;
import org.talend.esb.sam.agent.flowidprocessor.FlowIdProducerIn;
import org.talend.esb.sam.agent.message.FlowIdHelper;

public class FlowIdProducerTest {

	static {
		Logger LOG = Logger.getLogger(FlowIdProducerOut.class
	            .getName());
		LOG.setLevel(Level.FINEST);
	}

	@Test
	public void flowIdProducerInTest() {
		FlowIdProducerIn<Message> flowIdProducerIn = new FlowIdProducerIn<Message>();
		Message message = new MessageImpl();
		Exchange exchange = new ExchangeImpl();
		message.setExchange(exchange);
		String flowId = FlowIdHelper.getFlowId(message);

		Assert.assertNull("FlowId should be null before FlowIdProducerIn handleMessage()", flowId);
		flowIdProducerIn.handleMessage(message);
		flowId = FlowIdHelper.getFlowId(message);
		Assert.assertNotNull("FlowId should not be null after FlowIdProducerIn handleMessage()", flowId);
	}

	@Test
	public void flowIdProducerIn2Test() {
		FlowIdProducerIn<Message> flowIdProducerIn = new FlowIdProducerIn<Message>();
		Message message = new MessageImpl();
		Exchange exchange = new ExchangeImpl();
		message.setExchange(exchange);
		String flowId = FlowIdHelper.getFlowId(message);

		Assert.assertNull("FlowId should be null before FlowIdProducerIn handleMessage()", flowId);
		Map<String, List<String>> headers = new HashMap<String, List<String>>();
		headers.put("flowid", Arrays.asList("flowid"));
        message.put(Message.PROTOCOL_HEADERS, headers);
		flowIdProducerIn.handleMessage(message);
		flowId = FlowIdHelper.getFlowId(message);
		Assert.assertNotNull("FlowId should not be null after FlowIdProducerIn handleMessage()", flowId);
	}

	@Test
	public void flowIdProducerOutTest() {
		FlowIdProducerOut<Message> flowIdProducerOut = new FlowIdProducerOut<Message>();
		Message message = new MessageImpl();
		Exchange exchange = new ExchangeImpl();
		Message inMessage = new MessageImpl();
		exchange.setInMessage(inMessage);
		message.setExchange(exchange);

		String flowId = FlowIdHelper.getFlowId(message);
		Assert.assertNull("FlowId should be null before FlowIdProducerOut handleMessage()", flowId);
		flowIdProducerOut.handleMessage(message);
		flowId = FlowIdHelper.getFlowId(message);
		Assert.assertNotNull("FlowId should not be null after FlowIdProducerOut handleMessage()", flowId);
	}

	@Test
	public void flowIdProducerOut2Test() {
		FlowIdProducerOut<Message> flowIdProducerOut = new FlowIdProducerOut<Message>();
		Message message = new MessageImpl();
		Exchange exchange = new ExchangeImpl();
		Message inMessage = new MessageImpl();
		exchange.setInMessage(inMessage);
		message.setExchange(exchange);

		WeakReference<Message> wrPreviousMessage = EasyMock.createMock(WeakReference.class);
		EasyMock.expect(wrPreviousMessage.get()).andReturn(message);
		EasyMock.replay(wrPreviousMessage);
		message.put(PhaseInterceptorChain.PREVIOUS_MESSAGE, wrPreviousMessage);
		message.put(Message.REQUESTOR_ROLE, true);



		String flowId = FlowIdHelper.getFlowId(message);
		Assert.assertNull("FlowId should be null before FlowIdProducerOut handleMessage()", flowId);
		flowIdProducerOut.handleMessage(message);
		flowId = FlowIdHelper.getFlowId(message);
		Assert.assertNotNull("FlowId should not be null after FlowIdProducerOut handleMessage()", flowId);
	}
}
