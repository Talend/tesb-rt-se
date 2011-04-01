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
package org.talend.esb.sam.agent.serviceclient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.activation.DataHandler;

import junit.framework.Assert;

import org.apache.cxf.helpers.IOUtils;
import org.junit.Test;
import org.talend.esb.sam._2011._03.common.EventType;
import org.talend.esb.sam.common.event.Event;

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

	private String getContent(DataHandler dh) throws IOException,
			UnsupportedEncodingException {
		InputStream is = dh.getInputStream();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		IOUtils.copy(is, bos);
		String outContent = bos.toString("UTF-8");
		return outContent;
	}
}
