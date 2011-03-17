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
