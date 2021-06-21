/*
 * #%L
 * Service Activity Monitoring :: Agent
 * %%
 * Copyright (c) 2006-2021 Talend Inc. - www.talend.com
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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;

public class SubjectExtractor {

    private static final Logger LOG = Logger.getLogger(SubjectExtractor.class.getName());
    private final SAXParserFactory parserFactory = SAXParserFactory.newInstance();

    private final SAXParser parser;

    public SubjectExtractor() {
        parserFactory.setNamespaceAware(true);

        try {
            parserFactory.setFeature(javax.xml.XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.TRUE);
            parserFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", Boolean.TRUE);
        } catch (SAXNotRecognizedException ex) {
            LOG.fine("Property XMLConstants.FEATURE_SECURE_PROCESSING is not recognized");
        } catch (SAXNotSupportedException ex) {
            LOG.fine("Property XMLConstants.FEATURE_SECURE_PROCESSING is not recognized");
        } catch (ParserConfigurationException ex) {
            throw new RuntimeException(ex);
        }

        try {
            parser = parserFactory.newSAXParser();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getSubject(String document, AbstractSubjectExtractorHandler handler) throws Exception {
        if (document == null) {
            LOG.warning("Message content is null, couldn't get the Subject/Principal.");
            return null;
        }

        if (document.trim().isEmpty()) {
            LOG.warning("Message content is empty, couldn't get the Subject/Principal.");
            return null;
        }

        try {
            synchronized (parser) {
                parser.parse(new InputSource(new StringReader(document)), handler);
            }
        } catch (AbstractSubjectExtractorHandler.SubjectFoundException e) {
            // means we found the subject, can return it
            return handler.getSubject();
        } catch (SAXParseException e) {
            LOG.log(Level.WARNING, "Parse message content failed, couldn't get the Subject/Principal.", e);
            return null;
        }

        // subject is not found
        return null;


    }
}
