/**
 * Copyright (C) 2010 - 2011 Talend Inc. - www.talend.com
 */
package client;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import talend.camel.examples.jaxrsjmshttp.common.BookStore;


/**
 * <p>
 * Starter for the book store client.
 * </p>
 * <p>
 * Shows how to run the book store client using a proxy to the service that calls
 * the service using both JMS and HTTP.
 * </p>
 */
public class BookStoreClientMain {
    
    public static void main (String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("client.xml");
        BookStoreClient client = new BookStoreClient();
        BookStore bookStoreJmsClient = context.getBean("bookStoreJmsClient", BookStore.class);
        client.useBookStore(bookStoreJmsClient);
        
        final int defaultHttpPort = 9002; 
        String systemHttpPort = System.getProperty("http.port"); 
        int httpPort = systemHttpPort == null ? 9002 : Integer.valueOf(systemHttpPort); 

         
        String address = "http://localhost:" + httpPort + "/bookstore";
        System.out.println("Invoking HTTP service at " + address);
        BookStore bookStoreHttp = 
            JAXRSClientFactory.create(address, BookStore.class);
        client.useBookStore(bookStoreHttp);
        context.close();

        // TODO Implement with HTTP over Camel transport using direct component
        // "camel://direct:bookStoreHttp"        
    }
}
