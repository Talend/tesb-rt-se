/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package talend.camel.examples.jaxrsjmshttp.common;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(namespace = "http://books", name = "BookList")
@XmlType
public class BookList {

    Book[] book;

    public Book[] getBook() {
        return book;
    }

    public void setBook(Book[] book) {
        this.book = book;
    }
}
