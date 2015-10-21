package org.talend.esb.examples.ebook.importer;

import org.slf4j.Logger;
import org.talend.esb.examples.ebook.model.Book;

public class BookLogger {
    Logger LOG = org.slf4j.LoggerFactory.getLogger(BookLogger.class);
    
    public void log(Book book) {
        LOG.info(book.toString());
    }

}
