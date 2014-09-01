package org.talend.esb.policy.correlation.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.neethi.Assertion;
import org.apache.neethi.PolicyComponent;
import org.talend.esb.policy.correlation.impl.xpath.XpathNamespace;
import org.talend.esb.policy.correlation.impl.xpath.XpathPart;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CorrelationIDAssertion implements Assertion {

	public enum MethodType {
		CALLBACK,
		XPATH;
	}
	
	public CorrelationIDAssertion(){
		
	}
	
	
	/** The correlation name attribute name. */
	private static String CORRELATION_NAME_ATTRIBUTE_NAME = "name";	
	
	//by default use callback
	private MethodType methodType = MethodType.CALLBACK;
	//correlation name used for xpath
	private String correlationName = null;
	//correlation parts used for xpath
	private List<XpathPart> parts = new ArrayList<XpathPart>();
	//correlation name spaces used for xpath
	private List<XpathNamespace> namespaces = new ArrayList<XpathNamespace>();	
	public CorrelationIDAssertion(Element element) {
        if (element.hasAttributeNS(null, "type")) {
            String type = element.getAttributeNS(null, "type");
            setMethodType(type);
        }
        
        if (element.hasAttributeNS(null, "name")) {
        	setCorrelationName(element.getAttributeNS(null, "name"));
        }        
        
        NodeList partNodes = element.getElementsByTagNameNS(CorrelationIDPolicyBuilder.NAMESPACE,
        		XpathPart.XPATH_PART_NODE_NAME);
        
        NodeList namespaceNodes = element.getElementsByTagNameNS(CorrelationIDPolicyBuilder.NAMESPACE,
        		XpathNamespace.XPATH_NAMESPACE_NODE_NAME);        
        
       
        if(partNodes!=null && partNodes.getLength() > 0){
            for(int partNum = 0 ; partNum < partNodes.getLength(); partNum++){
            	XpathPart part = new XpathPart();
            	Node partNode = partNodes.item(partNum);
            	NamedNodeMap attributes =  partNode.getAttributes();
            	if(attributes!=null){
            		Node name = attributes.getNamedItem(XpathPart.PART_NAME_ATTRIBUTE);
            		if(name != null){
            			part.setName(name.getTextContent());
            		}
            		Node xpath = attributes.getNamedItem(XpathPart.PART_XPATH_ATTRIBUTE);
            		if(xpath != null){
            			part.setXpath(xpath.getTextContent());
            		} 
            		Node optional = attributes.getNamedItem(XpathPart.PART_OPTIONAL_ATTRIBUTE);
            		if(optional != null){
            			part.setOptional(Boolean.parseBoolean(optional.getTextContent()));
            		}            		
            	}
            	parts.add(part);
            }
        }
        
        if(namespaceNodes!=null && namespaceNodes.getLength() > 0){
            for(int namespaceNum = 0 ; namespaceNum < namespaceNodes.getLength(); namespaceNum++){
            	Node namespaceNode = namespaceNodes.item(namespaceNum);
            	NamedNodeMap attributes =  namespaceNode.getAttributes();
            	if(attributes!=null){
            		Node prefix = attributes.getNamedItem(XpathNamespace.PREFIX_ATTRIBUTE);
            		String p = null;
            		if(prefix != null){
            			p = prefix.getTextContent();
            		}
            		Node uri = attributes.getNamedItem(XpathNamespace.URI_ATTRIBUTE);
            		String u = null;
            		if(uri != null){
            			u = uri.getTextContent();
            		} 
            		
            		addNamespace(p, u);
            	}
            }
        }
	}

	@Override
	public short getType() {
		return org.apache.neethi.Constants.TYPE_ASSERTION;
	}

	@Override
	public boolean equal(PolicyComponent policyComponent) {
        return policyComponent == this;
	}

	@Override
	public QName getName() {
		return CorrelationIDPolicyBuilder.CORRELATION_ID;
	}

	@Override
	public boolean isOptional() {
		return false;
	}

	@Override
	public boolean isIgnorable() {
		return false;
	}
	
	public final void setCorrelationName(String correlationName) {
		this.correlationName = correlationName;
	}
	
	public String getCorrelationName(){
		return correlationName;
	}
	
	public List<XpathPart> getCorrelationParts(){
		return parts;
	}
	
	public List<XpathNamespace> getCorrelationNamespaces(){
		return namespaces;
	}
	
	public final void addNamespace(String prefix, String uri){
    	XpathNamespace namespace = new XpathNamespace();
    	namespace.setPrefix(prefix);
    	namespace.setUri(uri);
    	namespaces.add(namespace);
	}
	
	public void addNamespace(XpathNamespace namespace){
		if(namespace!=null){
			namespaces.add(namespace);
		}
    }	
	
	public final void addXpathPart(XpathPart part){
		if(part!=null){
			parts.add(part);
		}
	}	


	@Override
	public void serialize(XMLStreamWriter writer) throws XMLStreamException {
		String prefix = writer.getPrefix(CorrelationIDPolicyBuilder.NAMESPACE);

        if (prefix == null) {
            prefix = "tpa";
            writer.setPrefix(prefix, CorrelationIDPolicyBuilder.NAMESPACE);
        }

        // <tpa:CorrelationID>
        writer.writeStartElement(prefix, CorrelationIDPolicyBuilder.CORRELATION_ID_NAME, 
        		CorrelationIDPolicyBuilder.NAMESPACE);

        // xmlns:tpa="http://types.talend.com/policy/assertion/1.0"
        writer.writeNamespace(prefix, CorrelationIDPolicyBuilder.NAMESPACE);

        // attributes
        writer.writeAttribute(null, "type", methodType.name().toLowerCase());
        
        if(correlationName!=null){
        	writer.writeAttribute(null, CORRELATION_NAME_ATTRIBUTE_NAME, correlationName);
        }
        
        if(parts !=null && !parts.isEmpty()){
        	for (XpathPart part : parts) {
        		// <tpa:Part>
                writer.writeStartElement(prefix, XpathPart.XPATH_PART_NODE_NAME, 
                		CorrelationIDPolicyBuilder.NAMESPACE);

                // xmlns:tpa="http://types.talend.com/policy/assertion/1.0"
                writer.writeNamespace(prefix, CorrelationIDPolicyBuilder.NAMESPACE);
                
                // part attribute name
                writer.writeAttribute(null, XpathPart.PART_NAME_ATTRIBUTE, 
                		part.getName());
                
                // part attribute xpath
                writer.writeAttribute(null, XpathPart.PART_XPATH_ATTRIBUTE, 
                		part.getXpath());
                
                
                // </tpa:Part>
                writer.writeEndElement();
			}
        }
        
        if(namespaces !=null && !namespaces.isEmpty()){
        	for (XpathNamespace namespace : namespaces) {
        		// <tpa:Namespace>
                writer.writeStartElement(prefix, XpathNamespace.XPATH_NAMESPACE_NODE_NAME, 
                		CorrelationIDPolicyBuilder.NAMESPACE);

                // xmlns:tpa="http://types.talend.com/policy/assertion/1.0"
                writer.writeNamespace(prefix, CorrelationIDPolicyBuilder.NAMESPACE);
                
                // name space prefix
                writer.writeAttribute(null, XpathNamespace.PREFIX_ATTRIBUTE, 
                		namespace.getPrefix());
                
                // name space uri
                writer.writeAttribute(null, XpathNamespace.URI_ATTRIBUTE, 
                		namespace.getUri());
                
                // </tpa:Namespace>
                writer.writeEndElement();
			}
        }
        
        // </tpa:SchemaValidation>
        writer.writeEndElement();
	}

	@Override
	public PolicyComponent normalize() {
		return this;
	}



	public MethodType getMethodType() {
		return methodType;
	}

	public final void setMethodType(String type){
		methodType = MethodType.valueOf(type.toUpperCase());
	}
}
