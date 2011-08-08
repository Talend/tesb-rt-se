/*
 * #%L
 * Locator Demo Client
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
/*******************************************************************************
 *
 * Copyright (c) 2011 Talend Inc. - www.talend.com
 * All rights reserved.
 *
 * This program and the accompanying materials are made available
 * under the terms of the Apache License v2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/

package demo.client;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.ws.Endpoint;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.Service;
import javax.xml.ws.soap.AddressingFeature;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import javax.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.EndpointReferenceType;
import org.apache.cxf.wsdl.EndpointReferenceUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.format.Printer;
import org.talend.esb.locator.proxy.service.LocatorProxyService;
import org.talend.esb.locator.proxy.service.types.LookupRequestType;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import demo.common.Greeter;

public class Client {

	public static void main(String[] args) throws Exception {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("/client.xml");
		LocatorProxyService client = (LocatorProxyService) context.getBean("locatorProxyService");

		LookupRequestType lookupRequestType = new LookupRequestType();
		lookupRequestType.setServiceName(new QName("http://talend.org/esb/examples/", "GreeterService"));
		W3CEndpointReference endpointReference = client.lookupEndpoint(lookupRequestType);
		System.out.println(endpointReference.toString());
		
		//Printer myPrinterProxy = jaxwsServiceObject.getPort(endpointReference, Printer.class, new AddressingFeature());
		
//		String resource_identifier = EndpointReferenceManager.getReferenceParameterFromMessageContext("");
//		
//		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//        DocumentBuilder builder = factory.newDocumentBuilder();
//        org.w3c.dom.Document doc = builder.parse(new java.io.ByteArrayInputStream(endpointReference.toString().getBytes()));
//        XPathFactory xPathFactory = XPathFactory.newInstance();
//        XPath xpath = xPathFactory.newXPath();
//        XPathExpression expr = xpath.compile("/EndpointReference/Address/text()");
//        Object result = expr.evaluate(doc, XPathConstants.NODE);
//        Node node = (Node) result;
//        String address = node.getNodeValue().trim();
//		Thread.sleep(2000);
//		context.close();
//		System.exit(0);
		
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(Greeter.class);
		factory.setAddress("http://localhost:8080/services/services/GreeterService");
		Greeter greeter = (Greeter) factory.create();

		String reply = greeter.greetMe("HI");
		System.out.println("Server said: " + reply);

	}
}
