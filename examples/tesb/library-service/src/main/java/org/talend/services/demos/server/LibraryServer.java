package org.talend.services.demos.server;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class LibraryServer {
	
	private ClassPathXmlApplicationContext context;

	protected LibraryServer() throws Exception {
        System.out.println("Starting Server");
        context = new ClassPathXmlApplicationContext(new String[] {"cxf-servlet.xml"});
        context.start();
    }

	protected ClassPathXmlApplicationContext getContext() {
		return context;
	}

	public static void main(String args[]) throws Exception {
        final LibraryServer server = new LibraryServer();
        final ClassPathXmlApplicationContext context = server.getContext();
        final LibraryPublisher publisher = (LibraryPublisher) context.getBean("publisher");
        System.out.println("Server ready...");
        publisher.publishNewBooksNotifications();
        Thread.sleep(15 * 60 * 1000);
        System.out.println("Server exiting");
        context.close();
        System.exit(0);
    }

}