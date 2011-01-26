package org.talend.esb.sample.cxf;

import javax.jws.WebService;

@WebService(targetNamespace = "http://talend.org/esb/examples/", name = "Greeter")
public interface Greeter {
	String greetMe(String requestType);
}
