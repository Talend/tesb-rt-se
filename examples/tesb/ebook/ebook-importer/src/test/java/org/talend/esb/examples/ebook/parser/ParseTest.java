package org.talend.esb.examples.ebook.parser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.junit.Test;
import org.talend.esb.examples.ebook.model.Book;
import org.talend.esb.examples.ebook.model.Format;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class ParseTest {

    @Test
    public void testParse() throws Exception {
        Document doc = readDocument("pg50180.rdf");
        BookParser parser = new BookParser();
        Book book = parser.parse(doc);
        Assert.assertEquals("Maugham, W. Somerset (William Somerset) - Penelope\nA Comedy in Three Acts", book.toString());
        Assert.assertEquals("ebooks/50180", book.getId());
        Assert.assertEquals("Penelope\nA Comedy in Three Acts", book.getTitle());
        Assert.assertEquals("Maugham, W. Somerset (William Somerset)", book.getCreator());
        //Assert.assertEquals(4, book.getSubjects().size());
        //Assert.assertEquals("Justice -- Early works to 1800", book.getSubjects().get(0).getSubject());
        Assert.assertEquals("http://www.gutenberg.org/cache/epub/50180/pg50180.cover.medium.jpg", book.getCover().toString());
        Assert.assertEquals(11, book.getFormats().size());
        Format format = book.getFormats().get(0);
        Assert.assertEquals("http://www.gutenberg.org/ebooks/50180.kindle.images", format.getFile().toString());
        Assert.assertEquals("application/x-mobipocket-ebook", format.getMediaType().toString());
        
    }

    private Document readDocument(String path) throws ParserConfigurationException, SAXException, IOException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(path);
        if (is == null) {
            throw new FileNotFoundException(path);
        }
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        return dbf.newDocumentBuilder().parse(is);
    }

}
