package org.talend.services.demos.client;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class LibraryClient {

	private LibraryClient() {
		super();
	}

	public static void main(String args[]) throws Exception {
        ClassPathXmlApplicationContext context 
        = new ClassPathXmlApplicationContext(new String[] {"client-applicationContext.xml"});

        LibraryTester client = (LibraryTester)context.getBean("tester");

        client.testLibrary();
        context.close();
        System.exit(0); 
    }

}
