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

import org.springframework.context.support.ClassPathXmlApplicationContext;
import demo.common.Greeter;

public class Client {

	public static void main(String[] args) throws Exception {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("/client.xml");
		Greeter client = (Greeter) context.getBean("greeterService");

		String response = null;
		for (int i = 0; i < 10; i++) {
			System.out.println("BEGIN...");

			response = client.greetMe("MyName#" + i);
			System.out.println("Response from the service: ");
			System.out.println(response);
			
			System.out.println("END...");

		}

		Thread.sleep(2000);
		context.close();
		System.exit(0); 

	}
}
