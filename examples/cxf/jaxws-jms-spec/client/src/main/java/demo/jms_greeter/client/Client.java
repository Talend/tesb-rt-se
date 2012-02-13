/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package demo.jms_greeter.client;

import java.net.URL;

import javax.xml.namespace.QName;

import com.talend.examples.jms_greeter.JMSGreeterPortType;
import com.talend.examples.jms_greeter.JMSGreeterService;


public final class Client {

    private static final QName SERVICE_NAME =
        new QName("http://talend.com/examples/jms-greeter", "JMSGreeterService");
    private static final QName PORT_NAME =
        new QName("http://talend.com/examples/jms-greeter", "GreeterPort");

    public Client() throws Exception {
        this(new String[0]);
    }
    public Client(String args[]) throws Exception {

        URL wsdl = null;
        if (args.length == 0) {
            wsdl = Client.class.getResource("/jms-spec-wsdl/jms_greeter.wsdl");
        }

        JMSGreeterService service = new JMSGreeterService(wsdl, SERVICE_NAME);
        JMSGreeterPortType greeter = (JMSGreeterPortType)service.getPort(PORT_NAME, JMSGreeterPortType.class);

        System.out.println("Invoking sayHi...");
        System.out.println("server responded with: " + greeter.sayHi());
        System.out.println();

        System.out.println("Invoking greetMe...");
        System.out.println("server responded with: " + greeter.greetMe(System.getProperty("user.name")));
        System.out.println();

        System.out.println("Invoking greetMeOneWay...");
        greeter.greetMeOneWay(System.getProperty("user.name"));
        System.out.println("No response from server as method is OneWay");
        System.out.println();
    }

    public static void main(String[] args) throws Exception {
        new Client(args);
        System.exit(0);
    }
}
