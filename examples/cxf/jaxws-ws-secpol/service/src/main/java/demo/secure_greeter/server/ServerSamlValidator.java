/**
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */
package demo.secure_greeter.server;

import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.dom.handler.RequestData;
import org.apache.wss4j.common.saml.SamlAssertionWrapper;
import org.apache.wss4j.common.saml.OpenSAMLUtil;
import org.apache.wss4j.dom.validate.Credential;
import org.apache.wss4j.dom.validate.SamlAssertionValidator;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.opensaml.core.xml.XMLObject;
import org.w3c.dom.Element;

/**
 * A validator for a SAML Assertion. It checks the issuer name, the confirmation method, and
 * checks to see that it contains an Attribute with a value of "authenticated-client".
 */
public class ServerSamlValidator extends SamlAssertionValidator {
    
    @Override
    public Credential validate(Credential credential, RequestData data) throws WSSecurityException {
        Credential validatedCredential = super.validate(credential, data);
        SamlAssertionWrapper assertion = validatedCredential.getSamlAssertion();
        
        if (!"alice".equals(assertion.getIssuerString())) {
            throw new WSSecurityException(WSSecurityException.ErrorCode.FAILURE, "invalidSAMLsecurity");
        }
        
        String confirmationMethod = assertion.getConfirmationMethods().get(0);
        if (!OpenSAMLUtil.isMethodSenderVouches(confirmationMethod)) {
            throw new WSSecurityException(WSSecurityException.ErrorCode.FAILURE, "invalidSAMLsecurity");
        }
        
        Assertion saml2Assertion = assertion.getSaml2();
        if (saml2Assertion == null) {
            throw new WSSecurityException(WSSecurityException.ErrorCode.FAILURE, "invalidSAMLsecurity");
        }
        
        boolean authenticatedClient = false;
        for (AttributeStatement attributeStatement : saml2Assertion.getAttributeStatements()) {
            for (Attribute attribute : attributeStatement.getAttributes()) {
                if (!"attribute-role".equals(attribute.getName())) {
                    continue;
                }
                for (XMLObject attributeValue : attribute.getAttributeValues()) {
                    Element attributeValueElement = attributeValue.getDOM();
                    String text = attributeValueElement.getTextContent();
                    if ("authenticated-client".equals(text)) {
                        authenticatedClient = true;
                    }
                }
            }
        }
        
        if (!authenticatedClient) {
            throw new WSSecurityException(WSSecurityException.ErrorCode.FAILURE, "invalidSAMLsecurity");
        }
        
        return validatedCredential;
    }

}
