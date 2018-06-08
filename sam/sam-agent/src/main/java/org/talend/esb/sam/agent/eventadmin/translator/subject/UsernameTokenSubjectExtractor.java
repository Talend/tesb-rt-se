package org.talend.esb.sam.agent.eventadmin.translator.subject;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class UsernameTokenSubjectExtractor extends AbstractSubjectExtractorHandler {
    
    public static final String UT_NAMESPACE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
    
    public static final String UT_SUBJECT_TAG = "Username";

    public static final String SOAP_NAMESPACE = "http://schemas.xmlsoap.org/soap/envelope/";
    
    public static final String SOAP_HEADER_TAG = "Header";
    
    private final StringBuilder answer = new StringBuilder();
    
    private boolean inSubjectTag = false;
    
    private boolean inSoapHeader = true;
    
    @Override
    public String getSubject() {
        return answer.toString();
    }
    
    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
        if (isSoapHeaderTag(uri, localName)) {
            inSoapHeader = true;
        } else if (isUTSubjectTag(uri, localName)) {
            inSubjectTag = true;
        }
    }
    
    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (isSoapHeaderTag(uri, localName)) {
            if (!inSoapHeader) {
                throw new IllegalStateException("Missed startElement for " + SOAP_HEADER_TAG);
            }
            inSoapHeader = false;
        } else if (isUTSubjectTag(uri, localName)) {
            if (!inSubjectTag) {
                throw new IllegalStateException("Missed startElement for " + UT_SUBJECT_TAG);
            }
            inSubjectTag = false;
            throw new SubjectFoundException();
        }
    }
    
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        if (inSoapHeader && inSubjectTag) {
            answer.append(ch, start, length);
        }
    }
    
    private final boolean isUTSubjectTag(String uri, String localName) {
        return UT_SUBJECT_TAG.equals(localName) && UT_NAMESPACE.equals(uri);
    }
    
    private final boolean isSoapHeaderTag(String uri, String localName) {
        return SOAP_HEADER_TAG.equals(localName) && SOAP_NAMESPACE.equals(uri);
    }
}
