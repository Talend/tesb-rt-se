package org.talend.esb.policy.correlation.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.neethi.Assertion;
import org.apache.neethi.PolicyComponent;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CorrelationIDAssertion implements Assertion {

	public enum MethodType {
		CALLBACK,
		XPATH;
	}
	
	/** The correlation name attribute name. */
	private static String CORRELATION_NAME_ATTRIBUTE_NAME = "name";	

	//by default use callback
	private MethodType methodType = MethodType.CALLBACK;
	//correlation name used for xpath
	private String correlationName = null;
	//correlation parts used for xpath
	private List<CorrelationIDPart> parts = new ArrayList<CorrelationIDPart>();
	public CorrelationIDAssertion(Element element) {
        if (element.hasAttributeNS(null, "type")) {
            String type = element.getAttributeNS(null, "type");
            methodType = MethodType.valueOf(type.toUpperCase());
        }
        
        if (element.hasAttributeNS(null, "name")) {
        	correlationName = element.getAttributeNS(null, "name");
        }        
        
        NodeList partNodes = element.getElementsByTagNameNS(CorrelationIDPolicyBuilder.NAMESPACE,
        		CorrelationIDPart.CORRELATION_PART_NAME);
        
        if(partNodes.getLength() > 0){
            for(int partNum = 0 ; partNum < partNodes.getLength(); partNum++){
            	CorrelationIDPart part = new CorrelationIDPart();
            	Node partNode = partNodes.item(partNum);
            	NamedNodeMap attributes =  partNode.getAttributes();
            	if(attributes!=null){
            		Node name = attributes.getNamedItem(CorrelationIDPart.PART_NAME_ATTRIBUTE);
            		if(name != null){
            			part.setName(name.getTextContent());
            		}
            		Node xpath = attributes.getNamedItem(CorrelationIDPart.PART_XPATH_ATTRIBUTE);
            		if(xpath != null){
            			part.setXpath(xpath.getTextContent());
            		} 
            		Node optional = attributes.getNamedItem(CorrelationIDPart.PART_OPTIONAL_ATTRIBUTE);
            		if(optional != null){
            			part.setOptional(Boolean.parseBoolean(optional.getTextContent()));
            		}            		
            	}
            	parts.add(part);
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
	
	public String getCorrelationName(){
		return correlationName;
	}
	
	public List<CorrelationIDPart> getCorrelationParts(){
		return parts;
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
        	for (CorrelationIDPart part : parts) {
        		// <tpa:Part>
                writer.writeStartElement(prefix, CorrelationIDPart.CORRELATION_PART_NAME, 
                		CorrelationIDPolicyBuilder.NAMESPACE);
                
                // xmlns:tpa="http://types.talend.com/policy/assertion/1.0"
                writer.writeNamespace(prefix, CorrelationIDPolicyBuilder.NAMESPACE);
                
                // part attribute name
                writer.writeAttribute(null, CorrelationIDPart.PART_NAME_ATTRIBUTE, 
                		part.getName());
                
                // part attribute xpath
                writer.writeAttribute(null, CorrelationIDPart.PART_XPATH_ATTRIBUTE, 
                		part.getXpath());
                
                
                // </tpa:Part>
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



}
