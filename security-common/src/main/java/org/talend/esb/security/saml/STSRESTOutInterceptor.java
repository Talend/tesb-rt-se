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
package org.talend.esb.security.saml;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageUtils;
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
 * It then stores the DOM Element on the message context so that the REST Saml interceptors
 * can retrieve it and send it in a form, header, etc.
 * It caches a token and attempts to "renew" it if it has expired.
 */
public class STSRESTOutInterceptor extends AbstractPhaseInterceptor<Message> {

	private static final Logger LOG = LogUtils.getL7dLogger(STSRESTOutInterceptor.class);

    private STSClient stsClient;
    protected SecurityToken securityToken;

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

        // See if we have a cached token
        if (securityToken != null && !securityToken.isExpired()
            && securityToken.getToken() != null) {
            message.put(SAMLConstants.SAML_TOKEN_ELEMENT, securityToken.getToken());
            return;
        }

        if (stsClient == null) {
            return;
        }

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
        String appliesTo = o == null ? null : o.toString();
        // TODO - Enable once we pick up CXF 3.1.15+ (https://issues.apache.org/jira/browse/CXF-7588)
        //appliesTo = appliesTo == null
        //		? message.getContextualProperty(Message.ENDPOINT_ADDRESS).toString()
        //				: appliesTo;

        stsClient.setMessage(message);

        try {
            SecurityToken tok = null;
            if (securityToken == null || !stsClient.isAllowRenewing()) {
                tok = stsClient.requestSecurityToken(appliesTo);
            } else {
                tok = renewToken(securityToken, message, appliesTo);
            }
            securityToken = tok;
        } catch (RuntimeException ex) {
        	securityToken = null;
            throw new Fault(ex);
        } catch (Exception ex) {
        	securityToken = null;
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            throw new Fault(new RuntimeException(ex.getMessage() + ", stacktrace: " + sw.toString()));
        }

        if (securityToken != null && securityToken.getToken() != null) {
            message.put(SAMLConstants.SAML_TOKEN_ELEMENT, securityToken.getToken());
        }
    }

    private SecurityToken renewToken(
    	SecurityToken tok, Message message, String appliesTo
    ) throws Exception {
    	boolean issueAfterFailedRenew =
            MessageUtils.getContextualBoolean(
                message, SecurityConstants.STS_ISSUE_AFTER_FAILED_RENEW, true
            );
    	try {
    		return stsClient.renewSecurityToken(tok);
    	} catch (RuntimeException ex) {
            LOG.log(Level.WARNING, "Error renewing a token", ex);
            if (issueAfterFailedRenew) {
                // Perhaps the STS does not support renewing, so try to issue a new token
                return stsClient.requestSecurityToken(appliesTo);
            }
            throw ex;
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "Error renewing a token", ex);
            if (issueAfterFailedRenew) {
            	// Perhaps the STS does not support renewing, so try to issue a new token
                return stsClient.requestSecurityToken(appliesTo);
            }
            throw ex;
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
