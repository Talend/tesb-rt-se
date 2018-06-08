package org.talend.esb.sam.agent.eventadmin.translator.subject;

import java.io.StringReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

public class SubjectExtractor {
    
    private final SAXParserFactory parserFactory = SAXParserFactory.newInstance();
    
    private final SAXParser parser;
    
    public SubjectExtractor() {
        parserFactory.setNamespaceAware(true);
        
        try {
            parser = parserFactory.newSAXParser();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public String getSubject(String document, AbstractSubjectExtractorHandler handler) throws Exception {
        if (document == null) {
            throw new NullPointerException("Input document cannot be null.");
        }
        
        if (document.trim().isEmpty()) {
            throw new IllegalArgumentException("Input document cannot be empty.");
        }
        
        try {
            synchronized (parser) {
                parser.parse(new InputSource(new StringReader(document)), handler);
            }
        } catch (AbstractSubjectExtractorHandler.SubjectFoundException e) {
            // means we found the subject, can return it
            return handler.getSubject();
        } catch (SAXParseException e) {
            throw new IllegalArgumentException("Cannot parse input document.", e);
        }
        
        // subject is not found
        return null;
    }
}
