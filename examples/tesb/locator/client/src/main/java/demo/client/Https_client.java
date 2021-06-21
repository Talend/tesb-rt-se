/*
 * #%L
 * Locator Demo Client
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

import org.springframework.context.support.ClassPathXmlApplicationContext;
import demo.common.Greeter_Https;

public class Https_client {

	public static void main(String[] args) throws Exception {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("/https_client.xml");
		Greeter_Https client = context.getBean(Greeter_Https.class);

		String response = null;
		for (int i = 0; i < 10; i++) {
			System.out.println("BEGIN...");

			response = client.greetMe_Https("MyName#" + i);
			System.out.println("Response from the Https service: ");
			System.out.println(response);

			System.out.println("END...");

		}

		Thread.sleep(2000);
		context.close();
		System.exit(0);

	}
}
