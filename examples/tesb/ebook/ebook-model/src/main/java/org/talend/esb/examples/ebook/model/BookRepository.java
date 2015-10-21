package org.talend.esb.examples.ebook.model;

import java.util.List;

public interface BookRepository {
    void add(Book book);
    Book getBook(String id);
    List<Book> getBooks();
    void delete(String id);
    void update(Book book);
}
