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

import java.util.logging.Level;
import java.util.logging.Logger;

import demo.common.*;

import org.apache.cxf.jaxrs.client.WebClient;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Client {

	private static final Logger LOG = Logger.getLogger(Client.class
			.getPackage().getName());

	private static void usingClientReset(ClassPathXmlApplicationContext context) {
		OrderService proxy = (OrderService) context.getBean("restClient");
		org.apache.cxf.jaxrs.client.Client client = WebClient.client(proxy);
		for (int i = 0; i < 5; i++) {
			Order ord = proxy.getOrder("1");
			client.reset();
			System.out.println(ord.getDescription());
		}
	}

	private static void standardClient(ClassPathXmlApplicationContext context) {
		OrderService proxy = (OrderService) context.getBean("restClient");
		for (int i = 0; i < 5; i++) {
			Order ord = proxy.getOrder("1");
			System.out.println(ord.getDescription());
		}

	}

	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "META-INF/client.xml" });
		// Use client reset after each invoke
		try {
			usingClientReset(context);
			System.out.println("\nNo exceptions during reset usecase - OK\n");
		} catch (Exception e) {
			System.out.println("Exception during reset usecase\n");
			e.printStackTrace();
		}
		// Use client proxy as usual
		try {
			standardClient(context);
			System.out.println("\nNo exceptions during usual usecase - OK\n");
		} catch (Exception e) {
			System.out.println("Exception during usual usecase\n");
			e.printStackTrace();
		}
		System.exit(0);
	}

}