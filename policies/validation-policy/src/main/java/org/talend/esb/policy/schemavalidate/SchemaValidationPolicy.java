package org.talend.esb.policy.schemavalidate;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.neethi.Assertion;
import org.apache.neethi.PolicyComponent;
import org.w3c.dom.Element;

public class SchemaValidationPolicy implements Assertion {


    public enum ValidationType {

        WSDLSchema("WSDLSchema"),
        CustomSchema("CustomSchema");


        private String symbolicName;

        private ValidationType(String symbolicName) {
            this.symbolicName = symbolicName;
        }

        public String getSymbolicName() {
            return symbolicName;
        }
    }


    public enum AppliesToType {

        consumer("consumer"),
        provider("provider"),
        always("always"),
        none("none");


        private String symbolicName;

        private AppliesToType(String symbolicName) {
            this.symbolicName = symbolicName;
        }

        public String getSymbolicName() {
            return symbolicName;
        }
    }


    public enum MessageType {

        request("request"),
        response("response"),
        all("all"),
        none("none");


        private String symbolicName;

        private MessageType(String symbolicName) {
            this.symbolicName = symbolicName;
        }

        public String getSymbolicName() {
            return symbolicName;
        }
    }

    //by default using schema embedded in the wsdl to do the validation,
    //currently, only support "WSDLSchema"
    private ValidationType validationType = ValidationType.WSDLSchema;
    //by default no validate on both side
    private AppliesToType appliesToType = AppliesToType.none;
    //by default no validate for request/response
    private MessageType messageType = MessageType.none;
    // by default path to external xsd is not set
    private String customSchemaPath = null;

    public SchemaValidationPolicy(Element element) {
        if (element.hasAttributeNS(null, "type")) {
            String type = element.getAttributeNS(null, "type");
            validationType = ValidationType.valueOf(type);
        }

        if (element.hasAttributeNS(null, "appliesTo")) {
            String applyTo = element.getAttributeNS(null, "appliesTo");
            appliesToType = AppliesToType.valueOf(applyTo);
        }

        if (element.hasAttributeNS(null, "message")) {
            String message = element.getAttributeNS(null, "message");
            messageType = MessageType.valueOf(message);
        }

        if (element.hasAttributeNS(null, "schemaPath")) {
            validationType = ValidationType.CustomSchema;
            customSchemaPath = element.getAttributeNS(null, "schemaPath");
        }

    }


    public SchemaValidationPolicy() {

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
        return SchemaValidationPolicyBuilder.SCHEMA_VALIDATION;
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
        String prefix = writer.getPrefix(SchemaValidationPolicyBuilder.NAMESPACE);

        if (prefix == null) {
            prefix = "tpa";
            writer.setPrefix(prefix, SchemaValidationPolicyBuilder.NAMESPACE);
        }

        // <tpa:SchemaValidation>
        writer.writeStartElement(prefix, SchemaValidationPolicyBuilder.SCHEMA_VALIDATION_NAME,
                SchemaValidationPolicyBuilder.NAMESPACE);

        // xmlns:tpa="http://types.talend.com/policy/assertion/1.0"
        writer.writeNamespace(prefix, SchemaValidationPolicyBuilder.NAMESPACE);

        // attributes
        writer.writeAttribute(null, "type", validationType.name());
        writer.writeAttribute(null, "appliesTo", appliesToType.name());
        writer.writeAttribute(null, "message", messageType.name());
        if(validationType == ValidationType.CustomSchema){
            writer.writeAttribute(null, "schemaPath", customSchemaPath);
        }

        // </tpa:SchemaValidation>
        writer.writeEndElement();
    }

    @Override
    public PolicyComponent normalize() {
        return this;
    }

    public ValidationType getValidationType() {
        return validationType;
    }

    public void setValidationType(ValidationType validationType) {
        this.validationType = validationType;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public AppliesToType getApplyToType() {
        return appliesToType;
    }

    public void setAppliesToType(AppliesToType appliesToType) {
        this.appliesToType = appliesToType;
    }

    public String getCustomSchemaPath() {
        return customSchemaPath;
    }

    public void setCustomSchemaPath(String customSchemaPath) {
        this.customSchemaPath = customSchemaPath;
    }

}
