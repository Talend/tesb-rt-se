/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package service;

import org.apache.camel.Consume;

import talend.tif.examples.jaxrsjmshttp.common.Book;

/**
 * <p>
 * Shows how to attach to a camel route using pojo messaging.
 * </p>
 * <p>
 * Receives a message from jms, deserializes with 
 * JAXB and sends the Book object to the java method receiveBook.
 * </p>
 */
public class BookReceiver {
    
    @Consume(uri="jms://test.books")
    public void receiveBook(Book book) {
        System.out.println("Received book over jms: " + book);
    }

}
