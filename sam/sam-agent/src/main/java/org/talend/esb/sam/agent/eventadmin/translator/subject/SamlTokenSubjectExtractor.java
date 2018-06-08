package org.talend.esb.sam.agent.eventadmin.translator.subject;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class SamlTokenSubjectExtractor extends AbstractSubjectExtractorHandler {
    
    public static final String SAML2_NAMESPACE = "urn:oasis:names:tc:SAML:2.0:assertion";
    
    public static final String SAML2_SUBJECT_TAG = "NameID";

    public static final String SOAP_NAMESPACE = "http://schemas.xmlsoap.org/soap/envelope/";
    
    public static final String SOAP_HEADER_TAG = "Header";
    
    private final StringBuilder answer = new StringBuilder();
    
    private boolean inSubjectTag = false;
    
    private boolean inSoapHeader = true;
    
    public String getSubject() {
        return answer.toString();
    }
    
    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
        if (isSoapHeaderTag(uri, localName)) {
            inSoapHeader = true;
        } else if (isSamlSubjectTag(uri, localName)) {
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
        } else if (isSamlSubjectTag(uri, localName)) {
            if (!inSubjectTag) {
                throw new IllegalStateException("Missed startElement for " + SAML2_SUBJECT_TAG);
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
    
    private final boolean isSamlSubjectTag(String uri, String localName) {
        return SAML2_SUBJECT_TAG.equals(localName) && SAML2_NAMESPACE.equals(uri);
    }
    
    private final boolean isSoapHeaderTag(String uri, String localName) {
        return SOAP_HEADER_TAG.equals(localName) && SOAP_NAMESPACE.equals(uri);
    }
}
