package org.talend.esb.policy.compression.test;


import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.cxf.message.Message;
import org.junit.Test;
import org.talend.esb.policy.compression.impl.CompressionInInterceptor;

public class CompressionInInterceptorTest {

	@Test
	public void handleNullMessage() {
		CompressionInInterceptor c = new CompressionInInterceptor();
		c.handleMessage(CompressionCommonTest.getMessageStub(null, null));

	}

	@Test
	public void handleEmptyMessage() throws Exception  {
		CompressionInInterceptor c = new CompressionInInterceptor();

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
	public void handleGetMessage() throws Exception {
		CompressionInInterceptor c = new CompressionInInterceptor();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Message.HTTP_REQUEST_METHOD, "GET");

		Message message = CompressionCommonTest.getMessageStub(null, map);
		c.decompressMessage(message);

	}
	
	
	@Test
	public void handleRuntimeException() throws Exception {
		CompressionInInterceptor c = new CompressionInInterceptor(){
			public void decompressMessage(Message message) throws org.apache.cxf.interceptor.Fault {
				throw new RuntimeException();
			};
		};
		
		try {
			c.handleMessage(null);
		}catch(RuntimeException ex){
			return;
		}
		
		fail("No exception is not expected");
	}
	
//	@Test
//	public void handleException() throws Exception {
//		CompressionInInterceptor c = new CompressionInInterceptor(){
//			@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
//			public void decompressMessage(Message message) throws org.apache.cxf.interceptor.Fault {
//				Exception ex = new Exception();
//				throw new Fault(ex);
//			};
//		};
//		
//		try {
//			c.handleMessage(null);
//		}catch(Exception ex){
//			return;
//		}
//		
//		fail("No exception is not expected");
//	}
	

}
