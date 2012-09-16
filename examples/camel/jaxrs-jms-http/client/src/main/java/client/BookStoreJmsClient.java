/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package client;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;

import talend.camel.examples.jaxrsjmshttp.common.Book;
import talend.camel.examples.jaxrsjmshttp.common.BookList;
import talend.camel.examples.jaxrsjmshttp.common.BookStore;

/**
 * <p>
 * CXF does not provide a JAX RS client API that can handle JMS transport. So we implement the BookStore
 * interface ourself. So the client code stays the same. Camel helps us by providing methods that make
 * coding the client very easy in most cases.
 * </p>
 */
public class BookStoreJmsClient implements BookStore {

    private static final String JMS_URI = "jms://test.bookStore";
    @EndpointInject
    ProducerTemplate producer;
    
    private Map<String, Object> getRestHeaders(String method, String path) {
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("org.apache.cxf.request.method", method);
        headers.put("org.apache.cxf.request.uri", path);
        return headers;
    }

    @Override
    public Book getBook(Long id) {
        return producer.requestBodyAndHeaders(JMS_URI, null, getRestHeaders("GET", "/" + id), Book.class);
    }

    @Override
    public Response addBook(Book book) {
        return producer.requestBodyAndHeaders(JMS_URI, book, getRestHeaders("POST", "/"), Response.class);
    }

    @Override
    public void oneWayRequest(Book book) throws Exception {
        producer.sendBodyAndHeaders(JMS_URI, book, getRestHeaders("POST", "/"));
    }

    @Override
    public BookList listBooks() {
        return producer.requestBodyAndHeaders(JMS_URI, null, getRestHeaders("GET", "/"), BookList.class);
    }

}
