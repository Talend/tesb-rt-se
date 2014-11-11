package org.talend.esb.policy.compression.impl;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.neethi.Assertion;
import org.apache.neethi.PolicyComponent;
import org.talend.esb.policy.compression.impl.internal.CompressionConstants;
import org.w3c.dom.Element;

/**
 * The Class CompressionAssertion.
 */
public class CompressionAssertion implements Assertion {


	/** The treshold attribute name. */
	private static String  TRESHOLD_ATTRIBUTE_NAME = "threshold";
	
    /**
     * The compression threshold to pass to the outgoing interceptor.
     */
    private int threshold = CompressionConstants.TRESHOLD_ATTRIBUTE_DEFAULT;
    
	/**
	 * Instantiates a new compression assertion.
	 *
	 * @param element the element
	 */
	public CompressionAssertion(Element element) {
      
        if (element.hasAttributeNS(null, TRESHOLD_ATTRIBUTE_NAME)) {
        	String attribute = element.getAttributeNS(null, TRESHOLD_ATTRIBUTE_NAME);
        	threshold = Integer.parseInt(attribute);
        }        
	}

	/* (non-Javadoc)
	 * @see org.apache.neethi.PolicyComponent#getType()
	 */
	@Override
	public short getType() {
		return org.apache.neethi.Constants.TYPE_ASSERTION;
	}

	/* (non-Javadoc)
	 * @see org.apache.neethi.PolicyComponent#equal(org.apache.neethi.PolicyComponent)
	 */
	@Override
	public boolean equal(PolicyComponent policyComponent) {
        return policyComponent == this;
	}

	/* (non-Javadoc)
	 * @see org.apache.neethi.Assertion#getName()
	 */
	@Override
	public QName getName() {
		return CompressionPolicyBuilder.COMPRESSION;
	}

	/* (non-Javadoc)
	 * @see org.apache.neethi.Assertion#isOptional()
	 */
	@Override
	public boolean isOptional() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.apache.neethi.Assertion#isIgnorable()
	 */
	@Override
	public boolean isIgnorable() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.apache.neethi.Assertion#serialize(javax.xml.stream.XMLStreamWriter)
	 */
	@Override
	public void serialize(XMLStreamWriter writer) throws XMLStreamException {
		String prefix = writer.getPrefix(CompressionPolicyBuilder.NAMESPACE);

        if (prefix == null) {
            prefix = "tpa";
            writer.setPrefix(prefix, CompressionPolicyBuilder.NAMESPACE);
        }

        // <tpa:Compression>
        writer.writeStartElement(prefix, CompressionPolicyBuilder.COMPRESSION_NAME, 
        		CompressionPolicyBuilder.NAMESPACE);

        // xmlns:tpa="http://types.talend.com/policy/assertion/1.0"
        writer.writeNamespace(prefix, CompressionPolicyBuilder.NAMESPACE);

        // attributes
        writer.writeAttribute(null, TRESHOLD_ATTRIBUTE_NAME, String.valueOf(threshold));

        // </tpa:Compression>
        writer.writeEndElement();
	}

	/* (non-Javadoc)
	 * @see org.apache.neethi.Assertion#normalize()
	 */
	@Override
	public PolicyComponent normalize() {
		return this;
	}

	/**
	 * Gets the threshold.
	 *
	 * @return the threshold
	 */
	public int getThreshold() {
		return threshold;
	}

}
