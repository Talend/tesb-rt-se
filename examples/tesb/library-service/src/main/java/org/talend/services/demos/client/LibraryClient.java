package org.talend.services.demos.client;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class LibraryClient {

	private LibraryClient() {
		super();
	}

	public static void main(String args[]) throws Exception {

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
        		new String[] {"httpClient-applicationContext.xml"});

        LibraryTester client = (LibraryTester)context.getBean("tester");

        System.out.println("***************************************************************");
        System.out.println("*** Library Service calls over HTTP ***************************");
        System.out.println("***************************************************************");

        client.testHttp();

        client = null;
        context.close();

        context = new ClassPathXmlApplicationContext(
        		new String[] {"jmsClient-applicationContext.xml"});

        // By starting the context consumer-side endpoint, that receives
        // callback messages, is started
        context.start();

        // Here request-callback request to the service provider is performed
        client = (LibraryTester)context.getBean("tester");

        System.out.println("***************************************************************");
        System.out.println("*** Library Service calls over JMS ****************************");
        System.out.println("***************************************************************");

        client.testJms();
        // wait for the notifications
        Thread.sleep(30000L);
        client = null;

        // Wait for some time to be able to receive
        // callback response from the service-provider
        // Thread.sleep(5 * 60 * 1000);
        Thread.sleep(1000L);

        context.stop();
        context.close();
        System.exit(0);
    }
}