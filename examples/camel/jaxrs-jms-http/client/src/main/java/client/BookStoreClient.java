/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package client;

import talend.camel.examples.jaxrsjmshttp.common.Book;
import talend.camel.examples.jaxrsjmshttp.common.BookList;
import talend.camel.examples.jaxrsjmshttp.common.BookStore;

/**
 * <p>
 * Business logic for a client that uses the book store. This class is a pure pojo 
 * without any references to Camel or CXF. So it can be easily tested using a mock or stub service
 * implementation.
 * </p>
 */
public class BookStoreClient {

    public void useBookStore(BookStore bookStore) {
        Book book = bookStore.getBook(123L);
        System.out.println(book);
        bookStore.addBook(new Book("The Lord of the Rings", 22353L));
        Book book2 = bookStore.getBook(22353L);
        System.out.println(book2);
        BookList books = bookStore.listBooks();
        System.out.println(books.getBook());
    }
    
}
