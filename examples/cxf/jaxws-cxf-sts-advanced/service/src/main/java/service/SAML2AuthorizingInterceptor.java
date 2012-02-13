/**
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */
package service;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.apache.cxf.interceptor.security.SimpleAuthorizingInterceptor;
import org.apache.cxf.security.SecurityContext;
import org.apache.ws.security.SAMLTokenPrincipal;
import org.apache.ws.security.saml.ext.AssertionWrapper;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AttributeStatement;
import org.w3c.dom.Element;


public class SAML2AuthorizingInterceptor extends SimpleAuthorizingInterceptor {

    @Override 
    protected boolean isUserInRole(SecurityContext sc, List<String> roles, boolean deny) {
        Principal principal = sc.getUserPrincipal();
        if (principal instanceof SAMLTokenPrincipal) {
            SAMLTokenPrincipal samlPrincipal = (SAMLTokenPrincipal)principal;
            AssertionWrapper assertion = samlPrincipal.getToken();
            String role = getRoleFromAssertion(assertion);
            if (roles.contains(role)) {
                return true;
            }
        }
        
        return false;
    }
    
    public void setMethodRolesMap(Map<String, String> rolesMap) {
        super.setMethodRolesMap(rolesMap);
    }
    
    private String getRoleFromAssertion(AssertionWrapper assertion) {
        Assertion saml2Assertion = assertion.getSaml2();
        if (saml2Assertion == null) {
            return null;
        }
        
        List<AttributeStatement> attributeStatements = saml2Assertion.getAttributeStatements();
        if (attributeStatements == null || attributeStatements.isEmpty()) {
            return null;
        }
        
        String nameFormat = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims";
        for (AttributeStatement statement : attributeStatements) {
            List<Attribute> attributes = statement.getAttributes();
            for (Attribute attribute : attributes) {
                if ("role".equals(attribute.getName()) 
                    && nameFormat.equals(attribute.getNameFormat())) {
                    Element attributeValueElement = attribute.getAttributeValues().get(0).getDOM();
                    return attributeValueElement.getTextContent();
                }
            }
        }
        return null;
    }
    
}
