/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package demo.jms_greeter.server;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ServerMain {

    public static void main(String args[]) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("META-INF/spring/server.xml");
        System.out.println("Server ready...");

        Thread.sleep(125 * 60 * 1000);
        System.out.println("Server exiting");
        context.close();
    }

}
