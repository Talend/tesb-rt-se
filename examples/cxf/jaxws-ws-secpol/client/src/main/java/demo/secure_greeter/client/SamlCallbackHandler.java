/*
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */
package demo.secure_greeter.client;

import java.io.IOException;
import java.util.Collections;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.ws.security.saml.ext.SAMLCallback;
import org.apache.ws.security.saml.ext.bean.AttributeBean;
import org.apache.ws.security.saml.ext.bean.AttributeStatementBean;
import org.apache.ws.security.saml.ext.bean.SubjectBean;
import org.apache.ws.security.saml.ext.builder.SAML2Constants;
import org.opensaml.common.SAMLVersion;

/**
 * A CallbackHandler instance used to create a simple SAML 2.0 Assertion. This assertion will
 * be added to the outbound security header of the client request. As it uses a subject
 * confirmation method of "Sender Vouches", it conveys to the Web Service Provider that the
 * client has authenticated an external user in some way (not shown as part of this sample), 
 * and has assigned the attribute role of "authenticated-client" to the external user. The
 * assertion that will be generated from this CallbackHandler instance will be signed by the 
 * client, as per the policy definition ("SignedSupportingTokens").
 */
public class SamlCallbackHandler implements CallbackHandler {

    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof SAMLCallback) {
                SAMLCallback callback = (SAMLCallback) callbacks[i];
                callback.setSamlVersion(SAMLVersion.VERSION_20);
                callback.setIssuer("alice");
                String subjectName = "uid=auth_client";
                SubjectBean subjectBean = 
                    new SubjectBean(
                        subjectName, null, SAML2Constants.CONF_SENDER_VOUCHES
                    );
                callback.setSubject(subjectBean);
                
                AttributeStatementBean attrBean = new AttributeStatementBean();
                if (subjectBean != null) {
                    attrBean.setSubject(subjectBean);
                }
                AttributeBean attributeBean = new AttributeBean();
                attributeBean.setSimpleName("attribute-role");
                attributeBean.setAttributeValues(Collections.singletonList("authenticated-client"));
                attrBean.setSamlAttributes(Collections.singletonList(attributeBean));
                callback.setAttributeStatementData(Collections.singletonList(attrBean));
            }
        }
    }
    
}
