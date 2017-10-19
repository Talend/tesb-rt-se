/*
 * #%L
 * Talend :: ESB :: Job :: Controller
 * %%
 * Copyright (C) 2011 - 2012 Talend Inc.
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
package org.talend.esb.job.controller.internal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.talend.esb.job.controller.ESBEndpointConstants.EsbSecurity;
import org.talend.esb.sam.agent.feature.EventFeature;
import org.talend.esb.servicelocator.cxf.LocatorFeature;
import org.xml.sax.ErrorHandler;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.cxf.Bus;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.headers.Header;
import org.apache.neethi.Policy;
import org.apache.wss4j.common.crypto.Crypto;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import static org.easymock.EasyMock.createNiceMock;

public class RuntimeESBConsumerTest {

	private ClassPathXmlApplicationContext serviceContext;

	private ClassPathXmlApplicationContext startContext(String configFileName) {
		ClassPathXmlApplicationContext context;
		context = new ClassPathXmlApplicationContext(
				new String[] { configFileName });
		context.start();
		return context;
	}

	@Before
	public void startCustomerService() {
		startContext("classpath:conf/libraryService/spring/service-context.xml");
	}

	@Test
	public void noPropertiesSetProvidesEmptyArgumentList() throws Exception {

		QName serviceName = new QName("http://services.talend.org/test/Library/1.0", "LibraryProvider");
		QName portName = new QName("http://services.talend.org/test/Library/1.0", "LibraryHttpPort");
		QName operationName = new QName("http://services.talend.org/test/Library/1.0", "seekBook");
		String publishedEndpointUrl = "local://LibraryHttpPort";
		String wsdlURL = "classpath:/conf/libraryService/Library.wsdl";
		boolean useServiceLocator = false;
		LocatorFeature locatorFeature = null;
		Map<String, String> locatorProps = new HashMap<String, String>();
		EventFeature samFeature = null;
		Map<String, String> samProps = new HashMap<String, String>();
		boolean useServiceRegistry = false;

		EsbSecurity esbSecurity = null;
		Policy policy = null;
		String username = "";
		String password = "";
		String alias = "";
		Map<String, String> clientProperties = new HashMap<String, String>();
		String roleName = "";
		Object securityToken = null;
		Crypto cryptoProvider = null;

		SecurityArguments securityArguments = new SecurityArguments(esbSecurity, policy, username, password, alias,
				clientProperties, roleName, securityToken, cryptoProvider);
		Bus bus = null;
		boolean logging = false;
		List<Header> soapHeaders = new ArrayList<Header>();
		Feature httpHeadersFeature = null;
		boolean enhancedResponse = false;
		Object correlationIDCallbackHandler = null;
		final boolean useGZipCompression = false;

		RuntimeESBConsumer consumer = new RuntimeESBConsumer(serviceName, portName, operationName, publishedEndpointUrl,
				wsdlURL, useServiceLocator, locatorFeature, locatorProps, samFeature, samProps, useServiceRegistry,
				securityArguments, bus, logging, soapHeaders, httpHeadersFeature, enhancedResponse,
				correlationIDCallbackHandler, useGZipCompression);

		String requestString = "<ns2:SearchFor xmlns:ns2=\"http://types.talend.org/test/Library/Common/1.0\" "+
					"xmlns:ns3=\"http://types.talend.org/test/GeneralObjects/ErrorHandling/1.0\">" +
					"<AuthorLastName>Icebear</AuthorLastName><ISBNNumber>123</ISBNNumber></ns2:SearchFor>";

		consumer.invoke(getDocumentFromString(requestString));

	}

	private Document getDocumentFromString(String xmlString) {
		SAXReader reader = new SAXReader();
		reader.setValidation(false);
		reader.setErrorHandler(createNiceMock(ErrorHandler.class));
		
		try {
			return reader.read(new StringReader(xmlString));
		} catch (DocumentException e) {
			return null;
		}
	}
	
	@After
	public void closeContextAfterEachTest() {

		if (serviceContext != null) {
			serviceContext.stop();
			serviceContext.close();
			serviceContext = null;
		}
	}
}