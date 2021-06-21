/*
 * #%L
 * Service Locator Client for CXF
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
package org.talend.esb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * The <code>DomMother</code> class provides a fabrication plant for general DOM trees.
 */
public final class DomMother {

    public static final DocumentBuilder DOC_BUILDER;

    public static final DOMImplementationLS DOM_IMPL_LS;

    private static LSSerializer serializer;

    static {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DOC_BUILDER = factory.newDocumentBuilder();

            DOMImplementation domImpl = DOC_BUILDER.getDOMImplementation();
            DOM_IMPL_LS = (DOMImplementationLS) domImpl.getFeature("LS", "3.0");
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private DomMother() {
    }

    public static LSSerializer getSerializer() {
        if (serializer == null) {
            serializer = DOM_IMPL_LS.createLSSerializer();

        }
        return serializer;
    }

    public static LSOutput getOuput() {
        return DOM_IMPL_LS.createLSOutput();
    }

    public static Document newRawDocument() {
        return DOC_BUILDER.newDocument();
    }

    public static Element newDocument(String rootTag) {
        Document document = newRawDocument();
        return addElement(document, document, null, rootTag);
    }

    public static Element newDocument(String namespace, String rootTag) {
        Document document = newRawDocument();
        return addElement(document, document, namespace, rootTag);
    }

    public static Element addElement(Node parent, String namespace, String tag) {
        return addElement(parent, null, namespace, tag);
    }

    public static Element addElement(Node parent, String prefix, String namespace, String tag) {
        Element result;
        Document doc = null;
        if (Node.DOCUMENT_NODE == parent.getNodeType()) {
            doc = (Document) parent;
        } else if (Node.ELEMENT_NODE == parent.getNodeType()) {
            doc = parent.getOwnerDocument();
        } else {
            throw new RuntimeException("Parent node must either be of type Document or Element.");
        }
        result = addElement(doc, parent, namespace, tag);
        if (prefix != null) {
            result.setPrefix(prefix);
        }
        return result;
    }

    private static Element addElement(Document owner, Node parent, String namespace, String tag) {
        Element child = null;
        if (namespace != null) {
            child = owner.createElementNS(namespace, tag);
        } else {
            child = owner.createElement(tag);
        }
        parent.appendChild(child);
        return child;
    }

    public static Element addLeafElement(Element parent, String namespace, String tag, String content) {
        Element newElement = addElement(parent, namespace, tag);
        DomMother.addText(newElement, content);

        return newElement;
    }

    public static Attr addAttribute(Element parent, String attributeName, String attributeValue) {
        return DomMother.addAttribute(parent, null, attributeName, attributeValue);
    }

    public static Attr addAttribute(Element parent, String namespace, String attributeName,
            String attributeValue) {
        return addAttribute(parent, null, namespace, attributeName, attributeValue);
    }

    public static Attr addAttribute(Element parent, String prefix, String namespace, String attributeName,
            String attributeValue) {
        if (attributeValue == null) {
            return null;
        }

        Document doc = parent.getOwnerDocument();
        Attr child = (namespace != null) ? doc.createAttributeNS(namespace, attributeName) : doc
                .createAttribute(attributeName);

        child.setValue(attributeValue);
        parent.setAttributeNode(child);

        if (prefix != null) {
            child.setPrefix(prefix);
        }
        return child;
    }

    public static Text addText(Element parent, String text) {
        Document doc = parent.getOwnerDocument();
        Text textNode = doc.createTextNode(text);
        parent.appendChild(textNode);

        return textNode;
    }

    public static Document parse(String xml) {
        try {
            return DOC_BUILDER.parse(new InputSource(new StringReader(xml)));
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Document parse(InputStream xml) {
        try {
            return DOC_BUILDER.parse(new InputSource(xml));
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Document parse(byte[] xml) {
        ByteArrayInputStream xmlStream = new ByteArrayInputStream(xml);
        return parse(xmlStream);
    }

    public static void serialize(Node xml, OutputStream out) {
        LSOutput ouput = getOuput();
        ouput.setEncoding("utf-8");
        ouput.setByteStream(out);

        getSerializer().write(xml, ouput);
    }

    public static String serializeString(Node xml) {
        return getSerializer().writeToString(xml);
    }

    public static InputStream serializeStream(Node xml) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(10000);
        serialize(xml, outputStream);
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    public static void addAtributes(Element parent, Map<String, String> attributes) {
        for (Map.Entry<String, String> attribute : attributes.entrySet()) {
            addAttribute(parent, attribute.getKey(), attribute.getValue());
        }
    }

    public static Document clone(Document doc) {
        Document copy = newRawDocument();
        Element rootElement = (Element) copy.importNode(doc.getDocumentElement(), true);
        copy.appendChild(rootElement);

        return copy;
    }

    public static Document clone(Element element) {
        Document copy = newRawDocument();
        Element rootElement = (Element) copy.importNode(element, true);
        copy.appendChild(rootElement);

        return copy;
    }
}
