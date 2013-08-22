package org.talend.esb.policy.samenabling;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.neethi.Assertion;
import org.apache.neethi.PolicyComponent;
import org.w3c.dom.Element;

public class SamEnablingPolicy implements Assertion {

    public enum ApplyToType {
        consumer, provider, always;
    }

    private ApplyToType applyToType = ApplyToType.provider;

    public SamEnablingPolicy(Element element) {

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
        return SamEnablingPolicyBuilder.SAM_ENABLE;
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
        String prefix = writer.getPrefix(SamEnablingPolicyBuilder.NAMESPACE);

        if (prefix == null) {
            prefix = "tpa";
            writer.setPrefix(prefix, SamEnablingPolicyBuilder.NAMESPACE);
        }

        writer.writeStartElement(prefix,
                SamEnablingPolicyBuilder.SAM_ENABLE_NAME,
                SamEnablingPolicyBuilder.NAMESPACE);

        writer.writeNamespace(prefix, SamEnablingPolicyBuilder.NAMESPACE);

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
