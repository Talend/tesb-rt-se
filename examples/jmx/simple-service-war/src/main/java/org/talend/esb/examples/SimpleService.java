
package org.talend.esb.examples;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface SimpleService {

	@WebMethod
	String sayHi(String name);

	@WebMethod
	int doubleIt(int arg);
}
