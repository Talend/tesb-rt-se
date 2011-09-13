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
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Client {

	private static final Logger LOG = Logger
	.getLogger(Client.class.getPackage().getName());

	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "META-INF/client.xml" });
		OrderService client = (OrderService) context.getBean("restClient");
		Order ord = client.getOrder("1");
		
		System.out.println(ord.getDescription());
		if (LOG.isLoggable(Level.INFO)) {
			LOG.log(Level.INFO, ord.getDescription());
		}
		System.exit(0);

	}

}