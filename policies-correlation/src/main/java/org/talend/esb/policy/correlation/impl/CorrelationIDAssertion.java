package org.talend.esb.policy.correlation.impl;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.neethi.Assertion;
import org.apache.neethi.PolicyComponent;
import org.w3c.dom.Element;

public class CorrelationIDAssertion implements Assertion {

	public enum MethodType {
		CALLBACK,
		XPATH;
	}

	

	//by default only validate the incoming request
	private String value = "";
	//by default use xpath
	private MethodType methodType = MethodType.CALLBACK;

	public CorrelationIDAssertion(Element element) {
        if (element.hasAttributeNS(null, "method")) {
            String method = element.getAttributeNS(null, "method");
            methodType = MethodType.valueOf(method.toUpperCase());
        }

        if (element.hasAttributeNS(null, "value")) {
            value = element.getAttributeNS(null, "value");      
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
		return CorrelationIDPolicyBuilder.CORRELATION_ID_SCHEMA;
	}

	@Override
	public boolean isOptional() {
		return false;
	}

	@Override
	public boolean isIgnorable() {
		return false;
	}

	@Override
	public void serialize(XMLStreamWriter writer) throws XMLStreamException {
		String prefix = writer.getPrefix(CorrelationIDPolicyBuilder.NAMESPACE);

        if (prefix == null) {
            prefix = "tpa";
            writer.setPrefix(prefix, CorrelationIDPolicyBuilder.NAMESPACE);
        }

        // <tpa:CorrelationID>
        writer.writeStartElement(prefix, CorrelationIDPolicyBuilder.CORRELATION_ID_SCHEMA_NAME, 
        		CorrelationIDPolicyBuilder.NAMESPACE);

        // xmlns:tpa="http://types.talend.com/policy/assertion/1.0"
        writer.writeNamespace(prefix, CorrelationIDPolicyBuilder.NAMESPACE);

        // attributes
        writer.writeAttribute(null, "method", methodType.name());
        writer.writeAttribute(null, "value", value);

        // </tpa:SchemaValidation>
        writer.writeEndElement();
	}

	@Override
	public PolicyComponent normalize() {
		return this;
	}

	public String getValue() {
		return value;
	}

	public MethodType getMethodType() {
		return methodType;
	}



}
