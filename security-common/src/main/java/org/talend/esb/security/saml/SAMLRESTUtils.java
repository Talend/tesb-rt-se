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

import java.util.Map;

import org.apache.cxf.jaxrs.AbstractJAXRSFactoryBean;
import org.apache.cxf.rs.security.saml.SamlHeaderInHandler;
import org.apache.cxf.rs.security.saml.SamlHeaderOutInterceptor;
import org.apache.cxf.ws.security.SecurityConstants;
import org.apache.cxf.ws.security.trust.STSClient;

public class SAMLRESTUtils {

    public static final String SIGNATURE_PASSWORD = "security.signature.password";

    private static final String SAML2_TOKEN_TYPE =
            "http://docs.oasis-open.org/wss/oasis-wss-saml-token-profile-1.1#SAMLV2.0";
    private static final String BEARER_KEYTYPE =
            "http://docs.oasis-open.org/ws-sx/ws-trust/200512/Bearer";

    private SAMLRESTUtils() {
    }

    public static void configureClient(final AbstractJAXRSFactoryBean clientFactory,
            final STSClient stsClient) {

        stsClient.setAllowRenewingAfterExpiry(true);
        stsClient.setEnableLifetime(true);
        stsClient.setTokenType(SAML2_TOKEN_TYPE);
        stsClient.setKeyType(BEARER_KEYTYPE);

        STSRESTOutInterceptor outInterceptor = new STSRESTOutInterceptor();
        outInterceptor.setStsClient(stsClient);
        clientFactory.getOutInterceptors().add(outInterceptor);
        clientFactory.getOutInterceptors().add(new SamlHeaderOutInterceptor());
    }

    public static void configureServer(final AbstractJAXRSFactoryBean serverFactory,
            final Map<String, Object> securityProps) {

        Map<String, Object> props = serverFactory.getProperties(true);

        String username = (String) securityProps.get(SecurityConstants.SIGNATURE_USERNAME);
        props.put(SecurityConstants.SIGNATURE_USERNAME, username);
        props.put(SecurityConstants.CALLBACK_HANDLER,
                new WSPasswordCallbackHandler(username, (String) securityProps.get(SIGNATURE_PASSWORD)));
        props.put(SecurityConstants.SIGNATURE_PROPERTIES,
                securityProps.get(SecurityConstants.SIGNATURE_PROPERTIES));

        serverFactory.setProvider(new SamlHeaderInHandler());
    }
}
