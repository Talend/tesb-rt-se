package demo.client;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import demo.common.Greeter_Https;

public class Https_client {

	public static void main(String[] args) throws Exception {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("/https_client.xml");
		Greeter_Https client = (Greeter_Https) context.getBean("greeterService_Https");

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
