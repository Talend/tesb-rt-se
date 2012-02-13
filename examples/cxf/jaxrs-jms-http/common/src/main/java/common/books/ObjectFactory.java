/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */

package common.books;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {

    private final static QName _Book_QNAME = new QName("http://books", "Book");
    
    /**
     * Create a new ObjectFactory that can be used to create new instances of
     * schema derived classes for package: common
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Book }
     */
    public Book createBook() {
        return new Book();
    }
    
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}
     * {@link Book }{@code >}
     */
    @XmlElementDecl(namespace = "http://books.com", name = "Book")
    public JAXBElement<Book> createBook(Book value) {
        return new JAXBElement<Book>(_Book_QNAME, Book.class, null, value);
    }
    
}
