package org.talend.esb.policy.samenable;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.neethi.Assertion;
import org.apache.neethi.PolicyComponent;
import org.w3c.dom.Element;

public class SamEnablePolicy implements Assertion {

    public enum ApplyToType {
        consumer, provider, always;
    }

    private ApplyToType applyToType = ApplyToType.provider;

    public SamEnablePolicy(Element element) {

        if (element.hasAttributeNS(null, "applyTo")) {
            String target = element.getAttributeNS(null, "applyTo");
            applyToType = ApplyToType.valueOf(target);
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
        return SamEnablePolicyBuilder.SAM_ENABLE;
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
        String prefix = writer.getPrefix(SamEnablePolicyBuilder.NAMESPACE);

        if (prefix == null) {
            prefix = "tpa";
            writer.setPrefix(prefix, SamEnablePolicyBuilder.NAMESPACE);
        }

        writer.writeStartElement(prefix,
                SamEnablePolicyBuilder.SAM_ENABLE_NAME,
                SamEnablePolicyBuilder.NAMESPACE);

        writer.writeNamespace(prefix, SamEnablePolicyBuilder.NAMESPACE);

        writer.writeAttribute(null, "applyTo", applyToType.name());

        writer.writeEndElement();
    }

    @Override
    public PolicyComponent normalize() {
        return this;
    }

    public ApplyToType getApplyToType() {
        return applyToType;
    }

}
