package demo.service;

import demo.common.Greeter;

public class GreeterImpl implements Greeter {

	public String greetMe(String me) {
		System.out.println("Executing operation greetMe");
		System.out.println("Message received: " + me + "\n");
		return "Hello " + me;
	}
}
