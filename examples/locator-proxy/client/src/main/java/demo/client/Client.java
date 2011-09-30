/*
 * #%L
 * Locator Proxy Demo Client
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


import javax.xml.namespace.QName;

import javax.xml.ws.Service;
import javax.xml.ws.soap.AddressingFeature;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.talend.schemas.esb._2011._09.locator.LookupRequestType;
import org.talend.webservices.esb.locator_v1.LocatorServiceV10;

import demo.common.Greeter;

public class Client {

	public static void main(String[] args) throws Exception {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("/META-INF/client.xml");
		LocatorServiceV10 client = (LocatorServiceV10) context.getBean("locatorService");

		LookupRequestType lookupRequestType = new LookupRequestType();
		lookupRequestType.setServiceName(new QName("http://talend.org/esb/examples/", "GreeterService"));
		W3CEndpointReference endpointReference = client.lookupEndpoint(lookupRequestType);
		System.out.println(endpointReference.toString());
		
		javax.xml.ws.Service jaxwsServiceObject = Service.create(new QName("http://talend.org/esb/examples/", "GreeterService"));
		
		Greeter greeterProxy = jaxwsServiceObject.getPort(endpointReference, Greeter.class);
		String reply = greeterProxy.greetMe("HI");
		System.out.println("Server said: " + reply);
		
	}
}
