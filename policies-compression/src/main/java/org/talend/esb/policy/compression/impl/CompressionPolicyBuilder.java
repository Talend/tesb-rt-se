package org.talend.esb.policy.compression.impl;

import javax.xml.namespace.QName;

import org.apache.neethi.Assertion;
import org.apache.neethi.AssertionBuilderFactory;
import org.apache.neethi.builders.AssertionBuilder;
import org.w3c.dom.Element;

/**
 * The Class CompressionPolicyBuilder.
 */
public class CompressionPolicyBuilder implements AssertionBuilder<Element> {
	
	/** The Constant NAMESPACE. */
	public static final String NAMESPACE = "http://types.talend.com/policy/assertion/1.0";

	/** The Constant COMPRESSION_NAME. */
	public static final String COMPRESSION_NAME = "Compression";

	/** The Constant COMPRESSION. */
	public static final QName COMPRESSION = new QName(NAMESPACE, COMPRESSION_NAME);

	/* (non-Javadoc)
	 * @see org.apache.neethi.builders.AssertionBuilder#build(java.lang.Object, org.apache.neethi.AssertionBuilderFactory)
	 */
	@Override
	public Assertion build(Element element, AssertionBuilderFactory factory)
			throws IllegalArgumentException {
        return new CompressionAssertion(element);
	}

	/* (non-Javadoc)
	 * @see org.apache.neethi.builders.AssertionBuilder#getKnownElements()
	 */
	@Override
	public QName[] getKnownElements() {
		return new QName[]{COMPRESSION};
	}
}
