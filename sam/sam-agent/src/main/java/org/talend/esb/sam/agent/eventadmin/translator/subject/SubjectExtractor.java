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
