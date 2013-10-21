package org.talend.esb.policy.correlation.impl;

import javax.xml.namespace.QName;

import org.apache.neethi.Assertion;
import org.apache.neethi.AssertionBuilderFactory;
import org.apache.neethi.builders.AssertionBuilder;
import org.w3c.dom.Element;

public class CorrelationIDPolicyBuilder implements AssertionBuilder<Element> {
	public static final String NAMESPACE = "http://types.talend.com/policy/assertion/1.0";

	public static final String CORRELATION_ID_NAME = "CorrelationID";

	public static final QName CORRELATION_ID = new QName(NAMESPACE, CORRELATION_ID_NAME);

	@Override
	public Assertion build(Element element, AssertionBuilderFactory factory)
			throws IllegalArgumentException {
        return new CorrelationIDAssertion(element);
	}

	@Override
	public QName[] getKnownElements() {
		return new QName[]{CORRELATION_ID};
	}
}
