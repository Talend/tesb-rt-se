package org.talend.esb.sample.cxf;

import java.util.logging.Logger;

public class GreeterImpl implements Greeter {

	private static final Logger LOG = Logger.getLogger(GreeterImpl.class
			.getPackage().getName());

	public String greetMe(String me) {
		LOG.info("Executing operation greetMe");
		System.out.println("Executing operation greetMe");
		System.out.println("Message received: " + me + "\n");
		return "Hello " + me;
	}
}
