/*
 * #%L
 * Talend :: ESB :: Job :: Controller
 * %%
 * Copyright (C) 2011 Talend Inc.
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

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;

public final class DOM4JMarshaller {

    private static final javax.xml.transform.TransformerFactory FACTORY =
        javax.xml.transform.TransformerFactory.newInstance();

    private DOM4JMarshaller() {
        
    }

    public static org.dom4j.Document sourceToDocument(Source source)
        throws TransformerException {

        org.dom4j.io.DocumentResult docResult = new org.dom4j.io.DocumentResult();
        FACTORY.newTransformer().transform(source, docResult);
        return docResult.getDocument();
    }

    public static Source documentToSource(org.dom4j.Document document) {
        return new org.dom4j.io.DocumentSource(document);
    }

}
