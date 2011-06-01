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
package org.talend.esb.job.util;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.dom4j.Document;
import org.dom4j.io.DOMReader;
import org.dom4j.io.DOMWriter;

public class DOM4JMarshaller {

    public static Document sourceToDocument(Source source) throws Exception {

        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        DOMResult result = new DOMResult();
        transformer.transform(source, result);
        DOMReader domReader = new DOMReader();
        return domReader.read((org.w3c.dom.Document)result.getNode());
    }

    public static Source documentToSource(Document document) throws Exception{
        
        DOMWriter domWriter = new DOMWriter();
        org.w3c.dom.Document w3cDocument = domWriter.write(document);
        DOMSource domSource = new DOMSource(w3cDocument);
        return domSource;
    }
    
}
