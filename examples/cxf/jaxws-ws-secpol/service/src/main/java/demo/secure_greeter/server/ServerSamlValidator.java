/**
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */
package demo.secure_greeter.server;

import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.handler.RequestData;
import org.apache.ws.security.saml.ext.AssertionWrapper;
import org.apache.ws.security.saml.ext.OpenSAMLUtil;
import org.apache.ws.security.validate.Credential;
import org.apache.ws.security.validate.SamlAssertionValidator;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AttributeStatement;
import org.opensaml.xml.XMLObject;
import org.w3c.dom.Element;

/**
 * A validator for a SAML Assertion. It checks the issuer name, the confirmation method, and
 * checks to see that it contains an Attribute with a value of "authenticated-client".
 */
public class ServerSamlValidator extends SamlAssertionValidator {
    
    @Override
    public Credential validate(Credential credential, RequestData data) throws WSSecurityException {
        Credential validatedCredential = super.validate(credential, data);
        AssertionWrapper assertion = validatedCredential.getAssertion();
        
        if (!"alice".equals(assertion.getIssuerString())) {
            throw new WSSecurityException(WSSecurityException.FAILURE, "invalidSAMLsecurity");
        }
        
        String confirmationMethod = assertion.getConfirmationMethods().get(0);
        if (!OpenSAMLUtil.isMethodSenderVouches(confirmationMethod)) {
            throw new WSSecurityException(WSSecurityException.FAILURE, "invalidSAMLsecurity");
        }
        
        Assertion saml2Assertion = assertion.getSaml2();
        if (saml2Assertion == null) {
            throw new WSSecurityException(WSSecurityException.FAILURE, "invalidSAMLsecurity");
        }
        
        boolean authenticatedClient = false;
        for (AttributeStatement attributeStatement : saml2Assertion.getAttributeStatements()) {
            for (Attribute attribute : attributeStatement.getAttributes()) {
                if (!"attribute-role".equals(attribute.getFriendlyName())) {
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
            throw new WSSecurityException(WSSecurityException.FAILURE, "invalidSAMLsecurity");
        }
        
        return validatedCredential;
    }

}
