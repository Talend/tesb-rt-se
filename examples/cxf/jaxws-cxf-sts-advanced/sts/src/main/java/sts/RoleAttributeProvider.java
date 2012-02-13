/**
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */
package sts;

import java.security.Principal;
import java.util.Collections;

import org.apache.cxf.sts.token.provider.AttributeStatementProvider;
import org.apache.cxf.sts.token.provider.TokenProviderParameters;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.saml.ext.bean.AttributeBean;
import org.apache.ws.security.saml.ext.bean.AttributeStatementBean;

/**
 * A custom AttributeStatementProvider that adds a "role" attribute depending on the client principal.
 */
public class RoleAttributeProvider implements AttributeStatementProvider {

    public AttributeStatementBean getStatement(TokenProviderParameters providerParameters) {
        Principal principal = providerParameters.getPrincipal();
        String role = "authenticated-user";
        if (principal.getName().contains("CN=Carl Client")) {
            role = "doubleit-user";
        }
            
        AttributeBean attributeBean = new AttributeBean();
        String tokenType = providerParameters.getTokenRequirements().getTokenType();
        if (WSConstants.WSS_SAML2_TOKEN_TYPE.equals(tokenType)
            || WSConstants.SAML2_NS.equals(tokenType)) {
            attributeBean.setQualifiedName("role");
            attributeBean.setNameFormat("http://schemas.xmlsoap.org/ws/2005/05/identity/claims");
        } else {
            attributeBean.setSimpleName("role");
            attributeBean.setQualifiedName("http://schemas.xmlsoap.org/ws/2005/05/identity/claims");
        }
        attributeBean.setAttributeValues(Collections.singletonList(role));

        AttributeStatementBean attributeStatementBean = new AttributeStatementBean();
        attributeStatementBean.setSamlAttributes(Collections.singletonList(attributeBean));

        return attributeStatementBean;
    }

}
