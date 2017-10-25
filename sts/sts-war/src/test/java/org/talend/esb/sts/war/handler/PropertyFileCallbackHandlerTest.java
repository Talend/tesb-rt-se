package org.talend.esb.sts.war.handler;

import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.wss4j.common.WSS4JConstants;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.dom.handler.RequestData;
import org.apache.wss4j.dom.message.token.UsernameToken;
import org.apache.wss4j.dom.validate.Credential;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class PropertyFileCallbackHandlerTest {

	static {
		org.apache.xml.security.Init.init();
	}

	@Test
	public void testPropertyFileCallbackHandler() throws Exception {

		PropertyFileCallbackHandler handler = new PropertyFileCallbackHandler();

		Document doc = getDocument();

		UsernameToken ut = new UsernameToken(true, doc, WSS4JConstants.PASSWORD_TEXT);
		ut.setName("tadmin");
		ut.setPassword("tadmin");

		Credential credential = new Credential();
		credential.setUsernametoken(ut);

		RequestData data = new RequestData();

		handler.validate(credential, data);

	}
	
	@Test
	public void testPropertyFileCallbackHandlerEmptyPassword() throws Exception {

		PropertyFileCallbackHandler handler = new PropertyFileCallbackHandler();

		Document doc = getDocument();

		UsernameToken ut = new UsernameToken(true, doc, WSS4JConstants.PASSWORD_TEXT);
		ut.setName("tadmin");
		ut.setPassword("");

		Credential credential = new Credential();
		credential.setUsernametoken(ut);

		RequestData data = new RequestData();

		handler.validate(credential, data);

	}

	@Test
	public void testPropertyFileCallbackHandlerHashedPassword() throws Exception {

		PropertyFileCallbackHandler handler = new PropertyFileCallbackHandler();

		Document doc = getDocument();

		UsernameToken ut = new UsernameToken(true, doc, WSS4JConstants.PASSWORD_DIGEST);
		ut.setName("tadmin");
		ut.setPassword("tadmin");

		Credential credential = new Credential();
		credential.setUsernametoken(ut);

		RequestData data = new RequestData();

		try {
			handler.validate(credential, data);
		} catch (WSSecurityException ex) {
			return;
		}

		Assert.fail("Expected WSSecurityException is not thrown ");
	}

	@Test
	public void testPropertyFileCallbackHandlerIncorrectPasswordType() throws Exception {

		PropertyFileCallbackHandler handler = new PropertyFileCallbackHandler();

		Document doc = getDocument();

		UsernameToken ut = new UsernameToken(true, doc, "incorrectPasswordType");
		ut.setName("tadmin");
		ut.setPassword("tadmin");

		Credential credential = new Credential();
		credential.setUsernametoken(ut);

		RequestData data = new RequestData();

		try {
			handler.validate(credential, data);
		} catch (WSSecurityException ex) {
			return;
		}

		Assert.fail("Expected WSSecurityException is not thrown ");
	}

	@Test
	public void testPropertyFileCallbackHandlerNullUser() throws Exception {

		PropertyFileCallbackHandler handler = new PropertyFileCallbackHandler();

		Document doc = getDocument();

		UsernameToken ut = new UsernameToken(true, doc, WSS4JConstants.PASSWORD_TEXT);
		ut.setName(null);
		ut.setPassword("tadmin");

		Credential credential = new Credential();
		credential.setUsernametoken(ut);

		RequestData data = new RequestData();

		try {
			handler.validate(credential, data);
		} catch (WSSecurityException ex) {
			return;
		}

		Assert.fail("Expected WSSecurityException is not thrown ");
	}

	@Test
	public void testPropertyFileCallbackHandlerNoCredentials() throws Exception {

		PropertyFileCallbackHandler handler = new PropertyFileCallbackHandler();

		Credential credential = new Credential();

		RequestData data = new RequestData();

		try {
			handler.validate(credential, data);
		} catch (WSSecurityException ex) {
			return;
		}

		Assert.fail("Expected WSSecurityException is not thrown");

	}

	private Document getDocument() {
		final String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
				+ "<Emp id=\"1\"><name>Pankaj</name><age>25</age>\n" + "<role>Developer</role><gen>Male</gen></Emp>";

		return convertStringToDocument(xmlStr);
	}

	private static Document convertStringToDocument(String xmlStr) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(xmlStr)));
			return doc;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}