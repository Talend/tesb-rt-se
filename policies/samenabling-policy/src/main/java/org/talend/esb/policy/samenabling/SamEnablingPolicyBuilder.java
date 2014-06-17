package org.talend.esb.policy.samenabling;

import javax.xml.namespace.QName;

import org.apache.neethi.Assertion;
import org.apache.neethi.AssertionBuilderFactory;
import org.apache.neethi.builders.AssertionBuilder;
import org.w3c.dom.Element;

public class SamEnablingPolicyBuilder implements AssertionBuilder<Element> {
    public static final String NAMESPACE = "http://types.talend.com/policy/assertion/1.0";

    public static final String SAM_ENABLE_NAME = "ServiceActivityMonitoring";

    public static final QName SAM_ENABLE = new QName(NAMESPACE, SAM_ENABLE_NAME);

    @Override
    public Assertion build(Element element, AssertionBuilderFactory factory)
            throws IllegalArgumentException {
        return new SamEnablingPolicy(element);
    }

    @Override
    public QName[] getKnownElements() {
        return new QName[] { SAM_ENABLE };
    }
}
