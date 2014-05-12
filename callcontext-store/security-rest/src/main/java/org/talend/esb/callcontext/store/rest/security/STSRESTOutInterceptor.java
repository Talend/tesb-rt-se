/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.talend.esb.callcontext.store.rest.security;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.rs.security.saml.SAMLConstants;
import org.apache.cxf.rs.security.saml.SamlFormOutInterceptor;
import org.apache.cxf.rs.security.saml.SamlHeaderOutInterceptor;
import org.apache.cxf.ws.security.SecurityConstants;
import org.apache.cxf.ws.security.tokenstore.SecurityToken;
import org.apache.cxf.ws.security.trust.STSClient;

/**
 * An outbound REST interceptor which uses the STSClient to obtain a token from a STS.
 * It then stores the DOM Element on the message context so that
 * the REST SAML interceptors can retrieve it and send it in a form, header, etc.
 * It caches a token and attempts to "renew" it if it has expired.
 */
public class STSRESTOutInterceptor extends AbstractPhaseInterceptor<Message> {

    private STSClient stsClient;
    private SecurityToken securityToken;

    public STSRESTOutInterceptor() {
        super(Phase.WRITE);
        addBefore(SamlFormOutInterceptor.class.getName());
        addBefore(SamlHeaderOutInterceptor.class.getName());
    }

    @Override
    public void handleMessage(Message message) throws Fault {

        if (!isRequestor(message)) {
            return;
        }

        if (null != securityToken && !securityToken.isExpired() && null != securityToken.getToken()) {
            message.setContextualProperty(SAMLConstants.SAML_TOKEN_ELEMENT, securityToken.getToken());
            return;
        }

        if (null == stsClient) {
            return;
        }

        try {
            // Transpose ActAs/OnBehalfOf info from original request to the STS client.
            Object token = message.getContextualProperty(SecurityConstants.STS_TOKEN_ACT_AS);
            if (token != null) {
                stsClient.setActAs(token);
            }

            token = message.getContextualProperty(SecurityConstants.STS_TOKEN_ON_BEHALF_OF);
            if (token != null) {
                stsClient.setOnBehalfOf(token);
            }

            Object o = message.getContextualProperty(SecurityConstants.STS_APPLIES_TO);
            String appliesTo = null == o ? null : o.toString();
            appliesTo = null == appliesTo
                ? message.getContextualProperty(Message.ENDPOINT_ADDRESS).toString()
                    : appliesTo;

            stsClient.setMessage(message);

            SecurityToken tok = null;
            if (null == securityToken) {
                tok = stsClient.requestSecurityToken(appliesTo);
            } else {
                tok = stsClient.renewSecurityToken(securityToken);
            }
            securityToken = tok;

            if (null != securityToken && !securityToken.isExpired() && null != securityToken.getToken()) {
                message.setContextualProperty(SAMLConstants.SAML_TOKEN_ELEMENT, securityToken.getToken());
                return;
            }
        } catch (RuntimeException ex) {
            throw new Fault(ex);
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            throw new Fault(new RuntimeException(ex.getMessage() + ", stacktrace: " + sw.toString()));
        }
    }

    public STSClient getStsClient() {
        return stsClient;
    }

    /**
     * Set the STSClient object. This does the heavy lifting to get a (SAML) Token from the STS.
     * @param stsClient the STSClient object.
     */
    public void setStsClient(STSClient stsClient) {
        this.stsClient = stsClient;
    }
}
