package org.talend.esb.policy.schemavalidate;

import javax.xml.namespace.QName;

import org.apache.neethi.Assertion;
import org.apache.neethi.AssertionBuilderFactory;
import org.apache.neethi.builders.AssertionBuilder;
import org.w3c.dom.Element;

public class SchemaValidationPolicyBuilder implements AssertionBuilder<Element> {
	public static final String NAMESPACE = "http://types.talend.com/policy/assertion/1.0";

	public static final String SCHEMA_VALIDATION_NAME = "SchemaValidation";

	public static final QName SCHEMA_VALIDATION = new QName(NAMESPACE, SCHEMA_VALIDATION_NAME);

	@Override
	public Assertion build(Element element, AssertionBuilderFactory factory)
			throws IllegalArgumentException {
        return new SchemaValidationPolicy(element);
	}

	@Override
	public QName[] getKnownElements() {
		return new QName[]{SCHEMA_VALIDATION};
	}
}
