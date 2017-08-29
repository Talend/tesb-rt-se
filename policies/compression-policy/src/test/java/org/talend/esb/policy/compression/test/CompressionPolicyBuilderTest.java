package org.talend.esb.policy.compression.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.cxf.message.Message;
import org.apache.cxf.ws.policy.AssertionInfoMap;
import org.apache.neethi.Assertion;
import org.junit.Test;
import org.talend.esb.policy.compression.impl.CompressionPolicyBuilder;
import org.xml.sax.SAXException;

public class CompressionPolicyBuilderTest {

	@Test
	public void testCompressionPolicyBuilder() {
		
		Collection<Assertion> c = new ArrayList<Assertion>();

		AssertionInfoMap aim = new AssertionInfoMap(c);

		Message m = CompressionCommonTest.getMessageStub(aim, null);

		try {
			assertNull(CompressionPolicyBuilder.getAssertion(m));
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

}
