/**
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */
package demo.secure_greeter.server;

import java.util.logging.Logger;

import com.talend.examples.secure_greeter.SecureGreeterPortType;

@javax.jws.WebService(serviceName = "SecureGreeterService",
                      targetNamespace = "http://talend.com/examples/secure-greeter",
                      endpointInterface = "com.talend.examples.secure_greeter.SecureGreeterPortType", 
                      wsdlLocation = "classpath:/ws-secpol-wsdl/greeter.wsdl")
public class GreeterImpl implements SecureGreeterPortType {

    private static final Logger LOG = Logger.getLogger(GreeterImpl.class.getPackage().getName());

    public String greetMe(String me) {
        LOG.info("Executing operation greetMe");
        System.out.println("Executing operation greetMe");
        System.out.println("Message received: " + me + "\n");
        return "Hello " + me;
    }

}
