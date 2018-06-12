/*
 * #%L
 * Service Activity Monitoring :: Agent
 * %%
 * Copyright (C) 2011 - 2012 Talend Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
