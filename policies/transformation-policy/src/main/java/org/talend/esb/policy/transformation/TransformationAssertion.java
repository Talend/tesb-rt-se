package org.talend.esb.policy.transformation;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.neethi.Assertion;
import org.apache.neethi.PolicyComponent;
import org.w3c.dom.Element;

public class TransformationAssertion implements Assertion {
	private static final String NS_PREFIX = "tpa";
	private static final String TYPE_NAME = "type";
	private static final String IN_XSLT_PATH_ATTRIBUTE_NAME = "inXSLTPath";
	private static final String OUT_XSLT_PATH_ATTRIBUTE_NAME = "outXSLTPath";
	
	private final TransformationType transformationType;
	private final String inXSLTPath;
	private final String outXSLTPath;

	public TransformationAssertion(Element element) {      
      	String sType = element.getAttribute(TYPE_NAME);
      	if ((sType == null) || (sType.isEmpty())) {
      		transformationType = TransformationType.xslt;
      	} else {
      		transformationType = TransformationType.valueOf(sType);
      	}
      	inXSLTPath = element.getAttribute(IN_XSLT_PATH_ATTRIBUTE_NAME);
      	outXSLTPath = element.getAttribute(OUT_XSLT_PATH_ATTRIBUTE_NAME);
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
		return TransformationPolicyBuilder.TRANSFORMATION;
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
		String prefix = writer.getPrefix(TransformationPolicyBuilder.NAMESPACE);

        if (prefix == null) {
            prefix = NS_PREFIX;
            writer.setPrefix(prefix, TransformationPolicyBuilder.NAMESPACE);
        }

        // <tpa:Transformation>
        writer.writeStartElement(prefix, TransformationPolicyBuilder.TRANSFORMATION_NAME, 
        		TransformationPolicyBuilder.NAMESPACE);

        // xmlns:tpa="http://types.talend.com/policy/assertion/1.0"
        writer.writeNamespace(prefix, TransformationPolicyBuilder.NAMESPACE);

        // attributes
        writer.writeAttribute(null, IN_XSLT_PATH_ATTRIBUTE_NAME, String.valueOf(inXSLTPath));
        writer.writeAttribute(null, OUT_XSLT_PATH_ATTRIBUTE_NAME, String.valueOf(outXSLTPath));

        // </tpa:Transformation>
        writer.writeEndElement();
	}

	@Override
	public PolicyComponent normalize() {
		return this;
	}

	public TransformationType getTransformationType() {
		return transformationType;
	}

	public String getInXSLTPath() {
		return inXSLTPath;
	}

	public String getOutXSLTPath() {
		return outXSLTPath;
	}


}
