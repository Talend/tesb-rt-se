package demo.service;

import javax.jws.WebService;

import demo.common.Greeter;

@WebService(targetNamespace = "http://talend.org/esb/examples/", serviceName = "GreeterService")
public class GreeterImpl implements Greeter {

	public String greetMe(String me) {
		System.out.println("Executing operation greetMe");
		System.out.println("Message received: " + me + "\n");
		return "Hello " + me;
	}
}
