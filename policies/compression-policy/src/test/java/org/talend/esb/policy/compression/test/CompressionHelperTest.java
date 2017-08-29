package org.talend.esb.policy.compression.test;

import static org.junit.Assert.*;

import java.util.Scanner;
import javax.xml.stream.XMLStreamException;

import org.apache.cxf.io.CachedOutputStream;
import org.junit.Test;
import org.talend.esb.policy.compression.impl.internal.CompressionHelper;

public class CompressionHelperTest {

	@Test
	public void testExceptionHandling() {

		try {
			CompressionHelper.loadSoapBodyContent(null, null, null);
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			fail("XMLStreamException is not expected");
		} catch (RuntimeException ex) {
			return;
		}

		fail("No exception is not expected");
	}

	@Test
	public void testSoapBodyIsNotFound() throws Exception {

		// Loading SOAP body content to separate stream
		CachedOutputStream soapBodyContent = new CachedOutputStream();

		CachedOutputStream cache = new CachedOutputStream();
		cache.write("1".getBytes());

		Scanner scanner = new Scanner(cache.getInputStream());

		try {
			CompressionHelper.loadSoapBodyContent(soapBodyContent, scanner,
					"(\\d)(a)*");
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			fail("XMLStreamException is not expected");
		} catch (RuntimeException ex) {
			return;
		} finally {
			cache.close();
		}

		fail("No exception is not expected");
	}

	@Test
	public void testNoSoapContentIsFound() {

		try {
			CompressionHelper.loadSoapBodyContent(null, null, null);
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			fail("XMLStreamException is not expected");
		} catch (RuntimeException ex) {
			return;
		}

		fail("No exception is not expected");
	}
	
	@Test
	public void testNullScaner() throws Exception {

		// Loading SOAP body content to separate stream
		CachedOutputStream soapBodyContent = new CachedOutputStream();

		CachedOutputStream cache = new CachedOutputStream();
		cache.write("1".getBytes());

		try {
			CompressionHelper.loadSoapBodyContent(soapBodyContent, null,
					"(\\d)(a)*");
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			fail("XMLStreamException is not expected");
		} catch (RuntimeException ex) {
			return;
		} finally {
			cache.close();
		}

		fail("No exception is not expected");
	}
	

}
