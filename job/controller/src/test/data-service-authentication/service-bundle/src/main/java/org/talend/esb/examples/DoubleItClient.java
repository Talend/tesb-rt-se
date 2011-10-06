package org.talend.esb.examples;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DoubleItClient {

	public static void main(String[] args) throws Exception {

		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"META-INF/spring/client-beans.xml");
		DoubleIt client = (DoubleIt) context.getBean("DoubleItClient");
		int result = client.execute(10);
		
		System.out.println("Result is " + result);
	}

}
