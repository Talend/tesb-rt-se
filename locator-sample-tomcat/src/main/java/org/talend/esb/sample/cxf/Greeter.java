package org.talend.esb.sample.cxf;

import java.net.SocketException;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
 
@WebService(name = "Greeter", targetNamespace = "http://talend.org/esb/examples/")
public interface Greeter {
    
	@WebMethod(operationName = "greetMe", action = "urn:GreetMe")
	String greetMe(@WebParam(name = "arg0") String requestType);    
}
