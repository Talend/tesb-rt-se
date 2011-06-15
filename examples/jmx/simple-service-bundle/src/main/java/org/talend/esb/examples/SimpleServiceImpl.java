
package org.talend.esb.examples;

import javax.jws.WebService;

@WebService
public class SimpleServiceImpl implements SimpleService {

	@Override
	public String sayHi(String name) {

		if (name.equals("Joe"))
			throw new RuntimeException("Incorrect name");

		return "Hi " + name + "!";
	}

	@Override
	public int doubleIt(int arg) {
		return arg * 2;
	}

}
