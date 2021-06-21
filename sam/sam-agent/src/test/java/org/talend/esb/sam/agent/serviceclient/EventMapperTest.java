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
package org.talend.esb.sam.agent.serviceclient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.activation.DataHandler;

import junit.framework.Assert;

import org.apache.cxf.helpers.IOUtils;
import org.easymock.EasyMock;
import org.junit.Test;
import org.talend.esb.sam._2011._03.common.EventType;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.event.MessageInfo;
import org.talend.esb.sam.common.event.Originator;

@SuppressWarnings("deprecation")
public class EventMapperTest {

	@Test
	public void testEventMapper() throws IOException {
		Event event = new Event();
		event.setContent("testContent");
		EventType eventOut = EventMapper.map(event);
		DataHandler dh = eventOut.getContent();
		String outContent = getContent(dh);
		Assert.assertEquals(event.getContent(), outContent);
		// TODO test the other properties
	}

	@Test
	public void testEventMapper2() throws IOException {
		Event event = new Event();
		event.setContent("testContent");

		MessageInfo messageInfo = EasyMock.createMock(MessageInfo.class);
		EasyMock.expect(messageInfo.getMessageId()).andReturn("MessageId").anyTimes();
		EasyMock.expect(messageInfo.getFlowId()).andReturn("FlowId").anyTimes();
		EasyMock.expect(messageInfo.getPortType()).andReturn("PortType").anyTimes();
		EasyMock.expect(messageInfo.getOperationName()).andReturn("OperationName").anyTimes();
		EasyMock.expect(messageInfo.getTransportType()).andReturn("TransportType").anyTimes();
		EasyMock.replay(messageInfo);
		event.setMessageInfo(messageInfo);

		Originator originator = EasyMock.createMock(Originator.class);
		EasyMock.expect(originator.getProcessId()).andReturn("ProcessId").anyTimes();
		EasyMock.expect(originator.getIp()).andReturn("Ip").anyTimes();
		EasyMock.expect(originator.getHostname()).andReturn("Hostname").anyTimes();
		EasyMock.expect(originator.getCustomId()).andReturn("CustomId").anyTimes();
		EasyMock.expect(originator.getPrincipal()).andReturn("Principal").anyTimes();
		EasyMock.replay(originator);
		event.setOriginator(originator);

		event.getCustomInfo().put("testKey", "testValue");

		EventType eventOut = EventMapper.map(event);
		DataHandler dh = eventOut.getContent();
		String outContent = getContent(dh);
		Assert.assertEquals(event.getContent(), outContent);

		Assert.assertEquals(eventOut.getOriginator().getProcessId(), event.getOriginator().getProcessId());
		Assert.assertEquals(eventOut.getOriginator().getIp(), event.getOriginator().getIp());
		Assert.assertEquals(eventOut.getOriginator().getHostname(), event.getOriginator().getHostname());
		Assert.assertEquals(eventOut.getOriginator().getCustomId(), event.getOriginator().getCustomId());
		Assert.assertEquals(eventOut.getOriginator().getPrincipal(), event.getOriginator().getPrincipal());

		Assert.assertEquals(eventOut.getMessageInfo().getMessageId(), event.getMessageInfo().getMessageId());
		Assert.assertEquals(eventOut.getMessageInfo().getFlowId(), event.getMessageInfo().getFlowId());
		Assert.assertSame(eventOut.getMessageInfo().getPorttype().getLocalPart(), event.getMessageInfo().getPortType());
		Assert.assertEquals(eventOut.getMessageInfo().getOperationName(), event.getMessageInfo().getOperationName());
		Assert.assertEquals(eventOut.getMessageInfo().getTransport(), event.getMessageInfo().getTransportType());

		Assert.assertEquals(eventOut.getCustomInfo().getItem().get(0).getValue(), "testValue");
		Assert.assertEquals(eventOut.getCustomInfo().getItem().get(0).getKey(), "testKey");

	}

	private String getContent(DataHandler dh) throws IOException, UnsupportedEncodingException {
		InputStream is = dh.getInputStream();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		IOUtils.copy(is, bos);
		String outContent = bos.toString("UTF-8");
		return outContent;
	}
}
