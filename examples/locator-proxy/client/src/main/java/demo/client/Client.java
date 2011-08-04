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

import javax.xml.namespace.QName;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.talend.esb.locator.proxy.service.LocatorProxyService;
import demo.common.Greeter;

public class Client {

	public static void main(String[] args) throws Exception {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("/client.xml");
		LocatorProxyService client = (LocatorProxyService) context.getBean("locatorProxyService");

		W3CEndpointReference endpointReference = client.lookupEndpoint(new QName("http://services.talend.org/CRMService", "CRMServiceProvider"));
		System.out.println(endpointReference.toString());
		
		Thread.sleep(2000);
		context.close();
		System.exit(0);

	}
}
