package org.talend.esb.examples;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public final class SimpleClient {

	public SimpleClient() {
	}

	public static void main(String[] args) throws Exception {

		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "META-INF/spring/client-bean.xml" });

		SimpleService client = (SimpleService) context.getBean("simpleClient");

		for (int i = 0; i < 100; i++) {
			String response = client.sayHi("Alex");
			System.out.println(response);
		}

		for (int i = 1; i < 6; i++) {
			int result = client.doubleIt(i);
			System.out.println(result);
		}

		// "Incorrect name" exception would be thrown
		String response = client.sayHi("Joe");
		System.out.println(response);

		System.exit(0);
	}

}
