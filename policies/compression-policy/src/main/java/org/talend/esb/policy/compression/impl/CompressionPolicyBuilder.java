package org.talend.esb.policy.compression.impl;

import java.io.IOException;
import java.util.Collection;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.cxf.message.Message;
import org.apache.cxf.ws.policy.AssertionInfo;
import org.apache.cxf.ws.policy.AssertionInfoMap;
import org.apache.neethi.Assertion;
import org.apache.neethi.AssertionBuilderFactory;
import org.apache.neethi.builders.AssertionBuilder;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

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
	
	/**
	 * Gets the assertion.
	 *
	 * @param message the message
	 * @return the assertion
	 * @throws SAXException the sAX exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException the parser configuration exception
	 */
	public static AssertionInfo getAssertion(Message message)
			throws SAXException, IOException, ParserConfigurationException {
		AssertionInfoMap aim = message.get(AssertionInfoMap.class);
		
		if (aim != null) {
			Collection<AssertionInfo> ais = aim
					.get(CompressionPolicyBuilder.COMPRESSION);

			if (ais == null) {
				return null;
			}

			for (AssertionInfo ai : ais) {
				if (ai.getAssertion() instanceof CompressionAssertion) {
					return ai;
				}
			}
		}

		return null;
	}
}
