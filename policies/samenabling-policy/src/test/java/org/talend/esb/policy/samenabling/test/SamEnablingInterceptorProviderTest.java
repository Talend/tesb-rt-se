package org.talend.esb.policy.samenabling.test;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.cxf.interceptor.Interceptor;
import org.apache.neethi.Assertion;
import org.junit.Test;
import org.talend.esb.policy.samenabling.SamEnablingInterceptorProvider;
import org.talend.esb.policy.samenabling.SamEnablingPolicy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SamEnablingInterceptorProviderTest {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testProcessEmptyMessage() {
		SamEnablingInterceptorProvider provider = new SamEnablingInterceptorProvider();
		provider.getOutInterceptors();

		for (Interceptor interceptor : provider.getOutInterceptors()) {
			interceptor.handleMessage(SamEnablingCommonTest.getMessageStub(
					null, null));
		}
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void testShutdown() {
		SamEnablingInterceptorProvider provider = new SamEnablingInterceptorProvider();
		provider.shutdown();
	}

	@SuppressWarnings({})
	@Test
	public void testIncorrectAppliesTo() throws Exception {
		SamEnablingInterceptorProvider provider = new SamEnablingInterceptorProvider();
		provider.getOutInterceptors();

		try {
			createSamlAssertion("notExistigAppliesTo");
			fail("Exception not thrown");
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}

	}

	public List<Assertion> createSamlAssertion(String appliesTo)
			throws Exception {

		List<Assertion> assertions = new ArrayList<Assertion>();

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbf.newDocumentBuilder();
		Document doc = builder.newDocument();

		Element element = doc.createElement("root");
		doc.appendChild(element);

		element.setAttribute("appliesTo", appliesTo);

		assertions.add(new SamEnablingPolicy(element));

		return assertions;

	}

}
