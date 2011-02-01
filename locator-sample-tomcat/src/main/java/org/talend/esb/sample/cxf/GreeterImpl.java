package org.talend.esb.sample.cxf;

import java.net.SocketException;
import java.util.logging.Logger;

import javax.jws.WebService;

@WebService(targetNamespace = "http://talend.org/esb/examples/", endpointInterface = "org.talend.esb.sample.cxf.Greeter", portName = "GreeterImplPort", serviceName = "GreeterService")
public class GreeterImpl implements Greeter {

        public String greetMe(String me) {
        return "Hello " + me;
    }
}
