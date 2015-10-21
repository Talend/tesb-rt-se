package org.talend.esb.examples.ebook.parser;

import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.talend.esb.examples.ebook.model.Book;
import org.talend.esb.examples.ebook.model.Format;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class BookParser {
    private XPath xpath;

    public BookParser() throws XPathExpressionException {
        XPathFactory xpathfactory = XPathFactory.newInstance();
        this.xpath = xpathfactory.newXPath();
        NamespaceMap namespaceMapmap = new NamespaceMap();
        namespaceMapmap.add("dcterms", "http://purl.org/dc/terms/");
        namespaceMapmap.add("rdf","http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        namespaceMapmap.add("pgterms", "http://www.gutenberg.org/2009/pgterms/");
        namespaceMapmap.add("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        namespaceMapmap.add("marcrel", "http://id.loc.gov/vocabulary/relators/");
        namespaceMapmap.add("pgterms", "http://www.gutenberg.org/2009/pgterms/");
        namespaceMapmap.add("dcam", "http://purl.org/dc/dcam/");
        namespaceMapmap.add("cc", "http://web.resource.org/cc/");
        xpath.setNamespaceContext(namespaceMapmap);
    }

    public Book parse(Document doc) throws XPathExpressionException, URISyntaxException {
        Book book = new Book();
        String id = xpath.evaluate("//pgterms:ebook/@rdf:about", doc);
        book.setId(id);
        book.setTitle(xpath.evaluate("//dcterms:title/text()", doc));
        book.setCreator(xpath.evaluate("//dcterms:creator//pgterms:name/text()", doc));
        NodeList files = (NodeList)xpath.evaluate("//pgterms:file", doc, XPathConstants.NODESET);
        //NodeList subjects = (NodeList)xpath.evaluate("//dcterms:subject/rdf:Description/rdf:value", doc, XPathConstants.NODESET);
        for (int c = 0; c < files.getLength(); c++) {
            Format format = parseFormat((Element)files.item(c));
            if (format.getMediaType().startsWith("image")) {
                book.setCover(format.getFile());
            }    
            book.getFormats().add(format);
        }
        /*
        for (int c = 0; c < subjects.getLength(); c++) {
            Element subjectEl = (Element)subjects.item(c);
            book.getSubjects().add(new Subject(subjectEl.getTextContent()));
        }
        */
        
        return book;
    }

    private Format parseFormat(Element file) throws XPathExpressionException {
        Format format = new Format();
        String about = xpath.evaluate("@rdf:about", file);
        try {
            format.setFile(new URI(about));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
        format.setExtent(new Integer(xpath.evaluate("dcterms:extent", file)));
        format.setMediaType(xpath.evaluate("dcterms:format/rdf:Description/rdf:value", file));
        format.setModified(xpath.evaluate("dcterms:modified", file));
        return format;
    }


}
