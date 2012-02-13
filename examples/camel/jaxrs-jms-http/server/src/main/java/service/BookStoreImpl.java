/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package service;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.ProtocolHeaders;

import talend.tif.examples.jaxrsjmshttp.common.Book;
import talend.tif.examples.jaxrsjmshttp.common.BookList;
import talend.tif.examples.jaxrsjmshttp.common.BookListener;
import talend.tif.examples.jaxrsjmshttp.common.BookStore;

/**
 * <p>
 * Implementation of the book store service. This is mainly a pure pojo.
 * One big advantage of that is that you can test it without any container
 * using a simple unit test.
 * </p>
 */
public class BookStoreImpl implements BookStore {

    @javax.ws.rs.core.Context
    private ProtocolHeaders context;
    
    private BookListener bookListener;

    private Map<Long, Book> books = new HashMap<Long, Book>();

    public BookStoreImpl() {
        books.put(123L, new Book("JMS and HTTP Transport in CXF JAX-RS", 123L));
    }

    public Book getBook(Long id) {
        return books.get(id);
    }
    
    public Response addBook(Book book) {
        books.put(book.getId(), book);
        try {
            bookListener.onBook(book);
        } catch (Exception e) {
            // Ignore: should be logged
        }
        return Response.ok(book).build();
    }

    public void oneWayRequest(Book book) throws Exception {
    	printRequestTransport();
    	bookListener.onBook(book);
    }

    private void printRequestTransport() {
        if (context == null) {
            return;
        }
    	if (context.getRequestHeaderValue("JMSMessageType") != null) {
    		System.out.println("It is the JMS Request");
    	} else {
    		System.out.println("It is the HTTP Request");
    	}
    }
    
    public BookList listBooks() {
        BookList bookList = new BookList();
        bookList.setBook((Book[])books.values().toArray(new Book[]{}));
        return bookList;
    }

    public void setBookListener(BookListener bookListener) {
        this.bookListener = bookListener;
    }

}


