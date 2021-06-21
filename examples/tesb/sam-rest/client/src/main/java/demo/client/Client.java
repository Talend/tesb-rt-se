/*
 * #%L
 * TESB :: Examples :: SAM Rest Client
 * %%
 * Copyright (c) 2006-2021 Talend Inc. - www.talend.com
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

package demo.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import demo.common.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Client {

	private static final Logger LOG = Logger.getLogger(Client.class
			.getPackage().getName());

	public static void main(String[] args) throws Exception {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "META-INF/client.xml" });
		OrderService client = (OrderService) context.getBean("restClient");
		String orderId = "1";

		for (int i = 0; i < 10; i++) {
			Order ord = client.getOrder(orderId);

			System.out.println("invoaction number:"+i);
			System.out.println("Order description is::"+ord.getDescription());
			if (LOG.isLoggable(Level.INFO)) {
				LOG.log(Level.INFO, ord.getDescription());
			}

			Thread.sleep(2000);
		}
		System.exit(0);

	}

}
