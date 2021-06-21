/*
 * #%L
 * Talend :: ESB :: Job :: Controller
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
package org.talend.esb.job.controller.internal.util;

import java.io.StringReader;
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.dom4j.DocumentException;

public final class DOM4JMarshaller {

    private static final Logger LOG = Logger.getLogger(DOM4JMarshaller.class.getName());
    private static TransformerFactory FACTORY;

    private DOM4JMarshaller() {
    }

    private static TransformerFactory getTransformerFactory() {
        if (null == FACTORY) {
            FACTORY = TransformerFactory.newInstance();
            try {
                FACTORY.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            } catch (IllegalArgumentException ex) {
                LOG.fine("Property XMLConstants.ACCESS_EXTERNAL_DTD is not recognized");
            }
            try {
                FACTORY.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
            } catch (IllegalArgumentException ex) {
                LOG.fine("Property XMLConstants.ACCESS_EXTERNAL_STYLESHEET is not recognized");
            }
            try {
                FACTORY.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            } catch (TransformerConfigurationException e) {
                throw new RuntimeException("Error setting the secure processing feature", e);
            }
        }
        return FACTORY;
    }

    public static org.dom4j.Document sourceToDocument(Source source)
        throws TransformerException, DocumentException {

        // org.dom4j.io.DocumentResult docResult = new
        // org.dom4j.io.DocumentResult();
        // FACTORY.newTransformer().transform(source, docResult);
        // return docResult.getDocument();
        //

        // fix for unsupported xmlns="" declaration processing over dom4j
        // implementation
        // // old version:
        // // org.dom4j.io.DocumentResult docResult = new
        // org.dom4j.io.DocumentResult();
        // // factory.newTransformer().transform(request, docResult);
        // // org.dom4j.Document requestDoc = docResult.getDocument();
        // new version:
        java.io.ByteArrayOutputStream os = new java.io.ByteArrayOutputStream();
        getTransformerFactory().newTransformer().transform(source,
                                                           new javax.xml.transform.stream.StreamResult(os));
        return new org.dom4j.io.SAXReader()
            .read(new java.io.ByteArrayInputStream(os.toByteArray()));
        // end of fix

    }

    public static Source documentToSource(org.dom4j.Document document) throws DocumentException {
        return new StreamSource(new StringReader(document.asXML()));
    }

}
