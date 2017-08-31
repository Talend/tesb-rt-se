package org.talend.esb.policy.compression.test;

import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.fail;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.message.Message;
import org.junit.Test;
import org.talend.esb.policy.compression.impl.CompressionOutInterceptor;
import org.talend.esb.policy.compression.impl.CompressionOutInterceptor.CompressionCachedOutputStreamCallback;

public class CompressionOutInterceptorTest {

	@Test
	public void testCompressionOutInterceptor() {

		CompressionOutInterceptor coi = new CompressionOutInterceptor(1000);
		assertEquals(coi.getThreshold(), 1000);
	}

	@Test
	public void handleNullMessage() {
		CompressionOutInterceptor c = new CompressionOutInterceptor();
		c.handleMessage(CompressionCommonTest.getMessageStub(null, null));

	}

	@Test
	public void handleEmptyMessage() throws Exception {
		CompressionOutInterceptor c = new CompressionOutInterceptor();

		Map<String, Object> map = new HashMap<String, Object>();

		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		byte[] buff = new byte[1024];
		XMLStreamReader reader = inputFactory
				.createXMLStreamReader(new ByteArrayInputStream(buff));

		map.put(XMLStreamReader.class.getName(), reader);

		map.put(InputStream.class.getName(), new ByteArrayInputStream(buff));

		Message message = CompressionCommonTest.getMessageStub(null, map);

		c.handleMessage(message);

	}

	@Test
	public void handleRuntimeException1() throws Exception {
		CompressionOutInterceptor c = new CompressionOutInterceptor() {
			public void wrapOriginalOutputStream(Message message) throws Fault {
				throw new RuntimeException();
			}
		};

		try {
			c.handleMessage(CompressionCommonTest.getMessageStub(null, null));
		} catch (RuntimeException ex) {
			return;
		}

		fail("No exception is not expected");
	}

	@Test
	public void handleRuntimeException2() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Message.HTTP_REQUEST_METHOD, "GET");

		Message message = CompressionCommonTest.getMessageStub(null, map);

		ByteArrayOutputStream os = new ByteArrayOutputStream();

		CompressionCachedOutputStreamCallback callback = new CompressionCachedOutputStreamCallback(
				os, 1024, message);

		CachedOutputStream wrapper = new CachedOutputStream();

		try {
			callback.onClose(wrapper);
		} catch (RuntimeException ex) {
			return;
		}

		fail("No exception is not expected");
	}

}
