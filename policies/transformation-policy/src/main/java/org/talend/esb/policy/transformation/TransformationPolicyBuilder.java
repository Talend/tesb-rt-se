package org.talend.esb.policy.transformation;

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

public class TransformationPolicyBuilder implements AssertionBuilder<Element> {

    public static final String NAMESPACE = "http://types.talend.com/policy/assertion/1.0";
    public static final String TRANSFORMATION_NAME = "Transformation";
    public static final QName TRANSFORMATION = new QName(NAMESPACE, TRANSFORMATION_NAME);

    @Override
    public Assertion build(Element element, AssertionBuilderFactory factory)
            throws IllegalArgumentException {
        return new TransformationAssertion(element);
    }

    @Override
    public QName[] getKnownElements() {
        return new QName[]{TRANSFORMATION};
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
                    .get(TransformationPolicyBuilder.TRANSFORMATION);

            if (ais == null) {
                return null;
            }

            for (AssertionInfo ai : ais) {
                if (ai.getAssertion() instanceof TransformationAssertion) {
                    return ai;
                }
            }
        }

        return null;
    }
}
