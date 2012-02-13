/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package demo.jms_greeter.client;

import com.talend.examples.jms_greeter.JMSGreeterPortType;


public final class Client {
    
    JMSGreeterPortType greeter;

    public void setGreeter(JMSGreeterPortType greeter) {
        this.greeter = greeter;
    }

    public void run() {
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

}
