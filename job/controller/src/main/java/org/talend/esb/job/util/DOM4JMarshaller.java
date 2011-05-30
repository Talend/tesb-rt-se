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
