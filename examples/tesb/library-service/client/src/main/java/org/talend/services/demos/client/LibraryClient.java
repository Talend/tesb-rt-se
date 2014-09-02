package org.talend.services.demos.client;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class LibraryClient {

	private LibraryClient() {
		super();
	}

	public static void main(String args[]) throws Exception {

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
        		new String[] {"client-context.xml"});

        // By starting the context consumer-side endpoint, that receives
        // callback messages, is started
        context.start();

        // Here request-callback request to the service provider is performed
        context.getBean("tester");
        
        Thread.sleep(120000);

        context.stop();
        context.close();
        System.exit(0);
    }
}