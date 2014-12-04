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

    private static final String PATH_ATTRIBUTE_NAME         = "path";
    private static final String APPLIES_TO_ATTRIBUTE_NAME   = "appliesTo";
    private static final String MESSAGE_TYPE_ATTRIBUTE_NAME = "message";


    public enum AppliesToType {
        consumer,
        provider,
        always,
        none
    }


    public enum MessageType {
        request,
        response,
        all,
        none
    }


    private final String path;
    private final TransformationType transformationType;
    private final AppliesToType appliesTo;
    private final MessageType messageType;

    public TransformationAssertion(Element element) {
          String sType = element.getAttribute(TYPE_NAME);
          if ((sType == null) || (sType.isEmpty())) {
              transformationType = TransformationType.xslt;
          } else {
              transformationType = TransformationType.valueOf(sType);
          }

          path = element.getAttribute(PATH_ATTRIBUTE_NAME);
          appliesTo = AppliesToType.valueOf(element.getAttribute(APPLIES_TO_ATTRIBUTE_NAME));
          messageType = MessageType.valueOf(element.getAttribute(MESSAGE_TYPE_ATTRIBUTE_NAME));
    }

    public TransformationAssertion(String path,
                                   AppliesToType appliesTo,
                                   MessageType messageType,
                                   TransformationType transformationType) {
        this.path = path;
        this.appliesTo = appliesTo;
        this.messageType = messageType;
        this.transformationType = transformationType;
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
        writer.writeAttribute(null, PATH_ATTRIBUTE_NAME, String.valueOf(path));
        writer.writeAttribute(null, APPLIES_TO_ATTRIBUTE_NAME, String.valueOf(appliesTo));
        writer.writeAttribute(null, MESSAGE_TYPE_ATTRIBUTE_NAME, String.valueOf(messageType));


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

    public String getPath() {
         return path;
     }

     public AppliesToType getAppliesTo() {
         return appliesTo;
     }

     public MessageType getMessageType() {
         return messageType;
     }
}
