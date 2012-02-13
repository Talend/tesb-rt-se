/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package demo.interceptors.server;

import java.util.logging.Logger;

import com.talend.examples.interceptors.Greeter;

@javax.jws.WebService(portName = "GreeterPort", serviceName = "GreeterService", targetNamespace = "http://talend.com/examples/interceptors", endpointInterface = "com.talend.examples.interceptors.Greeter", wsdlLocation = "classpath:/interceptors-wsdl/hello_world.wsdl")
public class GreeterImpl implements Greeter {

    private static final Logger LOG = Logger.getLogger(GreeterImpl.class.getPackage().getName());

    public String greetMe(String me) {
        LOG.info("Executing operation greetMe");
        System.out.println("Executing operation greetMe");
        System.out.println("Message received: " + me + "\n");
        return "Hello " + me;
    }

    public String sayHi() {
        LOG.info("Executing operation sayHi");
        System.out.println("Executing operation sayHi" + "\n");
        return "Bonjour";
    }

    public void greetMeOneWay(String me) {
        LOG.info("Executing operation greetMeOneWay");
        System.out.println("Executing operation greetMeOneWay\n");
        System.out.println("Hello there " + me);
    }
}
